package com.abatiq.app.ui.settings

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abatiq.app.R
import com.abatiq.app.data.domain.ParentalChallenge
import com.abatiq.app.ui.components.AbatiQIconButton
import com.abatiq.app.ui.components.AbatiQPrimaryButton
import com.abatiq.app.ui.components.AbatiQSecondaryButton
import com.abatiq.app.ui.components.WordmarkCompact
import com.abatiq.app.ui.theme.AbatiQColors

@Composable
fun ParentalGateScreen(
    onUnlocked: () -> Unit,
    onCancel: () -> Unit,
) {
    var challenge by remember { mutableStateOf(ParentalChallenge.generate()) }
    var input by remember { mutableStateOf("") }
    var wrong by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbatiQColors.Cream),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                WordmarkCompact()
                AbatiQIconButton(
                    contentDescription = stringResource(R.string.content_desc_close),
                    onClick = onCancel,
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(AbatiQColors.ForestDark, RoundedCornerShape(50)),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.parental_gate_title),
                style = MaterialTheme.typography.displaySmall.copy(color = AbatiQColors.ForestDark),
            )
            Text(
                text = stringResource(R.string.parental_gate_subtitle),
                style = MaterialTheme.typography.bodyLarge.copy(color = AbatiQColors.InkSoft),
            )

            Spacer(Modifier.height(8.dp))

            ChallengeCard(
                challenge = challenge,
                input = input,
                onInputChange = {
                    input = it.filter(Char::isDigit).take(3)
                    wrong = false
                },
                wrong = wrong,
                onSubmit = {
                    val parsed = input.toIntOrNull()
                    if (parsed != null && challenge.isCorrect(parsed)) {
                        onUnlocked()
                    } else {
                        wrong = true
                        input = ""
                        challenge = ParentalChallenge.generate()
                    }
                },
                onCancel = onCancel,
            )
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: ParentalChallenge,
    input: String,
    onInputChange: (String) -> Unit,
    wrong: Boolean,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(AbatiQColors.Paper)
                .border(2.dp, AbatiQColors.CreamDim, RoundedCornerShape(20.dp))
                .padding(20.dp),
        ) {
            Column {
                Text(
                    text = stringResource(
                        R.string.parental_gate_challenge_format,
                        challenge.a,
                        challenge.b,
                    ),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = AbatiQColors.ForestDark,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = { Text(stringResource(R.string.parental_gate_answer_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AbatiQColors.Cream,
                        unfocusedContainerColor = AbatiQColors.Cream,
                        focusedIndicatorColor = AbatiQColors.Forest,
                        unfocusedIndicatorColor = AbatiQColors.CreamDim,
                    ),
                )
                if (wrong) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.parental_gate_wrong),
                        style = MaterialTheme.typography.bodyMedium.copy(color = AbatiQColors.Terracotta),
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        AbatiQPrimaryButton(
            text = stringResource(R.string.parental_gate_continue),
            onClick = onSubmit,
            enabled = input.isNotBlank(),
        )
        Spacer(Modifier.height(12.dp))
        AbatiQSecondaryButton(
            text = stringResource(R.string.parental_gate_cancel),
            onClick = onCancel,
        )
    }
}