package com.abatiq.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abatiq.app.ui.theme.AbatiQColors

/**
 * Wordmark — large, asymmetric display type. Renders the brand "AbatiQ" with
 * a tiny honey dot beside the final glyph to hint at the warm reward system
 * without ever using a mascot.
 */
@Composable
fun Wordmark(
    modifier: Modifier = Modifier,
    color: Color = AbatiQColors.ForestDark,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = "Abati",
            style = MaterialTheme.typography.displaySmall.copy(
                color = color,
            ),
        )
        Text(
            text = "Q",
            style = MaterialTheme.typography.displaySmall.copy(color = color),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "•",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = AbatiQColors.HoneyDeep,
            ),
        )
    }
}

/** Sub-wordmark for compact headers (Result, Settings). */
@Composable
fun WordmarkCompact(
    modifier: Modifier = Modifier,
    color: Color = AbatiQColors.ForestDark,
) {
    Column(modifier = modifier) {
        Text(
            text = "AbatiQ",
            style = MaterialTheme.typography.titleLarge.copy(color = color),
        )
    }
}