package com.abatiq.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.abatiq.app.data.domain.LetterCardFeedback
import com.abatiq.app.ui.theme.AbatiQColors
import com.abatiq.app.ui.theme.Sniglet
import kotlin.math.PI
import kotlin.math.sin

/**
 * Felt-style balloon cell used in the "Tebak Huruf" playfield.
 *
 * **No wrapper card/rectangle** — the balloon (body + tie + tether + knot)
 * draws directly on the screen, so the playfield reads as "balloons floating
 * above the classroom" rather than "cards on a strip". A soft drop shadow
 * under the body sells the floating sensation.
 *
 * The whole balloon is rendered with a single `graphicsLayer` transform whose
 * `transformOrigin` is pinned to the bottom-centre of the cell, so the sway
 * reads as "balloon rocking on its string" rather than "balloon floating in
 * mid air".
 *
 * Three pastel slots (0=left, 1=middle, 2=right) — palette fixed so the
 * children can rely on the same slot colours every round.
 *
 * Idle behaviour: each slot has a phase offset of 120° so the three
 * balloons never sway in unison. While [isReacting] is true the idle sway
 * is suppressed and the reaction transforms take over (gentle shake on
 * the balloon + bounce/jump on the letter itself).
 */
@Composable
fun BalloonView(
    slotIndex: Int,
    letter: String,
    isReacting: Boolean,
    feedback: LetterCardFeedback,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = remember(slotIndex) { BalloonPalette.forSlot(slotIndex) }

    // --- Letter-level reaction (independent of balloon transform) ---
    val letterScale = remember { Animatable(1f) }
    val letterTranslateY = remember { Animatable(0f) }

    LaunchedEffect(isReacting, feedback) {
        if (isReacting && feedback == LetterCardFeedback.Correct) {
            letterScale.snapTo(1f)
            letterScale.animateTo(
                targetValue = 1.5f,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
            )
            letterScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 220, easing = EaseOutBackEasing),
            )
        } else if (isReacting && feedback == LetterCardFeedback.Gentle) {
            letterTranslateY.snapTo(0f)
            letterTranslateY.animateTo(
                targetValue = -6f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing),
            )
            letterTranslateY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
            )
        } else {
            letterScale.snapTo(1f)
            letterTranslateY.snapTo(0f)
        }
    }

    // --- Balloon-level reaction ---
    val gentleShake = rememberGentleShake(active = isReacting && feedback == LetterCardFeedback.Gentle)
    val reactionTranslationX = if (isReacting && feedback == LetterCardFeedback.Gentle) gentleShake * 14f else 0f
    val reactionRotation = if (isReacting && feedback == LetterCardFeedback.Gentle) gentleShake * 4f else 0f
    val reactionScale = if (isReacting && feedback == LetterCardFeedback.Correct) 1.04f else 1f

    // --- Idle sway: "angin sepo-sepo" — pelan & tenang ---
    // 3.5s per cycle, simple horizontal sway (no figure-8), small amplitude.
    val phaseOffset = remember(slotIndex) {
        (slotIndex.coerceIn(0, 2) * (2.0 * PI / 3.0)).toFloat()
    }
    val swayTransition = rememberInfiniteTransition(label = "balloon-sway")
    val swayPhase by swayTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
        ),
        label = "balloon-sway-phase",
    )

    val density = LocalDensity.current
    val swayTranslateXdp = with(density) { 4.dp.toPx() }
    val swayTranslateYdp = with(density) { 3.dp.toPx() }

    val idleTranslateX: Float
    val idleTranslateY: Float
    val idleRotation: Float
    if (!isReacting) {
        val phase = swayPhase + phaseOffset
        idleTranslateX = sin(phase) * swayTranslateXdp
        idleTranslateY = sin(phase * 0.7f) * swayTranslateYdp   // slower Y, no figure-8
        idleRotation = sin(phase) * 1f                          // ±1°
    } else {
        idleTranslateX = 0f
        idleTranslateY = 0f
        idleRotation = 0f
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // Whole balloon (body + tie + tether + knot + shadow + letter)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0.5f, 1f) // pivot at bottom-centre (tether anchor)
                    translationX = idleTranslateX + reactionTranslationX
                    translationY = idleTranslateY
                    rotationZ = idleRotation + reactionRotation
                    scaleX = reactionScale
                    scaleY = reactionScale
                }
                .clickable(enabled = !isReacting, onClick = onTap),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawBalloon(
                    bodyFill = palette.body,
                    borderColor = palette.border,
                    highlight = palette.highlight,
                    innerBorder = palette.innerBorder,
                    tetherColor = palette.tether,
                    glowAlpha = if (feedback == LetterCardFeedback.Correct) 0.22f else 0f,
                    glowColor = if (feedback == LetterCardFeedback.Correct) AbatiQColors.Honey else Color.Transparent,
                )
            }

            // Sniglet letter — sits centred on the body, with its own bounce/jump
            Box(
                modifier = Modifier.fillMaxSize().graphicsLayer(
                    scaleX = letterScale.value,
                    scaleY = letterScale.value,
                    translationY = letterTranslateY.value,
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = Sniglet,
                        fontWeight = FontWeight.Normal,
                        color = palette.letter,
                    ),
                )
            }
        }
    }
}

