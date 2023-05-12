package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import android.util.Log.d
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.common.compose.MoreClickButton
import org.calamarfederal.messyink.common.compose.MoreClickFilledTonalButton
import org.calamarfederal.messyink.common.compose.Placeholder
import org.calamarfederal.messyink.common.compose.material3.exposed.ExposedButtonDefaults
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.TickButton.Primary
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.TickButton.Secondary

internal enum class TickButton {
    Primary {},
    Secondary
}

@Composable
internal fun CompactTickButtons(
    centerSlot: @Composable () -> Unit,
    onAddTick: (Double) -> Unit,
    onEditTick: (TickButton) -> Unit,
    modifier: Modifier = Modifier,
    primaryWeight: Float = 10f,
    primaryAmount: Double = 5.00,
    secondaryWeight: Float = 7.5f,
    secondaryAmount: Double = 1.00,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val haptic = LocalHapticFeedback.current
        MoreClickButton(
            onClick = { onAddTick(primaryAmount) },
            onLongClick = {
                haptic.performHapticFeedback(LongPress)
                onEditTick(Primary)
            },
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Add, "add $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
        MoreClickFilledTonalButton(
            onClick = { onAddTick(secondaryAmount) },
            onLongClick = {
                haptic.performHapticFeedback(LongPress)
                onEditTick(Secondary)
            },
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Add, "add $secondaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(secondaryAmount.toStringAllowShorten(), style = textStyle)
        }
        Box(modifier = Modifier.weight(primaryWeight * 2)) {
            centerSlot()
        }
        MoreClickFilledTonalButton(
            onClick = { onAddTick(-secondaryAmount) },
            onLongClick = {
                haptic.performHapticFeedback(LongPress)
                onEditTick(Secondary)
            },
            colors = ExposedButtonDefaults.filledTonalButtonColors.copy(
                container = MaterialTheme.colorScheme.surfaceVariant,
                content = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Remove, "subtract $secondaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(secondaryAmount.toStringAllowShorten(), style = textStyle)
        }
        MoreClickButton(
            onClick = { onAddTick(-primaryAmount) },
            onLongClick = {
                haptic.performHapticFeedback(LongPress)
                onEditTick(Primary)
            },
            colors = ExposedButtonDefaults.buttonColors.copy(
                container = MaterialTheme.colorScheme.tertiary,
                content = MaterialTheme.colorScheme.onTertiary,
            ),
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Remove, "subtract $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
    }
}

@Composable
internal fun EditIncrement(
    currentAmount: Double,
    onChangeAmount: (Double) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    prompt: String = "Edit Increment",
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            var amount by remember { mutableStateOf("") }
            var error by remember { mutableStateOf(false) }
            var support by remember { mutableStateOf("") }
            val onSubmit = {
                amount.toDoubleOrNull()?.let { onChangeAmount(it) } ?: run {
                    if (amount.isBlank()) onDismissRequest()
                    error = true
                    support = "not a valid number"
                }
            }

            Text(text = prompt, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = { Placeholder(currentAmount.toStringAllowShorten()) },
                isError = error,
                supportingText = if (error) { -> Text(support) } else null,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions { onSubmit() },
                modifier = Modifier.focusRequester(focusRequester),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
                Button(onClick = { onSubmit() }) {
                    Text("Confirm")
                }
            }
        }
    }
}
