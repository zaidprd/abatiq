package com.abatiq.app

import android.app.Application
import com.abatiq.app.audio.GameAudioPlayer
import com.abatiq.app.audio.RecordedLetterAudioPlayer
import com.abatiq.app.data.local.AbatiQDatabase
import com.abatiq.app.data.local.PreferencesRepository
import com.abatiq.app.data.repository.GameRepository
import com.abatiq.app.data.repository.ProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Process-scoped singletons. Phase 1 is small enough that hand-rolled service
 * locators beat introducing Hilt — we keep the surface explicit and
 * testable.
 *
 * The default implementation is [RecordedLetterAudioPlayer], which plays the
 * bundled A–Z recordings and falls back to Indonesian TTS for feedback. Replace
 * it later with a fully recorded implementation if more feedback assets are
 * added.
 */
class AbatiQApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: AbatiQDatabase by lazy { AbatiQDatabase.get(this) }
    val preferences: PreferencesRepository by lazy { PreferencesRepository(this) }
    val gameRepository: GameRepository by lazy { GameRepository(database) }
    val progressRepository: ProgressRepository by lazy { ProgressRepository(database) }

    val gameAudioPlayer: GameAudioPlayer by lazy { RecordedLetterAudioPlayer(this) }

    override fun onCreate() {
        super.onCreate()
        // Mirror DataStore volume changes into the audio engine.
        applicationScope.launch {
            preferences.speechVolumePercent
                .distinctUntilChanged()
                .collect { percent ->
                    gameAudioPlayer.setVolume(percent)
                }
        }
    }
}
