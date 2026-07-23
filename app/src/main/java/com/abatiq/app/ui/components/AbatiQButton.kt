package com.abatiq.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abatiq.app.ui.theme.AbatiQColors

/**
 * Single primary button style for AbatiQ. Thick friendly borders, generous
 * touch height, no drop shadows — we lean on the border + warm fill for depth
 * so screens stay calm.
 */
@Composable
fun AbatiQPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = AbatiQColors.OnDark,
    fillColor: Color = AbatiQColors.Forest,
    borderColor: Color = AbatiQColors.ForestDark,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (enabled) fillColor else AbatiQColors.CreamDim)
            .border(
                width = 2.dp,
                color = if (enabled) borderColor else AbatiQColors.CreamDim,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                color = if (enabled) contentColor else AbatiQColors.InkSoft,
            ),
        )
    }
}

@Composable
fun AbatiQSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AbatiQColors.Paper)
            .border(
                width = 2.dp,
                color = AbatiQColors.CreamDim,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                color = if (enabled) AbatiQColors.ForestDark else AbatiQColors.InkSoft,
            ),
        )
    }
}

@Composable
fun AbatiQIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    background: Color = AbatiQColors.Paper,
    borderColor: Color = AbatiQColors.CreamDim,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (enabled) background else AbatiQColors.CreamDim)
            .border(
                width = 2.dp,
                color = if (enabled) borderColor else AbatiQColors.CreamDim,
                shape = RoundedCornerShape(50),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun AbatiQOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                BorderStroke(3.dp, AbatiQColors.ForestDark),
                RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(color = AbatiQColors.ForestDark),
        )
    }
}