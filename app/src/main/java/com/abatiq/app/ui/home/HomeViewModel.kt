package com.abatiq.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abatiq.app.audio.GameAudioPlayer
import com.abatiq.app.audio.PlaybackState
import com.abatiq.app.data.domain.ProgressSummary
import com.abatiq.app.data.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    progressRepository: ProgressRepository,
    private val audioPlayer: GameAudioPlayer,
) : ViewModel() {

    val progress: StateFlow<ProgressSummary> = progressRepository.summary
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProgressSummary.Empty,
        )

    val playback: StateFlow<PlaybackState> = audioPlayer.state
}
