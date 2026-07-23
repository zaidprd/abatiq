package com.abatiq.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abatiq.app.R
import com.abatiq.app.audio.PlaybackState
import com.abatiq.app.ui.components.AbatiQIconButton
import com.abatiq.app.ui.components.GardenBackdrop
import com.abatiq.app.ui.components.Wordmark
import com.abatiq.app.ui.theme.AbatiQColors

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val playback by viewModel.playback.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbatiQColors.Cream),
    ) {
        GardenBackdrop(accentSeed = 0)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Header(onOpenSettings = onOpenSettings, playback = playback)

            Spacer(Modifier.height(24.dp))

            Wordmark()

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_greeting),
                style = MaterialTheme.typography.headlineLarge.copy(color = AbatiQColors.ForestDark),
            )

            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge.copy(color = AbatiQColors.InkSoft),
            )

            Spacer(Modifier.height(24.dp))

            ModuleCard(
                title = stringResource(R.string.home_module_tebak_huruf_title),
                subtitle = stringResource(R.string.home_module_tebak_huruf_subtitle),
                onClick = onStartGame,
            )

            Spacer(Modifier.height(24.dp))

            ProgressCard(
                completedSessions = progress.completedSessions,
                bestScore = progress.bestScore,
                lastScore = progress.lastScore,
            )
        }
    }
}

@Composable
private fun Header(
    onOpenSettings: () -> Unit,
    playback: PlaybackState,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val label = when (playback) {
            is PlaybackState.Speaking -> stringResource(R.string.home_audio_speaking)
            PlaybackState.Unavailable -> stringResource(R.string.home_audio_unavailable)
            else -> stringResource(R.string.home_audio_idle)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(AbatiQColors.Paper)
                .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(color = AbatiQColors.ForestDark),
            )
        }

        AbatiQIconButton(
            contentDescription = stringResource(R.string.home_settings_cd),
            onClick = onOpenSettings,
            modifier = Modifier.size(48.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(AbatiQColors.ForestDark, RoundedCornerShape(50)),
            )
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(AbatiQColors.Forest, AbatiQColors.ForestDark),
                ),
            )
            .border(3.dp, AbatiQColors.ForestDark, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(24.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BlockLetter("A")
                Spacer(Modifier.width(8.dp))
                BlockLetter("B")
                Spacer(Modifier.width(8.dp))
                BlockLetter("C")
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall.copy(
                    color = AbatiQColors.Cream,
                    fontWeight = FontWeight.ExtraBold,
                ),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.Mint),
            )
        }
    }
}

@Composable
private fun BlockLetter(letter: String) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AbatiQColors.Cream)
            .border(3.dp, AbatiQColors.Honey, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.displaySmall.copy(color = AbatiQColors.ForestDark),
        )
    }
}

@Composable
private fun ProgressCard(
    completedSessions: Int,
    bestScore: Int,
    lastScore: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Paper)
            .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(24.dp))
            .padding(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.home_progress_title),
                style = MaterialTheme.typography.titleLarge.copy(color = AbatiQColors.ForestDark),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_progress_sessions, completedSessions),
                style = MaterialTheme.typography.bodyLarge.copy(color = AbatiQColors.InkSoft),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.home_progress_best, bestScore),
                style = MaterialTheme.typography.bodyLarge.copy(color = AbatiQColors.InkSoft),
            )
            if (lastScore > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_progress_last, lastScore),
                    style = MaterialTheme.typography.bodyLarge.copy(color = AbatiQColors.InkSoft),
                )
            }
        }
    }
}
