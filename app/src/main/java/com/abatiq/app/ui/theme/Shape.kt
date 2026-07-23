package com.abatiq.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Generous, rounded shapes for a tactile "alphabet block" feel. Avoids the
 * 9999px-pill trend; uses purposefully chunky corners that stay legible at
 * every screen density.
 */
val AbatiQShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)