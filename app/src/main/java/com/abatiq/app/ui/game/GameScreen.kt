package com.abatiq.app.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abatiq.app.LandscapeOnlyEffect
import com.abatiq.app.R
import com.abatiq.app.data.domain.LetterCard
import com.abatiq.app.data.domain.LetterCardFeedback
import com.abatiq.app.ui.components.AbatiQIconButton
import com.abatiq.app.ui.components.GardenBackdrop
import com.abatiq.app.ui.theme.AbatiQColors
import kotlin.math.PI
import kotlin.math.sin

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
        GardenBackdrop(accentSeed = 2)

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

            GameFooter(
                targetLetter = state.targetLetter,
                audioReady = state.audioReady,
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
                        LetterCardView(
                            card = current,
                            isReacting = current.id == reactingCardId,
                            feedback = if (current.id == reactingCardId) reactingFeedback else LetterCardFeedback.None,
                            onTap = { onTap(current.letter) },
                        )
                    } else {
                        // Reserved slot keeps the row layout stable while the
                        // first card reveals.
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(AbatiQColors.Paper.copy(alpha = 0.4f))
                                .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(28.dp)),
                        )
                    }
                }
            }
        }
    }
}

private const val CompanionLetters: Int = 3

@Composable
private fun LetterCardView(
    card: LetterCard,
    isReacting: Boolean,
    feedback: LetterCardFeedback,
    onTap: () -> Unit,
) {
    val baseScale by animateFloatAsState(
        targetValue = if (isReacting && feedback == LetterCardFeedback.Correct) 1.16f else 1f,
        animationSpec = tween(durationMillis = 260, easing = LinearEasing),
        label = "card-scale",
    )
    val shake = rememberShakeOffset(active = isReacting && feedback == LetterCardFeedback.Gentle)
    val rotation = if (feedback == LetterCardFeedback.Gentle) shake * 6f else 0f
    val translationX = if (feedback == LetterCardFeedback.Gentle) shake * 18f else 0f

    val borderColor = when (feedback) {
        LetterCardFeedback.Correct -> AbatiQColors.Forest
        LetterCardFeedback.Gentle -> AbatiQColors.Terracotta
        LetterCardFeedback.None -> AbatiQColors.ForestDark
    }
    val fillColor = when (feedback) {
        LetterCardFeedback.Correct -> AbatiQColors.Honey
        LetterCardFeedback.Gentle -> AbatiQColors.TerracottaSoft
        LetterCardFeedback.None -> AbatiQColors.Cream
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(36.dp))
                .background(AbatiQColors.Paper)
                .border(3.dp, AbatiQColors.CreamDim, RoundedCornerShape(36.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = baseScale
                        scaleY = baseScale
                        this.rotationZ = rotation
                        this.translationX = translationX
                    }
                    .clip(RoundedCornerShape(28.dp))
                    .background(fillColor)
                    .border(4.dp, borderColor, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isReacting, onClick = onTap),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = card.letter,
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = AbatiQColors.ForestDark,
                        fontWeight = FontWeight.Black,
                    ),
                )
            }
        }
    }
}

@Composable
private fun rememberShakeOffset(active: Boolean): Float {
    if (!active) return 0f
    val transition = rememberInfiniteTransition(label = "shake")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 220, easing = LinearEasing)),
        label = "shake-phase",
    )
    return sin(phase.toDouble()).toFloat()
}

@Composable
private fun GameFooter(
    targetLetter: String,
    audioReady: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        val status = if (audioReady) {
            stringResource(R.string.game_footer_listen)
        } else {
            stringResource(R.string.game_footer_no_audio)
        }
        Text(
            text = status,
            style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.InkSoft),
        )
    }
}

private fun placeholderCard(index: Int): LetterCard = LetterCard(
    letter = "",
    id = -1L - index,
    revealIndex = index,
    isRevealed = false,
)