/**
 * Reserved-slot balloon while the first card of a fresh batch reveals in.
 * Keeps the row layout stable without committing to a colour. Also a
 * frame-less slot, just a faint oval silhouette to anchor layout.
 */
@Composable
fun PlaceholderBalloon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val bodyCy = h * 0.36f
            val bodyRx = w * 0.42f
            val bodyRy = h * 0.32f
            drawOval(
                color = AbatiQColors.CreamDim.copy(alpha = 0.55f),
                topLeft = Offset(w * 0.5f - bodyRx, bodyCy - bodyRy),
                size = Size(bodyRx * 2f, bodyRy * 2f),
            )
            // faint tether placeholder
            drawLine(
                color = AbatiQColors.TetherGrey.copy(alpha = 0.5f),
                start = Offset(w * 0.5f, h * 0.78f),
                end = Offset(w * 0.5f, h * 1.0f),
                strokeWidth = 2f,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Palette
// ---------------------------------------------------------------------------

/**
 * One fixed pastel slot. Index 0 = left, 1 = middle, 2 = right.
 *
 * Letter colour is intentionally high-contrast against the body:
 *   - pastel-blue body   + pastel-pink letter   (warm pink on cool blue)
 *   - pastel-pink body   + honey-deep letter    (yellow on pink)
 *   - pastel-yellow body + pastel-blue letter   (cool blue on cream-yellow)
 *
 * [innerBorder] is a slightly lighter version of [border] used for the
 * second thin layer of the 2-lapis border, giving a subtle "stamped" depth.
 */
private data class BalloonPalette(
    val body: Color,
    val letter: Color,
    val border: Color,
    val innerBorder: Color,
    val highlight: Color,
    val tether: Color,
) {
    companion object {
        fun forSlot(index: Int): BalloonPalette = when (index.coerceIn(0, 2)) {
            0 -> BalloonPalette(
                body = AbatiQColors.PastelBlue,
                letter = AbatiQColors.PastelPink,
                border = AbatiQColors.ForestDark,
                innerBorder = AbatiQColors.CreamDim,
                highlight = Color.White.copy(alpha = 0.55f),
                tether = AbatiQColors.TetherGrey,
            )
            1 -> BalloonPalette(
                body = AbatiQColors.PastelPink,
                letter = AbatiQColors.HoneyDeep,
                border = AbatiQColors.ForestDark,
                innerBorder = AbatiQColors.CreamDim,
                highlight = Color.White.copy(alpha = 0.55f),
                tether = AbatiQColors.TetherGrey,
            )
            else -> BalloonPalette(
                body = AbatiQColors.PastelYellow,
                letter = AbatiQColors.PastelBlue,
                border = AbatiQColors.ForestDark,
                innerBorder = AbatiQColors.CreamDim,
                highlight = Color.White.copy(alpha = 0.55f),
                tether = AbatiQColors.TetherGrey,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Drawing
// ---------------------------------------------------------------------------

/**
 * Coordinates inside the cell (top → bottom):
 *   - 0%..68%   → balloon body ellipse (centred at ~36%, fairly tall)
 *   - 68%..72%  → knot (small ellipse at attachment point)
 *   - 72%..78%  → tie triangle (outer dark, inner lighter)
 *   - 78%..100% → tether line, tirus (tebal atas → menipis bawah)
 *
 * No card wrapper here — this draws directly onto the canvas.
 */
private fun DrawScope.drawBalloon(
    bodyFill: Color,
    borderColor: Color,
    innerBorder: Color,
    highlight: Color,
    tetherColor: Color,
    glowAlpha: Float,
    glowColor: Color,
) {
    val w = size.width
    val h = size.height

    // --- Correct-answer glow ring (sits behind the body) ---
    if (glowAlpha > 0f) {
        val cx = w * 0.5f
        val bodyCy = h * 0.36f
        val glowR = minOf(w, h) * 0.55f
        drawCircle(
            color = glowColor.copy(alpha = glowAlpha),
            radius = glowR,
            center = Offset(cx, bodyCy),
        )
    }

    // --- Soft drop shadow under the body (offset y=6dp, blur ~12dp) ---
    val shadowOffsetY = with(LocalDensity) { 6.dp.toPx() } // safe fallback
    val shadowOffsetYpx = (h * 0.04f) // proportional 6dp on landscape cell
    @Suppress("UNUSED_EXPRESSION") shadowOffsetY
    val bodyRx = w * 0.46f                  // enlarged from 0.42
    val bodyRy = h * 0.38f                  // enlarged from 0.34
    val bodyCy = h * 0.32f                  // sit a little higher to give tether room

    // Shadow ellipse — drawn first (behind everything else)
    drawOval(
        color = Color.Black.copy(alpha = 0.25f),
        topLeft = Offset(w * 0.5f - bodyRx * 0.95f, bodyCy - bodyRy + shadowOffsetYpx),
        size = Size(bodyRx * 1.9f, bodyRy * 1.05f),
    )

    // --- Tether line — tapered: thicker at top, thinning toward the bottom ---
    val tetherTopY = h * 0.72f
    val tetherBottomY = h * 1.00f
    val tetherTopX = w * 0.5f
    val tetherBottomX = w * 0.5f

    val tetherPath = Path().apply {
        // Build a tapered tether as a closed quad so we can fill it.
        val topHalfW = (w * 0.012f).coerceAtLeast(2.5f)
        val bottomHalfW = (w * 0.004f).coerceAtLeast(1.0f)
        moveTo(tetherTopX - topHalfW, tetherTopY)
        lineTo(tetherTopX + topHalfW, tetherTopY)
        lineTo(tetherBottomX + bottomHalfW, tetherBottomY)
        lineTo(tetherBottomX - bottomHalfW, tetherBottomY)
        close()
    }
    drawPath(path = tetherPath, color = tetherColor)

    // --- Knot — small ellipse at the attachment point ---
    val knotCy = tetherTopY
    val knotRx = w * 0.05f
    val knotRy = h * 0.025f
    drawOval(
        color = borderColor.copy(alpha = 0.85f),
        topLeft = Offset(w * 0.5f - knotRx, knotCy - knotRy),
        size = Size(knotRx * 2f, knotRy * 2f),
    )
    // tiny highlight on the knot
    drawOval(
        color = highlight.copy(alpha = 0.4f),
        topLeft = Offset(w * 0.5f - knotRx * 0.4f, knotCy - knotRy * 0.55f),
        size = Size(knotRx * 0.5f, knotRy * 0.5f),
    )

    // --- Tie triangle (2 lapis: outer dark + inner lighter for depth) ---
    val tieOuter = Path().apply {
        val baseHalfW = w * 0.07f
        val apexY = h * 0.66f
        val apexHalfW = w * 0.04f
        moveTo(tetherTopX - baseHalfW, tetherTopY)
        lineTo(tetherTopX + baseHalfW, tetherTopY)
        lineTo(tetherTopX + apexHalfW, apexY)
        lineTo(tetherTopX - apexHalfW, apexY)
        close()
    }
    drawPath(path = tieOuter, color = borderColor.copy(alpha = 0.65f))

    val tieInner = Path().apply {
        val baseHalfW = w * 0.048f
        val apexY = h * 0.685f
        val apexHalfW = w * 0.026f
        moveTo(tetherTopX - baseHalfW, tetherTopY - h * 0.005f)
        lineTo(tetherTopX + baseHalfW, tetherTopY - h * 0.005f)
        lineTo(tetherTopX + apexHalfW, apexY)
        lineTo(tetherTopX - apexHalfW, apexY)
        close()
    }
    drawPath(path = tieInner, color = innerBorder.copy(alpha = 0.85f))

    // --- Body ellipse with radial gradient (lighter top, slightly darker bottom) ---
    val bodyTopLeft = Offset(w * 0.5f - bodyRx, bodyCy - bodyRy)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                highlight.copy(alpha = 0.55f), // soft inner-light at top
                bodyFill,
                bodyFill.copy(alpha = 0.92f),   // very slight darkening at edge
            ),
            center = Offset(w * 0.5f, bodyCy - bodyRy * 0.4f),
            radius = bodyRy * 1.2f,
        ),
        topLeft = bodyTopLeft,
        size = Size(bodyRx * 2f, bodyRy * 2f),
    )

    // --- Bottom shade: a thin crescent at the bottom of the body ---
    drawOval(
        color = borderColor.copy(alpha = 0.10f),
        topLeft = bodyTopLeft,
        size = Size(bodyRx * 2f, bodyRy * 2f),
    )

    // --- 2 lapis border (outer dark tegas + inner sedikit lebih terang) ---
    drawOval(
        color = borderColor,
        topLeft = bodyTopLeft,
        size = Size(bodyRx * 2f, bodyRy * 2f),
        style = Stroke(width = (w * 0.014f).coerceAtLeast(2.5f)),
    )
    drawOval(
        color = innerBorder,
        topLeft = Offset(bodyTopLeft.x + 2f, bodyTopLeft.y + 2f),
        size = Size(bodyRx * 2f - 4f, bodyRy * 2f - 4f),
        style = Stroke(width = 1f),
    )

    // --- Perbesar sheen kiri-atas jadi 25–30% lebar badan, alpha dinaikkan ---
    val sheenCenterX = w * 0.5f - bodyRx * 0.32f
    val sheenCenterY = bodyCy - bodyRy * 0.48f
    val sheenRx = bodyRx * 0.28f
    val sheenRy = bodyRy * 0.32f
    drawOval(
        color = highlight.copy(alpha = 0.70f),
        topLeft = Offset(sheenCenterX - sheenRx, sheenCenterY - sheenRy),
        size = Size(sheenRx * 2f, sheenRy * 2f),
    )

    // --- Secondary tiny sharp highlight right at the top ---
    val tinyCenterX = w * 0.5f - bodyRx * 0.10f
    val tinyCenterY = bodyCy - bodyRy * 0.70f
    drawOval(
        color = Color.White.copy(alpha = 0.55f),
        topLeft = Offset(tinyCenterX - bodyRx * 0.07f, tinyCenterY - bodyRy * 0.10f),
        size = Size(bodyRx * 0.14f, bodyRy * 0.20f),
    )
}

// ---------------------------------------------------------------------------
// Reaction shake
// ---------------------------------------------------------------------------

@Composable
private fun rememberGentleShake(active: Boolean): Float {
    if (!active) return 0f
    val transition = rememberInfiniteTransition(label = "balloon-gentle-shake")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 220, easing = LinearEasing),
        ),
        label = "balloon-gentle-shake-phase",
    )
    return sin(phase.toDouble()).toFloat()
}

private val EaseOutBackEasing: androidx.compose.animation.core.Easing =
    androidx.compose.animation.core.Easing { fraction ->
        val c1 = 1.70158f
        val c3 = c1 + 1f
        val t = fraction
        val t1 = t - 1f
        1f + c3 * t1 * t1 * t1 + c1 * t1 * t1
    }
