package com.abatiq.app.ui.game

import com.abatiq.app.data.domain.Balloon
import com.abatiq.app.data.domain.BalloonState
import com.abatiq.app.data.domain.FeedbackPulse

/**
 * UI snapshot for the game screen. Pure data so the screen body can stay
 * declarative. Everything the renderer needs in one frame is here.
 */
data class GameUiState(
    val balloons: List<Balloon> = emptyList(),
    val targetLetter: String = "",
    val score: Int = 0,
    val correctTaps: Int = 0,
    val totalTaps: Int = 0,
    val remainingSeconds: Int = 60,
    val isCompleted: Boolean = false,
    val audioReady: Boolean = false,
    val audioSpeaking: Boolean = false,
    val feedbackPulse: FeedbackPulse = FeedbackPulse.None,
    val pulsedBalloonId: Long? = null,
    val isAwaitingRender: Boolean = false,
) {
    val isPlaying: Boolean
        get() = !isCompleted && targetLetter.isNotEmpty() && remainingSeconds > 0
}

/**
 * Result payload handed to the result screen once the timer reaches zero.
 */
data class GameResult(
    val score: Int,
    val correctTaps: Int,
    val totalTaps: Int,
)
