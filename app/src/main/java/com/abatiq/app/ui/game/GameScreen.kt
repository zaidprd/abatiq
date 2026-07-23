package com.abatiq.app.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abatiq.app.R
import com.abatiq.app.data.domain.Balloon
import com.abatiq.app.data.domain.BalloonState
import com.abatiq.app.data.domain.FeedbackPulse
import com.abatiq.app.ui.components.AbatiQIconButton
import com.abatiq.app.ui.components.GardenBackdrop
import com.abatiq.app.ui.theme.AbatiQColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * 60-second floating-balloon playfield. The screen is intentionally minimal:
 *   - a top bar with back, timer, score, and speaker replay
 *   - a sunlit-garden canvas where letter balloons rise from below
 *
 * No question text is rendered on screen. The audio abstraction is the only
 * prompt the child needs.
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbatiQColors.Cream),
    ) {
        GardenBackdrop(accentSeed = 2)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            GameTopBar(
                remainingSeconds = state.remainingSeconds,
                score = state.score,
                audioReady = state.audioReady,
                audioSpeaking = state.audioSpeaking,
                onBack = onBack,
                onReplay = viewModel::replayTarget,
            )
            BalloonPlayfield(
                state = state,
                onTap = viewModel::onBalloonTapped,
                onFrame = viewModel::onFrame,
                onPlayAreaChanged = viewModel::setPlayArea,
            )
        }
    }
}

@Composable
private fun GameTopBar(
    remainingSeconds: Int,
    score: Int,
    audioReady: Boolean,
    audioSpeaking: Boolean,
    onBack: () -> Unit,
    onReplay: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
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
                modifier = Modifier.size(30.dp),
            )
        }
        TimerChip(
            remainingSeconds = remainingSeconds,
            contentDescription = stringResource(R.string.game_timer_cd),
        )
        ScoreChip(
            score = score,
            contentDescription = stringResource(R.string.game_score_cd),
        )
        AbatiQIconButton(
            contentDescription = stringResource(R.string.game_replay_cd),
            onClick = onReplay,
            enabled = true,
            modifier = Modifier.size(56.dp),
            background = AbatiQColors.Honey,
            borderColor = AbatiQColors.HoneyDeep,
        ) {
            val speakerScale by animateFloatAsState(
                targetValue = if (audioSpeaking) 1.14f else 1f,
                animationSpec = tween(240),
                label = "speaker-pulse",
            )
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = null,
                    tint = AbatiQColors.ForestDark,
                    modifier = Modifier
                        .size(32.dp)
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
}

@Composable
private fun TimerChip(remainingSeconds: Int, contentDescription: String) {
    val fraction = (remainingSeconds.toFloat() / 60f).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 220),
        label = "timer-fraction",
    )
    Box(
        modifier = Modifier
            .semantics { this.contentDescription = "$contentDescription: $remainingSeconds detik" }
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Paper)
            .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = remainingSeconds.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = AbatiQColors.ForestDark,
                    fontWeight = FontWeight.ExtraBold,
                ),
            )
            Spacer(Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AbatiQColors.CreamDim),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .height(6.dp)
                        .background(AbatiQColors.HoneyDeep),
                )
            }
        }
    }
}

@Composable
private fun ScoreChip(score: Int, contentDescription: String) {
    Box(
        modifier = Modifier
            .semantics { this.contentDescription = "$contentDescription: $score" }
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(AbatiQColors.Forest)
            .border(2.dp, AbatiQColors.ForestDark, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "★",
                style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.Honey),
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = AbatiQColors.Cream,
                    fontWeight = FontWeight.ExtraBold,
                ),
            )
        }
    }
}

@Composable
private fun BalloonPlayfield(
    state: GameUiState,
    onTap: (Long, String) -> Unit,
    onFrame: (Float) -> Unit,
    onPlayAreaChanged: (GameViewModel.PlayArea) -> Unit,
) {
    val balloons = state.balloons
    val balloonsState = rememberUpdatedState(balloons)
    val onFrameState = rememberUpdatedState(onFrame)
    val onTapState = rememberUpdatedState(onTap)

    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            val now = withFrameNanos { it }
            if (lastNanos != 0L) {
                val deltaSeconds = (now - lastNanos) / 1_000_000_000f
                onFrameState.value(deltaSeconds.coerceIn(0f, 0.1f))
            }
            lastNanos = now
        }
    }

    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val balloonWidthPx = with(density) { BALLOON_WIDTH_DP.toPx() }
    val balloonHeightPx = with(density) { BALLOON_HEIGHT_DP.toPx() }
    val balloonRadiusPx = balloonWidthPx / 2f
    val stringLengthPx = with(density) { 14.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                onPlayAreaChanged(
                    GameViewModel.PlayArea(
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        balloonWidthPx = balloonWidthPx,
                        balloonHeightPx = balloonHeightPx,
                    ),
                )
            }
            .pointerInput(Unit) {
                // Keep one stable gesture detector while balloon positions update
                // every frame; restarting pointer input would cancel child taps.
                detectBalloonTaps(
                    balloons = { balloonsState.value },
                    radiusPx = balloonRadiusPx,
                    onTap = { id, letter -> onTapState.value(id, letter) },
                    onMiss = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                )
            },
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawEdgeOrnaments()
            balloons.forEach { balloon ->
                drawBalloon(
                    balloon = balloon,
                    balloonWidthPx = balloonWidthPx,
                    balloonHeightPx = balloonHeightPx,
                    stringLengthPx = stringLengthPx,
                    pulse = balloon.state != BalloonState.Active,
                    pulseState = when (balloon.state) {
                        BalloonState.TappedCorrect -> FeedbackPulse.Correct
                        BalloonState.TappedWrong -> FeedbackPulse.Gentle
                        BalloonState.Active -> FeedbackPulse.None
                    },
                )
            }
        }
    }
}

