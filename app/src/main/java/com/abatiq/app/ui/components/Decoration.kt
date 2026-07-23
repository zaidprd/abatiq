package com.abatiq.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abatiq.app.ui.theme.AbatiQColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Soft layered decoration painted with [Canvas] so we never need an external
 * image. All shapes are geometric: arches, dots, leaves. No faces, no
 * characters, no raster art.
 */
@Composable
fun GardenBackdrop(
    modifier: Modifier = Modifier,
    accentSeed: Int = 0,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AbatiQColors.Cream),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGarden(accentSeed = accentSeed)
        }
    }
}

private fun DrawScope.drawGarden(accentSeed: Int) {
    val w = size.width
    val h = size.height

    // Bottom hill
    drawPath(
        path = Path().apply {
            moveTo(0f, h * 0.86f)
            cubicTo(
                w * 0.2f, h * 0.72f,
                w * 0.5f, h * 0.94f,
                w, h * 0.82f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        },
        color = AbatiQColors.MintSurface,
    )

    // Subtle second hill (depth)
    drawPath(
        path = Path().apply {
            moveTo(0f, h * 0.93f)
            cubicTo(
                w * 0.35f, h * 0.86f,
                w * 0.6f, h * 0.99f,
                w, h * 0.92f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        },
        color = AbatiQColors.Mint,
        alpha = 0.5f,
    )

    // Top arch
    val archCenterX = if (accentSeed % 2 == 0) w * 0.78f else w * 0.18f
    val archCenterY = h * 0.18f
    drawArch(
        centerX = archCenterX,
        centerY = archCenterY,
        outerRadius = h * 0.18f,
        innerRadius = h * 0.12f,
        outerColor = AbatiQColors.Forest,
        innerColor = AbatiQColors.MintSurface,
    )

    // Honey sun
    drawCircle(
        color = AbatiQColors.Honey.copy(alpha = 0.45f),
        radius = h * 0.08f,
        center = Offset(w * 0.16f, h * 0.14f),
    )

    // A scatter of stars and dots
    val dots = listOf(
        Triple(0.55f, 0.22f, AbatiQColors.Terracotta.copy(alpha = 0.35f)),
        Triple(0.32f, 0.38f, AbatiQColors.Forest.copy(alpha = 0.30f)),
        Triple(0.84f, 0.42f, AbatiQColors.Forest.copy(alpha = 0.28f)),
        Triple(0.68f, 0.55f, AbatiQColors.Honey.copy(alpha = 0.45f)),
        Triple(0.12f, 0.58f, AbatiQColors.Terracotta.copy(alpha = 0.30f)),
        Triple(0.46f, 0.74f, AbatiQColors.Forest.copy(alpha = 0.20f)),
    )
    dots.forEach { (fx, fy, c) ->
        drawCircle(
            color = c,
            radius = h * 0.018f,
            center = Offset(w * fx, h * fy),
        )
    }

    // A faint eight-pointed star at the upper-right
    drawEightStar(
        center = Offset(w * 0.92f, h * 0.32f),
        radius = h * 0.05f,
        color = AbatiQColors.Honey.copy(alpha = 0.4f),
    )
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
    // Subtle stroke for definition
    drawCircle(
        color = outerColor.copy(alpha = 0.4f),
        radius = outerRadius,
        center = Offset(centerX, centerY),
        style = Stroke(width = outerRadius * 0.04f),
    )
}

private fun DrawScope.drawEightStar(
    center: Offset,
    radius: Float,
    color: Color,
) {
    val path = Path()
    val outer = radius
    val inner = radius * 0.45f
    val points = 8
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outer else inner
        val angle = (i * Math.PI / points).toFloat() - Math.PI.toFloat() / 2f
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color)
}

/** A tiny bordered decoration for section headers (leaves cluster). */
@Composable
fun LeavesRow(
    height: Dp = 24.dp,
    color: Color = AbatiQColors.Forest,
    modifier: Modifier = Modifier,
) {
    val leafHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { height.toPx() }
    Canvas(
        modifier = modifier.size(width = 96.dp, height = height),
    ) {
        val gap = 6f
        val leafWidth = leafHeightPx * 0.6f
        val stepX = leafWidth + gap
        val startX = (this.size.width - stepX * 5f) / 2f
        val baseY = this.size.height / 2f
        for (i in 0..5) {
            val x = startX + i * stepX
            drawLeaf(
                centerX = x + leafWidth / 2f,
                centerY = baseY,
                size = Size(leafWidth, leafHeightPx),
                color = color,
            )
        }
    }
}

private fun DrawScope.drawLeaf(
    centerX: Float,
    centerY: Float,
    size: Size,
    color: Color,
) {
    val path = Path().apply {
        moveTo(centerX, centerY - size.height / 2)
        cubicTo(
            centerX + size.width / 2, centerY - size.height / 4,
            centerX + size.width / 2, centerY + size.height / 4,
            centerX, centerY + size.height / 2,
        )
        cubicTo(
            centerX - size.width / 2, centerY + size.height / 4,
            centerX - size.width / 2, centerY - size.height / 4,
            centerX, centerY - size.height / 2,
        )
        close()
    }
    drawPath(path = path, color = color)
    drawLine(
        color = AbatiQColors.MintSurface,
        start = Offset(centerX, centerY - size.height / 2),
        end = Offset(centerX, centerY + size.height / 2),
        strokeWidth = 2f,
    )
}