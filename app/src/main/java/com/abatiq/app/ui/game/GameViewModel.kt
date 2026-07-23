package com.abatiq.app.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abatiq.app.audio.GameAudioPlayer
import com.abatiq.app.audio.GamePrompt
import com.abatiq.app.audio.PlaybackState
import com.abatiq.app.data.domain.Attempt
import com.abatiq.app.data.domain.BalloonField
import com.abatiq.app.data.domain.GameSession
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
 * Owns the 60-second floating-balloon session. Drives the countdown timer and
 * the field simulation, dispatches every tap through [GameSession] for the
 * scoring rules, and hands a single [GameUiState] to the renderer.
 *
 * Tap rules (owner-approved Phase 1 brief):
 *   - Correct tap → +10 score, a short positive spoken line, the next target
 *     letter is chosen immediately, and that new target is spoken.
 *   - Wrong tap → 0 score, a short gentle spoken line, the next target letter
 *     is chosen immediately, and that new target is spoken.
 *   - The background field never pauses — balloons keep rising.
 *   - When the timer reaches 0 the session is finalised and [result] is emitted.
 */
class GameViewModel(
    private val gameRepository: GameRepository,
    private val audioPlayer: GameAudioPlayer,
    private val random: Random = Random.Default,
    private val clock: () -> Long = { System.currentTimeMillis() },
    /** Granularity of the visible countdown in milliseconds. */
    private val tickIntervalMillis: Long = 100L,
    /** Default play area used until the screen reports its measured size. */
    initialPlayArea: PlayArea = PlayArea.Placeholder,
) : ViewModel() {

    private val _state = MutableStateFlow(
        GameUiState(remainingSeconds = GameSession.DEFAULT_DURATION_SECONDS),
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _result = MutableStateFlow<GameResult?>(null)
    val result: StateFlow<GameResult?> = _result.asStateFlow()

    private var session: GameSession? = null
    private val field: BalloonField = BalloonField(random = random)
    private var area: PlayArea = initialPlayArea

    private var timerJob: Job? = null
    private var populated: Boolean = false
    private var areaMeasured: Boolean = false

    init {
        viewModelScope.launch {
            val handle = gameRepository.startSession()
            session = handle.session
            _state.update {
                it.copy(
                    targetLetter = handle.session.targetLetter,
                    remainingSeconds = handle.session.durationSeconds,
                    isCompleted = false,
                    score = 0,
                    correctTaps = 0,
                    totalTaps = 0,
                )
            }
            populateFieldIfReady()
            // Speak the first target as soon as the audio engine is ready.
            audioPlayer.play(GamePrompt.TargetLetter(handle.session.targetLetter))
            startTimer()
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
     * Called by the Composable once the play area has been measured. Spawns
     * the initial balloon field with the current target letter on screen.
     */
    fun setPlayArea(area: PlayArea) {
        if (area == PlayArea.Placeholder) return
        this.area = area
        areaMeasured = true
        field.updateMetrics(area.balloonWidthPx, area.balloonHeightPx)
        populateFieldIfReady()
    }

    private fun populateFieldIfReady() {
        val current = session ?: return
        if (!areaMeasured || populated) return
        populated = true
        field.populate(current.targetLetter, area.width, area.height)
        _state.update { it.copy(balloons = field.balloons.toList()) }
    }

    /**
     * Tap was registered on the balloon with the given [id] and [letter].
     */
    fun onBalloonTapped(id: Long, letter: String) {
        val current = session ?: return
        if (_state.value.isCompleted) return
        val previousTarget = current.targetLetter
        val isCorrect = letter == previousTarget
        if (field.balloons.none { it.id == id }) return
        field.replaceBatchAfterTap(fieldHeight = area.height)
        val outcome = current.tap(letter, field.visibleLetters)
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
        _state.update {
            it.copy(
                balloons = field.balloons.toList(),
                targetLetter = outcome.nextTarget,
                score = current.score,
                correctTaps = current.correctTaps,
                totalTaps = current.totalTaps,
                feedbackPulse = if (isCorrect) {
                    com.abatiq.app.data.domain.FeedbackPulse.Correct
                } else {
                    com.abatiq.app.data.domain.FeedbackPulse.Gentle
                },
                pulsedBalloonId = null,
            )
        }
        // The newest target is the most important audio cue. Feedback remains
        // visual so it never delays or overwrites the next instruction.
        audioPlayer.play(
            if (outcome.isCorrect) {
                GamePrompt.CorrectThenTarget(outcome.nextTarget)
            } else {
                GamePrompt.GentleThenTarget(outcome.nextTarget)
            },
        )
    }

    /** Replay the current target letter through the audio abstraction. */
    fun replayTarget() {
        val target = _state.value.targetLetter
        if (target.isNotEmpty()) {
            audioPlayer.play(GamePrompt.TargetLetter(target))
        }
    }

    /** Called by the Composable each frame to advance the simulation. */
    fun onFrame(deltaSeconds: Float) {
        if (_state.value.isCompleted) return
        val current = session ?: return
        if (!populated) return
        field.step(deltaSeconds)
        _state.update { it.copy(balloons = field.balloons.toList()) }
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
        audioPlayer.stop()
    }

    /** Composable-provided play area dimensions in pixels. */
    data class PlayArea(
        val width: Float,
        val height: Float,
        val balloonWidthPx: Float = 200f,
        val balloonHeightPx: Float = 240f,
    ) {
        companion object {
            /** Used between init and the first layout pass. */
            val Placeholder = PlayArea(width = 1080f, height = 1920f)
        }
    }
}
