package com.abatiq.app.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abatiq.app.audio.GameAudioPlayer
import com.abatiq.app.audio.GamePrompt
import com.abatiq.app.audio.PlaybackState
import com.abatiq.app.data.domain.Attempt
import com.abatiq.app.data.domain.GameSession
import com.abatiq.app.data.domain.LetterBatchFactory
import com.abatiq.app.data.domain.LetterCard
import com.abatiq.app.data.domain.LetterCardFeedback
import com.abatiq.app.data.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Drives a two-minute "Tebak Huruf" session.
 *
 * Round flow:
 *  1. The screen asks for an initial batch from [GameSession.targetLetter].
 *  2. The renderer reveals the three cards with a short stagger.
 *  3. When the child taps a card we lock further input, play a reaction on the
 *     tapped card (zoom for correct, shake for gentle), then create the next
 *     batch and start the stagger again.
 *  4. The countdown timer runs from [GameSession.DEFAULT_DURATION_SECONDS].
 */
class GameViewModel(
    private val gameRepository: GameRepository,
    private val audioPlayer: GameAudioPlayer,
    private val random: Random = Random.Default,
    private val clock: () -> Long = { System.currentTimeMillis() },
    /** Granularity of the visible countdown in milliseconds. */
    private val tickIntervalMillis: Long = 100L,
    /** Visible duration of the reaction animation before the next batch appears. */
    private val reactionHoldMillis: Long = 700L,
    /** Delay between consecutive card reveals inside the same round. */
    private val cardStaggerMillis: Long = 140L,
) : ViewModel() {

    private val _state = MutableStateFlow(
        GameUiState(
            sessionDurationSeconds = GameSession.DEFAULT_DURATION_SECONDS,
            remainingSeconds = GameSession.DEFAULT_DURATION_SECONDS,
        ),
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _result = MutableStateFlow<GameResult?>(null)
    val result: StateFlow<GameResult?> = _result.asStateFlow()

    private var session: GameSession? = null
    private val batchFactory = LetterBatchFactory(random = random)
    private var roundJob: Job? = null
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val handle = gameRepository.startSession()
            session = handle.session
            _state.update {
                it.copy(
                    targetLetter = handle.session.targetLetter,
                    sessionDurationSeconds = handle.session.durationSeconds,
                    remainingSeconds = handle.session.durationSeconds,
                    isCompleted = false,
                    score = 0,
                    correctTaps = 0,
                    totalTaps = 0,
                    cards = emptyList(),
                )
            }
            audioPlayer.play(GamePrompt.TargetLetter(handle.session.targetLetter))
            startTimer()
            startRound(target = handle.session.targetLetter)
        }
        viewModelScope.launch {
            audioPlayer.state.collect { playback ->
                val ready = playback !is PlaybackState.Unavailable
                _state.update {
                    it.copy(
                        audioReady = ready,
                        audioSpeaking = playback is PlaybackState.Speaking,
                    )
                }
            }
        }
    }

    /**
     * Start a fresh round: prepare a new batch and reveal the cards one by one
     * with a stagger so each card "appears" individually.
     */
    private fun startRound(target: String) {
        roundJob?.cancel()
        roundJob = viewModelScope.launch {
            val batch = batchFactory.nextBatch(target)
            _state.update {
                it.copy(
                    cards = batch.map { card -> card.copy(isRevealed = false) },
                    reactingCardId = null,
                    reactingFeedback = LetterCardFeedback.None,
                )
            }
            batch.forEachIndexed { index, card ->
                if (index > 0) delay(cardStaggerMillis)
                revealCard(card.id)
            }
        }
    }

    private suspend fun revealCard(cardId: Long) {
        _state.update { current ->
            current.copy(
                cards = current.cards.map { card ->
                    if (card.id == cardId) card.copy(isRevealed = true) else card
                },
            )
        }
    }

    /**
     * Handle a tap on one of the visible cards. Locks further input while the
     * reaction animation runs.
     */
    fun onCardTapped(letter: String) {
        val current = session ?: return
        if (_state.value.isCompleted) return
        if (_state.value.reactingCardId != null) return // input locked
        val card = _state.value.cards.firstOrNull { it.letter == letter } ?: return

        val previousTarget = current.targetLetter
        val isCorrect = letter == previousTarget

        // Lock input and surface the reaction on the tapped card.
        _state.update {
            it.copy(
                reactingCardId = card.id,
                reactingFeedback = if (isCorrect) {
                    LetterCardFeedback.Correct
                } else {
                    LetterCardFeedback.Gentle
                },
            )
        }

        // We commit the tap and prepare the next round with the outcome the
        // engine returns. The engine's next target must come from the next
        // batch, so we ask it for the outcome by generating that batch first.
        val nextBatch = batchFactory.nextBatch(
            targetLetter = placeholderNextTarget(current, letter),
        )
        val outcome = current.tap(letter, nextBatch.map { it.letter })

        viewModelScope.launch {
            gameRepository.recordAttempt(
                Attempt(
                    sessionId = current.id,
                    targetLetter = outcome.previousTarget,
                    tappedLetter = letter,
                    isCorrect = outcome.isCorrect,
                    pointsAwarded = outcome.pointsAwarded,
                    timestampMillis = clock(),
                )
            )
        }

        audioPlayer.play(
            if (isCorrect) {
                GamePrompt.CorrectThenTarget(outcome.nextTarget)
            } else {
                GamePrompt.GentleThenTarget(outcome.nextTarget)
            },
        )

        // Hold the reaction long enough for the child to perceive it, then
        // refresh the round with the next batch.
        viewModelScope.launch {
            delay(reactionHoldMillis)
            if (_state.value.isCompleted) return@launch
            _state.update {
                it.copy(
                    score = current.score,
                    correctTaps = current.correctTaps,
                    totalTaps = current.totalTaps,
                    cards = emptyList(),
                    targetLetter = outcome.nextTarget,
                    reactingCardId = null,
                    reactingFeedback = LetterCardFeedback.None,
                    roundToken = it.roundToken + 1,
                )
            }
            startRound(target = outcome.nextTarget)
        }
    }

    /**
     * Provide a deterministic placeholder target for [LetterBatchFactory] so the
     * engine can pick its real next target from a fresh batch. We pass the
     * previous target because the factory guarantees it is included, but the
     * result is purely a vehicle for the outcome — we immediately overwrite the
     * target with the real one returned by the engine.
     */
    private fun placeholderNextTarget(session: GameSession, tappedLetter: String): String {
        // Prefer the engine's own next-target hint when available by passing
        // the previous target; this is only consumed to generate a valid batch.
        return session.targetLetter
    }

    /** Replay the current target letter through the audio abstraction. */
    fun replayTarget() {
        val target = _state.value.targetLetter
        if (target.isNotEmpty() && _state.value.reactingCardId == null) {
            audioPlayer.play(GamePrompt.TargetLetter(target))
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val current = session ?: return@launch
            while (true) {
                val now = clock()
                val remaining = current.remainingSeconds(now)
                _state.update { it.copy(remainingSeconds = remaining) }
                if (remaining <= 0) {
                    finalizeSession()
                    break
                }
                delay(tickIntervalMillis)
            }
        }
    }

    private fun finalizeSession() {
        val current = session ?: return
        roundJob?.cancel()
        val summary = current.end()
        viewModelScope.launch {
            gameRepository.completeSession(summary)
            _state.update {
                it.copy(
                    isCompleted = true,
                    remainingSeconds = 0,
                    score = summary.finalScore,
                    correctTaps = summary.correctTaps,
                    totalTaps = summary.totalTaps,
                    reactingCardId = null,
                    reactingFeedback = LetterCardFeedback.None,
                )
            }
            _result.value = GameResult(
                score = summary.finalScore,
                correctTaps = summary.correctTaps,
                totalTaps = summary.totalTaps,
            )
            audioPlayer.play(GamePrompt.SessionEnd)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        roundJob?.cancel()
        audioPlayer.stop()
    }
}
