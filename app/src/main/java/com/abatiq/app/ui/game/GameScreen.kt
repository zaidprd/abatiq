package com.abatiq.app.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abatiq.app.LandscapeOnlyEffect
import com.abatiq.app.R
import com.abatiq.app.data.domain.LetterCard
import com.abatiq.app.data.domain.LetterCardFeedback
import com.abatiq.app.ui.components.AbatiQIconButton
import com.abatiq.app.ui.components.BalloonView
import com.abatiq.app.ui.components.PlaceholderBalloon
import com.abatiq.app.ui.theme.AbatiQColors

/**
 * Landscape "Tebak Huruf" playfield.
 *
 * Layout (always landscape):
 *   - Top bar: back, score chip, timer chip, replay speaker.
 *   - Three big letter cards in a row, each in a chunky rounded frame.
 *   - Cards reveal one-by-one (stagger) each round.
 *   - Tap reaction: correct → quick zoom pulse; gentle → horizontal shake.
 *   - Round is locked while a reaction plays; a fresh batch appears after.
 */
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onSessionComplete: (result: GameResult) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        result?.let(onSessionComplete)
    }

    LandscapeOnlyEffect()

    BackHandler(enabled = state.reactingCardId == null) { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbatiQColors.Cream),
    ) {
        // 1. Raster background (TK classroom scene, project-owner Canva artwork)
        Image(
            painter = painterResource(R.drawable.ic_background_tebak_huruf_konversi),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // 2. Soft cream radial scrim brightens the centre so the balloons stay focal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AbatiQColors.Cream.copy(alpha = 0.55f),
                            AbatiQColors.Cream.copy(alpha = 0.25f),
                            AbatiQColors.Cream.copy(alpha = 0.0f),
                        ),
                        radius = 1400f,
                    ),
                ),
        )
        // 3. Subtle top-bar scrim so icons stay legible over the mural side
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.12f),
                            Color.Black.copy(alpha = 0.0f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GameTopBar(
                score = state.score,
                remainingSeconds = state.remainingSeconds,
                sessionDurationSeconds = state.sessionDurationSeconds,
                audioReady = state.audioReady,
                audioSpeaking = state.audioSpeaking,
                onBack = onBack,
                onReplay = viewModel::replayTarget,
            )

            val haptic = LocalHapticFeedback.current
            CardRow(
                cards = state.cards,
                reactingCardId = state.reactingCardId,
                reactingFeedback = state.reactingFeedback,
                onTap = { letter ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.onCardTapped(letter)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
            )
        }
    }
}

@Composable
private fun GameTopBar(
    score: Int,
    remainingSeconds: Int,
    sessionDurationSeconds: Int,
    audioReady: Boolean,
    audioSpeaking: Boolean,
    onBack: () -> Unit,
    onReplay: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AbatiQIconButton(
            contentDescription = stringResource(R.string.game_back_cd),
            onClick = onBack,
            modifier = Modifier.size(56.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = AbatiQColors.ForestDark,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        TimerChip(
            remainingSeconds = remainingSeconds,
            totalSeconds = sessionDurationSeconds,
        )
        Spacer(Modifier.size(12.dp))
        ScoreChip(score = score)
        Spacer(Modifier.size(12.dp))
        AbatiQIconButton(
            contentDescription = stringResource(R.string.game_replay_cd),
            onClick = onReplay,
            modifier = Modifier.size(56.dp),
            background = AbatiQColors.Honey,
            borderColor = AbatiQColors.HoneyDeep,
        ) {
            val speakerScale by animateFloatAsState(
                targetValue = if (audioSpeaking) 1.14f else 1f,
                animationSpec = tween(240),
                label = "speaker-pulse",
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = null,
                tint = AbatiQColors.ForestDark,
                modifier = Modifier
                    .size(30.dp)
                    .graphicsLayer(scaleX = speakerScale, scaleY = speakerScale),
            )
            if (!audioReady) {
                Text(
                    text = "!",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AbatiQColors.Terracotta,
                        fontWeight = FontWeight.Black,
                    ),
                )
            }
        }
    }
}

@Composable
private fun TimerChip(remainingSeconds: Int, totalSeconds: Int) {
    val fraction = if (totalSeconds == 0) 0f else
        (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 240),
        label = "timer-fraction",
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Paper)
            .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = remainingSeconds.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = AbatiQColors.ForestDark,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
                Text(
                    text = " dtk",
                    style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.InkSoft),
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AbatiQColors.CreamDim),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .fillMaxHeight()
                        .background(AbatiQColors.HoneyDeep),
                )
            }
        }
    }
}

@Composable
private fun ScoreChip(score: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Forest)
            .border(2.dp, AbatiQColors.ForestDark, RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "★",
                style = MaterialTheme.typography.titleLarge.copy(color = AbatiQColors.Honey),
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = AbatiQColors.Cream,
                    fontWeight = FontWeight.ExtraBold,
                ),
            )
        }
    }
}

@Composable
private fun CardRow(
    cards: List<LetterCard>,
    reactingCardId: Long?,
    reactingFeedback: LetterCardFeedback,
    onTap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val padded = remember(cards) {
        if (cards.size >= CompanionLetters) {
            cards
        } else {
            cards + List(CompanionLetters - cards.size) { placeholderCard(it) }
        }
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        padded.forEach { card ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = card,
                    transitionSpec = {
                        val direction = if (targetState.revealIndex >= initialState.revealIndex) 1 else -1
                        (slideInHorizontally(animationSpec = tween(220)) { full -> direction * full } + fadeIn()) togetherWith
                            (slideOutHorizontally(animationSpec = tween(160)) { full -> -direction * full } + fadeOut())
                    },
                    label = "card-swap",
                ) { current ->
                    if (current.isRevealed || current.id == reactingCardId) {
                        BalloonView(
                            slotIndex = current.revealIndex.coerceIn(0, 2),
                            letter = current.letter,
                            isReacting = current.id == reactingCardId,
                            feedback = if (current.id == reactingCardId) reactingFeedback else LetterCardFeedback.None,
                            onTap = { onTap(current.letter) },
                        )
                    } else {
                        // Reserved slot keeps the row layout stable while the
                        // first balloon reveals.
                        PlaceholderBalloon()
                    }
                }
            }
        }
    }
}

private const val CompanionLetters: Int = 3

private fun placeholderCard(index: Int): LetterCard = LetterCard(
    letter = "",
    id = -1L - index,
    revealIndex = index,
    isRevealed = false,
)