private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectBalloonTaps(
    balloons: () -> List<Balloon>,
    radiusPx: Float,
    onTap: (Long, String) -> Unit,
    onMiss: () -> Unit,
) {
    detectTapGestures(
        onTap = { offset: Offset ->
            val hit = balloons()
                .filter { it.state == BalloonState.Active }
                .minByOrNull { b ->
                    val cx = b.xFraction * size.width
                    val cy = b.topY + radiusPx
                    val dx = offset.x - cx
                    val dy = offset.y - cy
                    dx * dx + dy * dy
                }
            if (hit != null) {
                val cx = hit.xFraction * size.width
                val cy = hit.topY + radiusPx
                val dx = offset.x - cx
                val dy = offset.y - cy
                if (dx * dx + dy * dy <= radiusPx * radiusPx) {
                    onTap(hit.id, hit.letter)
                } else {
                    onMiss()
                }
            } else {
                onMiss()
            }
        },
    )
}

private fun DrawScope.drawEdgeOrnaments() {
    val w = size.width
    val h = size.height
    // soft sun upper-left
    drawCircle(
        color = AbatiQColors.Honey.copy(alpha = 0.32f),
        radius = h * 0.07f,
        center = Offset(w * 0.12f, h * 0.10f),
    )
    // tiny crescent dots
    drawCircle(
        color = AbatiQColors.Terracotta.copy(alpha = 0.35f),
        radius = h * 0.012f,
        center = Offset(w * 0.92f, h * 0.08f),
    )
    drawCircle(
        color = AbatiQColors.Forest.copy(alpha = 0.20f),
        radius = h * 0.010f,
        center = Offset(w * 0.05f, h * 0.92f),
    )
    // edge arch upper-right
    drawArch(
        centerX = w * 0.85f,
        centerY = h * 0.10f,
        outerRadius = h * 0.10f,
        innerRadius = h * 0.07f,
        outerColor = AbatiQColors.Forest.copy(alpha = 0.45f),
        innerColor = AbatiQColors.Cream,
    )
    // bottom hills
    drawPath(
        path = Path().apply {
            moveTo(0f, h * 0.88f)
            cubicTo(w * 0.2f, h * 0.78f, w * 0.5f, h * 0.96f, w, h * 0.86f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        },
        color = AbatiQColors.MintSurface,
    )
    drawPath(
        path = Path().apply {
            moveTo(0f, h * 0.94f)
            cubicTo(w * 0.35f, h * 0.88f, w * 0.6f, h * 0.99f, w, h * 0.92f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        },
        color = AbatiQColors.Mint,
        alpha = 0.55f,
    )
}

private fun DrawScope.drawBalloon(
    balloon: Balloon,
    balloonWidthPx: Float,
    balloonHeightPx: Float,
    stringLengthPx: Float,
    pulse: Boolean,
    pulseState: FeedbackPulse,
) {
    val cx = balloon.xFraction * size.width
    val cy = balloon.topY + balloonWidthPx / 2f
    val radius = balloonWidthPx / 2f * if (pulse) 1.12f else 1f
    val color = colorForLetter(balloon.letter)
    val borderColor = when (pulseState) {
        FeedbackPulse.Correct -> AbatiQColors.Forest
        FeedbackPulse.Gentle -> AbatiQColors.Terracotta
        FeedbackPulse.None -> AbatiQColors.ForestDark
    }
    drawCircle(color = color, radius = radius, center = Offset(cx, cy))
    drawCircle(
        color = borderColor,
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 5f),
    )
    // string
    drawLine(
        color = AbatiQColors.ForestDark,
        start = Offset(cx, cy + radius),
        end = Offset(cx, cy + radius + stringLengthPx),
        strokeWidth = 2.5f,
    )
    // letter
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            this.color = AbatiQColors.ForestDark.toArgb()
            textSize = radius * 1.05f
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
        }
        val fm = paint.fontMetrics
        val baseline = cy - (fm.ascent + fm.descent) / 2f
        drawText(balloon.letter, cx, baseline, paint)
    }
}

private fun DrawScope.drawArch(
    centerX: Float,
    centerY: Float,
    outerRadius: Float,
    innerRadius: Float,
    outerColor: Color,
    innerColor: Color,
) {
    drawCircle(color = outerColor, radius = outerRadius, center = Offset(centerX, centerY))
    drawCircle(color = innerColor, radius = innerRadius, center = Offset(centerX, centerY))
    drawCircle(
        color = outerColor.copy(alpha = 0.55f),
        radius = outerRadius,
        center = Offset(centerX, centerY),
        style = Stroke(width = outerRadius * 0.06f),
    )
}

private fun colorForLetter(letter: String): Color {
    val palette = listOf(
        AbatiQColors.MintSurface,
        AbatiQColors.Honey,
        AbatiQColors.Mint,
        AbatiQColors.TerracottaSoft,
        AbatiQColors.SkyBlue,
        AbatiQColors.Lavender,
    )
    val idx = (letter.firstOrNull()?.code ?: 0) % palette.size
    return palette[((idx % palette.size) + palette.size) % palette.size]
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt(),
)

private val BALLOON_WIDTH_DP = 72.dp
private val BALLOON_HEIGHT_DP = 88.dp
