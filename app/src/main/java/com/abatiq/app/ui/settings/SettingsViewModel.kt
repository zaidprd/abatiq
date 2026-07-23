package com.abatiq.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abatiq.app.audio.GameAudioPlayer
import com.abatiq.app.audio.GamePrompt
import com.abatiq.app.audio.PlaybackState
import com.abatiq.app.data.local.PreferencesRepository
import com.abatiq.app.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: PreferencesRepository,
    private val gameRepository: GameRepository,
    private val audioPlayer: GameAudioPlayer,
) : ViewModel() {

    val volumePercent: StateFlow<Int> = preferences.speechVolumePercent
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PreferencesRepository.DEFAULT_VOLUME_PERCENT,
        )

    private val _resetCompleted = MutableStateFlow(false)
    val resetCompleted: StateFlow<Boolean> = _resetCompleted.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = audioPlayer.state

    fun setVolume(percent: Int) {
        viewModelScope.launch {
            preferences.setSpeechVolumePercent(percent)
        }
    }

    fun playAudioTest() {
        audioPlayer.play(GamePrompt.AudioTest)
    }

    fun resetProgress() {
        viewModelScope.launch {
            gameRepository.resetProgress()
            _resetCompleted.value = true
        }
    }

    fun dismissResetMessage() {
        _resetCompleted.value = false
    }
}
