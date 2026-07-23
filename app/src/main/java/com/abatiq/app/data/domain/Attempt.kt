package com.abatiq.app.data.domain

/**
 * One tap written to the audit trail. Persists both the target letter and the
 * letter that was tapped so a future parent report can review what each child
 * did during a session.
 */
data class Attempt(
    val sessionId: String,
    val targetLetter: String,
    val tappedLetter: String,
    val isCorrect: Boolean,
    val pointsAwarded: Int,
    val timestampMillis: Long,
)

/**
 * Light-weight feedback flag, kept distinct from the GameSession so the UI
 * layer can decide what to show without leaking domain state.
 */
enum class FeedbackPulse {
    None,
    Correct,
    Gentle,
}
