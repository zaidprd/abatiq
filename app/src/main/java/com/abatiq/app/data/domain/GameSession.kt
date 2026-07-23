package com.abatiq.app.data.domain

import kotlin.random.Random

/**
 * The small set of rules that govern one 120-second play session. Pulled out of
 * the ViewModel so the test suite can drive it without any Android dependency.
 *
 * Responsibilities:
 *   - Track the current target letter.
 *   - Score (correct = +10, wrong = 0, never negative).
 *   - Track correct/total tap counts for the result screen.
 *   - Detect when the timer has ended.
 *
 * Everything is deterministic given a [Random] seed and a clock.
 */
class GameSession(
    private val random: Random = Random.Default,
    private val clock: () -> Long = { System.currentTimeMillis() },
    val durationSeconds: Int = DEFAULT_DURATION_SECONDS,
    val letters: List<String> = Question.Letters,
) {

    val id: String = "s-${clock()}-${random.nextInt(1 shl 20)}"
    val startedAtEpochMs: Long = clock()

    private var _targetLetter: String = selectTargetLetter()
    val targetLetter: String get() = _targetLetter

    private var _score: Int = 0
    val score: Int get() = _score

    private var _correctTaps: Int = 0
    val correctTaps: Int get() = _correctTaps

    private var _totalTaps: Int = 0
    val totalTaps: Int get() = _totalTaps

    private var _endedAtEpochMs: Long? = null
    val endedAtEpochMs: Long? get() = _endedAtEpochMs

    private var _completed: Boolean = false
    val completed: Boolean get() = _completed

    /** Seconds remaining for the session, clamped to 0 when the timer ends. */
    fun remainingSeconds(nowEpochMs: Long): Int {
        val elapsedMs = (nowEpochMs - startedAtEpochMs).coerceAtLeast(0L)
        val remainingMs = durationSeconds * 1000L - elapsedMs
        return ((remainingMs + 999L) / 1000L).toInt().coerceAtLeast(0)
    }

    /**
     * Apply a tap once the UI has prepared the next round. The next target is
     * chosen from [nextTargetCandidates], which must contain the three letters
     * that will appear in the next batch.
     */
    fun tap(letter: String, nextTargetCandidates: List<String>): TapOutcome {
        require(nextTargetCandidates.isNotEmpty()) { "Target berikutnya harus terlihat di layar" }
        require(nextTargetCandidates.size == ROUND_SIZE) {
            "Setiap ronde menampilkan tepat $ROUND_SIZE huruf"
        }
        _totalTaps += 1
        val previousTarget = _targetLetter
        val isCorrect = letter == previousTarget
        val points = if (isCorrect) POINTS_PER_CORRECT else 0
        if (isCorrect) {
            _score += points
            _correctTaps += 1
        }
        val distinctCandidates = nextTargetCandidates.distinct()
        val preferredTargets = distinctCandidates.filter { it != previousTarget }
        val targetPool = preferredTargets.ifEmpty { distinctCandidates }
        _targetLetter = targetPool[random.nextInt(targetPool.size)]
        return TapOutcome(
            previousTarget = previousTarget,
            tappedLetter = letter,
            isCorrect = isCorrect,
            pointsAwarded = points,
            nextTarget = _targetLetter,
        )
    }

    /**
     * End the session. After this call [completed] is true and the result
     * summary is final.
     */
    fun end(): Summary {
        if (!_completed) {
            _endedAtEpochMs = clock()
            _completed = true
        }
        return summary()
    }

    fun summary(): Summary = Summary(
        sessionId = id,
        startedAtEpochMs = startedAtEpochMs,
        endedAtEpochMs = _endedAtEpochMs ?: clock(),
        durationSeconds = durationSeconds,
        finalScore = _score,
        correctTaps = _correctTaps,
        totalTaps = _totalTaps,
        completed = _completed,
    )

    private fun selectTargetLetter(): String = letters[random.nextInt(letters.size)]

    /**
     * One tap's resolution. The UI uses this to pick a feedback tone (positive
     * or gentle) and to update the score/total/correct counters.
     */
    data class TapOutcome(
        val previousTarget: String,
        val tappedLetter: String,
        val isCorrect: Boolean,
        val pointsAwarded: Int,
        val nextTarget: String,
    )

    /**
     * Final session summary persisted to Room. All fields are decimal integers
     * so SQLite can store them without custom type converters.
     */
    data class Summary(
        val sessionId: String,
        val startedAtEpochMs: Long,
        val endedAtEpochMs: Long,
        val durationSeconds: Int,
        val finalScore: Int,
        val correctTaps: Int,
        val totalTaps: Int,
        val completed: Boolean,
    )

    companion object {
        /** Owner-approved session length — two minutes per round. */
        const val DEFAULT_DURATION_SECONDS = 120

        /** Owner-approved scoring rule. */
        const val POINTS_PER_CORRECT = 10

        /** Exactly three letter cards are visible per round. */
        const val ROUND_SIZE = 3
    }
}
