package com.abatiq.app.audio

/**
 * A prompt is any short voice line the app plays during the game. The
 * abstraction lets the production build swap bundled recordings in without
 * changing call sites.
 *
 * The set is intentionally minimal — owner-approved Phase 1 prompt vocabulary:
 *   - [TargetLetter]         "Which letter is this?" (one per A..J)
 *   - [CorrectFeedback]      Short positive spoken line (one variant)
 *   - [GentleFeedback]       Short gentle spoken line (one variant)
 *   - SessionEnd             "Alhamdulillah, the session is done!"
 *   - AudioTest              Settings preview line
 *
 * When reviewed human-voice recordings replace TTS, [GameAudioPlayer]
 * implementations can map these prompts to raw `.wav`/`.ogg` assets without
 * touching call sites; the data model stays the same.
 */
sealed interface GamePrompt {
    data class TargetLetter(val letter: String) : GamePrompt
    data class CorrectThenTarget(val letter: String) : GamePrompt
    data class GentleThenTarget(val letter: String) : GamePrompt
    object CorrectFeedback : GamePrompt
    object GentleFeedback : GamePrompt
    object SessionEnd : GamePrompt
    object AudioTest : GamePrompt
}

/**
 * Read-only playback state. The UI uses this to disable replay buttons while
 * a line is still speaking and to show a "speaker unavailable" hint.
 */
sealed interface PlaybackState {
    object Idle : PlaybackState
    data class Speaking(val prompt: GamePrompt) : PlaybackState
    object Unavailable : PlaybackState
}

/**
 * Replaceable audio abstraction. The Phase 1 implementation is
 * [TtsGameAudioPlayer] backed by Android TextToSpeech; when the owner has
 * reviewed human-voice recordings, a new implementation can be installed
 * in [com.abatiq.app.AbatiQApplication.gameAudioPlayer] without touching the
 * UI, ViewModel, or domain layers.
 */
interface GameAudioPlayer {
    val state: kotlinx.coroutines.flow.StateFlow<PlaybackState>

    fun play(prompt: GamePrompt)

    fun stop()

    fun setVolume(percent: Int)

    fun shutdown()
}
