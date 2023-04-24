package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.calamarfederal.messyink.common.compose.Placeholder

@Composable
internal fun CustomTickEntryDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onAddTick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Column {
                CustomTickEntry(onAddTick = { onAddTick(it); onDismiss() }, modifier = modifier)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTickEntry(
    onAddTick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        var input by rememberSaveable { mutableStateOf("") }
        var isError by rememberSaveable { mutableStateOf(false) }
        TextField(
            value = input,
            onValueChange = { input = it },
            placeholder = { Placeholder("Amount") },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions {
                input.toDoubleOrNull()?.let { onAddTick(it) } ?: run { isError = true }
            },
        )
        FilledIconButton(onClick = {
            input.toDoubleOrNull()?.let { onAddTick(it) } ?: run { isError = true }
        }) {
            Icon(Icons.Filled.Done, "add custom amount")
        }
    }
}
