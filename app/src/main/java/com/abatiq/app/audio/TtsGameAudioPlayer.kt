package com.abatiq.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Android TextToSpeech implementation of [GameAudioPlayer]. Indonesian is the
 * only language. If the engine fails to initialise (e.g. no Indonesian voice
 * installed on the device) we expose [PlaybackState.Unavailable] so the UI can
 * gracefully disable replay controls while still letting the child play.
 *
 * Final human-voice recordings will replace this without touching call sites.
 */
class TtsGameAudioPlayer(
    context: Context,
    private val locale: Locale = Locale("id", "ID"),
) : GameAudioPlayer {

    private val _state = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private val utteranceIds = AtomicInteger()

    @Volatile
    private var tts: TextToSpeech? = null

    @Volatile
    private var ready: Boolean = false

    @Volatile
    private var pendingPrompt: GamePrompt? = null

    @Volatile
    private var volumePercent: Int = 80

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status != TextToSpeech.SUCCESS) {
                _state.value = PlaybackState.Unavailable
                return@TextToSpeech
            }
            val engine = tts ?: return@TextToSpeech
            val availability = engine.setLanguage(locale)
            if (availability == TextToSpeech.LANG_MISSING_DATA ||
                availability == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                _state.value = PlaybackState.Unavailable
                return@TextToSpeech
            }
            engine.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
            )
            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // PlaybackState already reflects "speaking" from play()
                }

                override fun onDone(utteranceId: String?) {
                    _state.value = PlaybackState.Idle
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _state.value = PlaybackState.Idle
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _state.value = PlaybackState.Idle
                }
            })
            ready = true
            _state.value = PlaybackState.Idle
            pendingPrompt?.also {
                pendingPrompt = null
                play(it)
            }
        }
    }

    override fun play(prompt: GamePrompt) {
        val engine = tts
        if (engine == null || !ready) {
            pendingPrompt = prompt
            return
        }
        val line = PromptCatalog.lineFor(prompt)
        val id = "u-${utteranceIds.incrementAndGet()}"
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volumePercent / 100f)
        }
        _state.value = PlaybackState.Speaking(prompt)
        val result = engine.speak(line, TextToSpeech.QUEUE_FLUSH, params, id)
        if (result != TextToSpeech.SUCCESS) {
            _state.value = PlaybackState.Idle
        }
    }

    override fun stop() {
        tts?.stop()
        _state.value = PlaybackState.Idle
    }

    override fun setVolume(percent: Int) {
        volumePercent = percent.coerceIn(0, 100)
    }

    override fun shutdown() {
        pendingPrompt = null
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
        _state.value = PlaybackState.Idle
    }
}
