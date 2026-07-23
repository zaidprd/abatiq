package com.abatiq.app.ui.game

import com.abatiq.app.data.domain.LetterCard
import com.abatiq.app.data.domain.LetterCardFeedback

/**
 * UI snapshot for the game screen. Pure data so the screen body can stay
 * declarative. Everything the renderer needs in one frame is here.
 */
data class GameUiState(
    /** Three cards of the active round, always sized [GameSession.ROUND_SIZE]. */
    val cards: List<LetterCard> = emptyList(),
    /** Target letter currently being spoken. */
    val targetLetter: String = "",
    val score: Int = 0,
    val correctTaps: Int = 0,
    val totalTaps: Int = 0,
    /** Total duration of the session in seconds (always 120 in Phase 1). */
    val sessionDurationSeconds: Int = DEFAULT_DURATION_SECONDS,
    val remainingSeconds: Int = DEFAULT_DURATION_SECONDS,
    val isCompleted: Boolean = false,
    val audioReady: Boolean = false,
    val audioSpeaking: Boolean = false,
    /** Which card is currently playing a reaction animation, if any. */
    val reactingCardId: Long? = null,
    val reactingFeedback: LetterCardFeedback = LetterCardFeedback.None,
    /**
     * Set to the [com.abatiq.app.data.domain.GameSession] token right after a
     * tap. Incremented so the UI can drive keyframe-based animations without
     * rebuilding the Composable tree.
     */
    val roundToken: Long = 0L,
) {
    val isPlaying: Boolean
        get() = !isCompleted && targetLetter.isNotEmpty() && remainingSeconds > 0

    companion object {
        const val DEFAULT_DURATION_SECONDS: Int = 120
    }
}

/**
 * Result payload handed to the result screen once the timer reaches zero.
 */
data class GameResult(
    val score: Int,
    val correctTaps: Int,
    val totalTaps: Int,
)
