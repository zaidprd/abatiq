package com.abatiq.app.ui.result

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abatiq.app.R
import com.abatiq.app.ui.components.AbatiQPrimaryButton
import com.abatiq.app.ui.components.AbatiQSecondaryButton
import com.abatiq.app.ui.components.GardenBackdrop
import com.abatiq.app.ui.theme.AbatiQColors

@Composable
fun ResultScreen(
    score: Int,
    correctTaps: Int,
    totalTaps: Int,
    bestScore: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbatiQColors.Cream),
    ) {
        GardenBackdrop(accentSeed = 3)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(24.dp))
            ResultHeader()
            Spacer(Modifier.height(24.dp))
            ScoreBlock(score = score, correctTaps = correctTaps)
            Spacer(Modifier.height(16.dp))
            BestScoreRow(bestScore = bestScore)
            Spacer(Modifier.height(32.dp))
            AbatiQPrimaryButton(
                text = stringResource(R.string.result_play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            AbatiQSecondaryButton(
                text = stringResource(R.string.result_home),
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ResultHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(listOf(AbatiQColors.Honey, AbatiQColors.HoneyDeep)),
            )
            .border(3.dp, AbatiQColors.HoneyDeep, RoundedCornerShape(28.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "★",
                style = MaterialTheme.typography.displayLarge.copy(color = AbatiQColors.ForestDark),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.result_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = AbatiQColors.ForestDark,
                    fontWeight = FontWeight.ExtraBold,
                ),
            )
        }
    }
}

@Composable
private fun ScoreBlock(score: Int, correctTaps: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Paper)
            .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(24.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.result_score_label),
                style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.InkSoft),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    color = AbatiQColors.ForestDark,
                    fontWeight = FontWeight.Black,
                ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.result_correct_taps, correctTaps),
                style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.InkSoft),
            )
        }
    }
}

@Composable
private fun BestScoreRow(bestScore: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(AbatiQColors.Forest),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.result_best_label, bestScore),
            style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.InkSoft),
        )
    }
}
