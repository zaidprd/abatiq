package com.abatiq.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abatiq.app.R
import com.abatiq.app.audio.PlaybackState
import com.abatiq.app.ui.components.AbatiQIconButton
import com.abatiq.app.ui.components.AbatiQPrimaryButton
import com.abatiq.app.ui.components.AbatiQSecondaryButton
import com.abatiq.app.ui.components.WordmarkCompact
import com.abatiq.app.ui.theme.AbatiQColors

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val volume by viewModel.volumePercent.collectAsStateWithLifecycle()
    val playback by viewModel.playbackState.collectAsStateWithLifecycle()
    val resetCompleted by viewModel.resetCompleted.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbatiQColors.Cream),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                WordmarkCompact()
                AbatiQIconButton(
                    contentDescription = stringResource(R.string.game_back_cd),
                    onClick = onBack,
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(AbatiQColors.ForestDark, RoundedCornerShape(50)),
                    )
                }
            }

            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.displaySmall.copy(color = AbatiQColors.ForestDark),
            )

            VolumeCard(
                value = volume,
                onValueChange = viewModel::setVolume,
                onTest = viewModel::playAudioTest,
                audioUnavailable = playback is PlaybackState.Unavailable,
            )

            ResetCard(
                onConfirmRequest = { showResetDialog = true },
                resetCompleted = resetCompleted,
                onDismissResetMessage = viewModel::dismissResetMessage,
            )
        }
    }

    if (showResetDialog) {
        ConfirmDialog(
            title = stringResource(R.string.settings_reset_confirm_title),
            message = stringResource(R.string.settings_reset_confirm_message),
            confirmLabel = stringResource(R.string.settings_reset_confirm_yes),
            cancelLabel = stringResource(R.string.settings_reset_confirm_no),
            onConfirm = {
                viewModel.resetProgress()
                showResetDialog = false
            },
            onCancel = { showResetDialog = false },
        )
    }
}

@Composable
private fun VolumeCard(
    value: Int,
    onValueChange: (Int) -> Unit,
    onTest: () -> Unit,
    audioUnavailable: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Paper)
            .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(24.dp))
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_volume_label),
            style = MaterialTheme.typography.titleLarge.copy(color = AbatiQColors.ForestDark),
        )
        Text(
            text = stringResource(R.string.settings_volume_value, value),
            style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.InkSoft),
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = AbatiQColors.Forest,
                activeTrackColor = AbatiQColors.Forest,
                inactiveTrackColor = AbatiQColors.CreamDim,
            ),
        )
        if (audioUnavailable) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.settings_audio_unavailable),
                style = MaterialTheme.typography.bodySmall.copy(color = AbatiQColors.Terracotta),
            )
        }
        Spacer(Modifier.height(8.dp))
        AbatiQSecondaryButton(
            text = stringResource(R.string.settings_test_audio),
            onClick = onTest,
        )
    }
}

@Composable
private fun ResetCard(
    onConfirmRequest: () -> Unit,
    resetCompleted: Boolean,
    onDismissResetMessage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Paper)
            .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(24.dp))
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_reset_progress),
            style = MaterialTheme.typography.titleLarge.copy(color = AbatiQColors.Terracotta),
        )
        Spacer(Modifier.height(8.dp))
        AbatiQPrimaryButton(
            text = stringResource(R.string.settings_reset_progress),
            onClick = onConfirmRequest,
            fillColor = AbatiQColors.Terracotta,
            borderColor = AbatiQColors.Terracotta,
            contentColor = AbatiQColors.Cream,
        )
        if (resetCompleted) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.settings_reset_done),
                style = MaterialTheme.typography.bodyMedium.copy(color = AbatiQColors.ForestDark),
            )
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbatiQColors.Ink.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AbatiQColors.Paper)
                .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(24.dp))
                .padding(24.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(color = AbatiQColors.ForestDark),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = AbatiQColors.InkSoft),
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AbatiQSecondaryButton(text = cancelLabel, onClick = onCancel)
                }
                Box(modifier = Modifier.weight(1f)) {
                    AbatiQPrimaryButton(
                        text = confirmLabel,
                        onClick = onConfirm,
                        fillColor = AbatiQColors.Terracotta,
                        borderColor = AbatiQColors.Terracotta,
                    )
                }
            }
        }
    }
}