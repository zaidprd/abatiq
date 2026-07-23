package com.abatiq.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme: ColorScheme = lightColorScheme(
    primary = AbatiQColors.Forest,
    onPrimary = AbatiQColors.Cream,
    primaryContainer = AbatiQColors.MintSurface,
    onPrimaryContainer = AbatiQColors.ForestDark,
    secondary = AbatiQColors.Terracotta,
    onSecondary = AbatiQColors.Cream,
    secondaryContainer = AbatiQColors.TerracottaSoft,
    onSecondaryContainer = AbatiQColors.ForestDark,
    tertiary = AbatiQColors.HoneyDeep,
    onTertiary = AbatiQColors.Ink,
    tertiaryContainer = AbatiQColors.Honey,
    onTertiaryContainer = AbatiQColors.ForestDark,
    background = AbatiQColors.Cream,
    onBackground = AbatiQColors.Ink,
    surface = AbatiQColors.Paper,
    onSurface = AbatiQColors.Ink,
    surfaceVariant = AbatiQColors.MintSurface,
    onSurfaceVariant = AbatiQColors.InkSoft,
    outline = AbatiQColors.ForestDark,
    outlineVariant = AbatiQColors.CreamDim,
    error = AbatiQColors.Terracotta,
    onError = AbatiQColors.Cream,
    scrim = Color(0x991F1B16),
)

/**
 * The brief calls for a calm daytime surface. We deliberately keep dark mode
 * aligned with the warm cream palette instead of switching to a cool grey, so
 * brand mood is preserved. This is a parent-trustable decision: dark mode is
 * gentle, never harsh.
 */
@Composable
fun AbatiQTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = AbatiQTypography,
        shapes = AbatiQShapes,
        content = content,
    )
}