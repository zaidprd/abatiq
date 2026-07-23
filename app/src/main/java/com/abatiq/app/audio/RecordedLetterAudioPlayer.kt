package com.abatiq.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.abatiq.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Plays the supplied alphabet recordings for every letter prompt. TextToSpeech
 * remains only as a fallback for feedback and for devices where a recording
 * cannot be decoded.
 *
 * The WAV files are bundled in res/raw as alphabet_a.wav ... alphabet_z.wav.
 * Replace or update those resources later without changing game logic.
 */
class RecordedLetterAudioPlayer(
    context: Context,
    private val locale: Locale = Locale("id", "ID"),
) : GameAudioPlayer {

    private val appContext = context.applicationContext
    private val _state = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()
    private val utteranceIds = AtomicInteger()

    @Volatile
    private var volumePercent = 80

    @Volatile
    private var ttsReady = false

    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    private var pendingRecordedLetter: String? = null
    private var pendingPrompt: GamePrompt? = null

    init {
        tts = TextToSpeech(appContext) { status ->
            val engine = tts ?: return@TextToSpeech
            if (status != TextToSpeech.SUCCESS) {
                _state.value = PlaybackState.Unavailable
                return@TextToSpeech
            }
            val languageStatus = engine.setLanguage(locale)
            if (languageStatus == TextToSpeech.LANG_MISSING_DATA ||
                languageStatus == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                _state.value = PlaybackState.Unavailable
                return@TextToSpeech
            }
            engine.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build(),
            )
            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit

                override fun onDone(utteranceId: String?) {
                    val letter = pendingRecordedLetter
                    pendingRecordedLetter = null
                    if (letter != null) {
                        playRecordedLetter(letter, pendingPrompt ?: GamePrompt.TargetLetter(letter))
                    } else {
                        _state.value = PlaybackState.Idle
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    pendingRecordedLetter = null
                    _state.value = PlaybackState.Idle
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    pendingRecordedLetter = null
                    _state.value = PlaybackState.Idle
                }
            })
            ttsReady = true
            pendingPrompt?.also {
                pendingPrompt = null
                play(it)
            }
        }
    }

    override fun play(prompt: GamePrompt) {
        stopCurrentPlayback()
        pendingPrompt = prompt
        when (prompt) {
            is GamePrompt.TargetLetter -> playRecordedLetter(prompt.letter, prompt)
            is GamePrompt.CorrectThenTarget -> playRecordedFeedbackThenLetter(
                feedbackResource = R.raw.masyaallah_benar,
                nextLetter = prompt.letter,
                prompt = prompt,
            )
            is GamePrompt.GentleThenTarget -> playRecordedFeedbackThenLetter(
                feedbackResource = R.raw.salah,
                nextLetter = prompt.letter,
                prompt = prompt,
            )
            GamePrompt.CorrectFeedback -> playRecordedFeedback(
                feedbackResource = R.raw.masyaallah_benar,
                prompt = prompt,
            )
            GamePrompt.GentleFeedback -> playRecordedFeedback(
                feedbackResource = R.raw.salah,
                prompt = prompt,
            )
            GamePrompt.SessionEnd -> speakText("Alhamdulillah, sesi belajar selesai!", prompt)
            GamePrompt.AudioTest -> speakText("Tes suara. AbatiQ siap menemani belajar.", prompt)
        }
    }

    private fun playRecordedFeedbackThenLetter(
        feedbackResource: Int,
        nextLetter: String,
        prompt: GamePrompt,
    ) {
        val feedbackPlayer = MediaPlayer.create(appContext, feedbackResource)
        if (feedbackPlayer == null) {
            pendingRecordedLetter = nextLetter
            speakText(
                if (feedbackResource == R.raw.masyaallah_benar) "MasyaAllah, benar." else "Ayo lanjut.",
                prompt,
            )
            return
        }
        mediaPlayer = feedbackPlayer
        feedbackPlayer.setVolume(volumePercent / 100f, volumePercent / 100f)
        _state.value = PlaybackState.Speaking(prompt)
        feedbackPlayer.setOnCompletionListener {
            it.release()
            if (mediaPlayer === it) mediaPlayer = null
            playRecordedLetter(nextLetter, prompt)
        }
        feedbackPlayer.setOnErrorListener { failedPlayer, _, _ ->
            failedPlayer.release()
            if (mediaPlayer === failedPlayer) mediaPlayer = null
            playRecordedLetter(nextLetter, prompt)
            true
        }
        feedbackPlayer.start()
    }

    private fun playRecordedFeedback(
        feedbackResource: Int,
        prompt: GamePrompt,
    ) {
        val feedbackPlayer = MediaPlayer.create(appContext, feedbackResource)
        if (feedbackPlayer == null) {
            speakText(
                if (feedbackResource == R.raw.masyaallah_benar) {
                    "MasyaAllah, benar! Barakallahu fiik."
                } else {
                    "Belum tepat, ayo terus mencoba ya."
                },
                prompt,
            )
            return
        }
        mediaPlayer = feedbackPlayer
        feedbackPlayer.setVolume(volumePercent / 100f, volumePercent / 100f)
        _state.value = PlaybackState.Speaking(prompt)
        feedbackPlayer.setOnCompletionListener {
            it.release()
            if (mediaPlayer === it) mediaPlayer = null
            _state.value = PlaybackState.Idle
        }
        feedbackPlayer.setOnErrorListener { failedPlayer, _, _ ->
            failedPlayer.release()
            if (mediaPlayer === failedPlayer) mediaPlayer = null
            _state.value = PlaybackState.Idle
            true
        }
        feedbackPlayer.start()
    }

    private fun playRecordedLetter(letter: String, prompt: GamePrompt) {
        val resourceId = resourceForLetter(letter)
        if (resourceId == null) {
            speakText("Huruf $letter.", prompt)
            return
        }
        pendingPrompt = null
        val player = MediaPlayer.create(appContext, resourceId)
        if (player == null) {
            speakText("Huruf $letter.", prompt)
            return
        }
        mediaPlayer = player
        player.setVolume(volumePercent / 100f, volumePercent / 100f)
        _state.value = PlaybackState.Speaking(prompt)
        player.setOnCompletionListener {
            it.release()
            if (mediaPlayer === it) mediaPlayer = null
            _state.value = PlaybackState.Idle
        }
        player.setOnErrorListener { failedPlayer, _, _ ->
            failedPlayer.release()
            if (mediaPlayer === failedPlayer) mediaPlayer = null
            speakText("Huruf $letter.", prompt)
            true
        }
        player.start()
    }

    private fun speakText(text: String, prompt: GamePrompt) {
        val engine = tts
        if (engine == null || !ttsReady) {
            val letter = pendingRecordedLetter
            pendingRecordedLetter = null
            if (letter != null) {
                playRecordedLetter(letter, pendingPrompt ?: GamePrompt.TargetLetter(letter))
            } else {
                _state.value = PlaybackState.Unavailable
            }
            return
        }
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volumePercent / 100f)
        }
        _state.value = PlaybackState.Speaking(prompt)
        val result = engine.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            params,
            "recorded-${utteranceIds.incrementAndGet()}",
        )
        if (result != TextToSpeech.SUCCESS) _state.value = PlaybackState.Idle
    }

    override fun stop() {
        pendingPrompt = null
        pendingRecordedLetter = null
        stopCurrentPlayback()
        _state.value = PlaybackState.Idle
    }

    private fun stopCurrentPlayback() {
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnErrorListener(null)
        mediaPlayer?.stopSafely()
        mediaPlayer = null
        tts?.stop()
    }

    override fun setVolume(percent: Int) {
        volumePercent = percent.coerceIn(0, 100)
        mediaPlayer?.setVolume(volumePercent / 100f, volumePercent / 100f)
    }

    override fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        ttsReady = false
    }

    private fun resourceForLetter(letter: String): Int? = when (letter.uppercase(Locale.ROOT)) {
        "A" -> R.raw.alphabet_a
        "B" -> R.raw.alphabet_b
        "C" -> R.raw.alphabet_c
        "D" -> R.raw.alphabet_d
        "E" -> R.raw.alphabet_e
        "F" -> R.raw.alphabet_f
        "G" -> R.raw.alphabet_g
        "H" -> R.raw.alphabet_h
        "I" -> R.raw.alphabet_i
        "J" -> R.raw.alphabet_j
        "K" -> R.raw.alphabet_k
        "L" -> R.raw.alphabet_l
        "M" -> R.raw.alphabet_m
        "N" -> R.raw.alphabet_n
        "O" -> R.raw.alphabet_o
        "P" -> R.raw.alphabet_p
        "Q" -> R.raw.alphabet_q
        "R" -> R.raw.alphabet_r
        "S" -> R.raw.alphabet_s
        "T" -> R.raw.alphabet_t
        "U" -> R.raw.alphabet_u
        "V" -> R.raw.alphabet_v
        "W" -> R.raw.alphabet_w
        "X" -> R.raw.alphabet_x
        "Y" -> R.raw.alphabet_y
        "Z" -> R.raw.alphabet_z
        else -> null
    }
}

private fun MediaPlayer.stopSafely() {
    try {
        if (isPlaying) stop()
    } catch (_: IllegalStateException) {
        // Player was already released by its completion/error callback.
    }
    try {
        release()
    } catch (_: IllegalStateException) {
        // Nothing else to release.
    }
}
