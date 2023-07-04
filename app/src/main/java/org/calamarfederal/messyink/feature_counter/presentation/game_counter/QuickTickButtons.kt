package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.common.presentation.compose.MoreClickButton
import org.calamarfederal.messyink.common.presentation.compose.MoreClickFilledTonalButton
import org.calamarfederal.messyink.common.presentation.compose.material3.exposed.ExposedButtonDefaults
import org.calamarfederal.messyink.common.presentation.format.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.TickButton.Primary
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.TickButton.Secondary

internal enum class TickButton {
    Primary,
    Secondary
}

@Composable
internal fun CompactTickButtons(
    centerSlot: @Composable () -> Unit,
    onAddTick: (Double) -> Unit,
    onEditIncrement: (TickButton) -> Unit,
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
                onEditIncrement(Primary)
            },
            modifier = Modifier
                .testTag(GameCounterTestTags.PrimaryIncButton)
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Filled.Add, "add $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
        MoreClickFilledTonalButton(
            onClick = { onAddTick(secondaryAmount) },
            onLongClick = {
                haptic.performHapticFeedback(LongPress)
                onEditIncrement(Secondary)
            },
            modifier = Modifier
                .testTag(GameCounterTestTags.SecondaryIncButton)
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Filled.Add, "add $secondaryAmount")
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
                onEditIncrement(Secondary)
            },
            colors = ExposedButtonDefaults.filledTonalButtonColors.copy(
                container = MaterialTheme.colorScheme.surfaceVariant,
                content = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .testTag(GameCounterTestTags.SecondaryDecButton)
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Filled.Remove, "subtract $secondaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(secondaryAmount.toStringAllowShorten(), style = textStyle)
        }
        MoreClickButton(
            onClick = { onAddTick(-primaryAmount) },
            onLongClick = {
                haptic.performHapticFeedback(LongPress)
                onEditIncrement(Primary)
            },
            colors = ExposedButtonDefaults.buttonColors.copy(
                container = MaterialTheme.colorScheme.tertiary,
                content = MaterialTheme.colorScheme.onTertiary,
            ),
            modifier = Modifier
                .testTag(GameCounterTestTags.PrimaryDecButton)
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Filled.Remove, "subtract $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
    }
}

@Composable
internal fun EditIncrementDialog(
    currentAmount: Double,
    onChangeAmount: (Double) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    prompt: String = "New Increment",
    confirmText: String = "Change",
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    var inputText by remember { mutableStateOf("") }
    val newIncrement by remember { derivedStateOf { inputText.toDoubleOrNull() } }
    var inputError by remember { mutableStateOf(false) }
    var supportText by remember { mutableStateOf("") }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        },
        confirmButton = {
            TextButton(
                enabled = newIncrement != null,
                onClick = {
                    newIncrement?.let { onChangeAmount(it) } ?: onDismissRequest()
                },
            ) { Text(confirmText) }
        },
        title = {
            Text(prompt)
        },
        text = {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    newIncrement?.let { onChangeAmount(it) } ?: run {
                        inputError = true
                        supportText = "must be a decimal number"
                    }
                },
                isError = inputError,
                singleLine = true,
                supportingText = { Text(supportText) },
                placeholder = {
                    Text(
                        text = currentAmount.toStringAllowShorten(),
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                    )
                },
                modifier = Modifier.focusRequester(focusRequester),
            )
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        }
    )
}
