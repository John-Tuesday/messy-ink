package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport

/**
 * Screen Version of Edit Tick. Uses [Scaffold]
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditTickScreen(
    uiTickSupport: UiTickSupport,
    onChangeTick: (UiTickSupport) -> Unit,
    onDone: () -> Unit,
    onClose: () -> Unit,
    isDoneEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text("Edit Tick") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, "discard changes")
                    }
                },
                actions = {
                    FilledTonalIconButton(onClick = onDone, enabled = isDoneEnabled) {
                        Icon(Icons.Filled.Done, "save changes")
                    }
                }
            )
        },
    ) { padding ->
        EditTickLayout(
            tickSupport = uiTickSupport,
            onChangeTick = onChangeTick,
            onDone = { if (isDoneEnabled) onDone() },
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        )
    }
}

/**
 * [AlertDialog] version of edit tick. Only exists because modalbottomsheet isn't working
 */
@Composable
fun EditTickDialog(
    uiTickSupport: UiTickSupport,
    onChangeTick: (UiTickSupport) -> Unit,
    onDone: () -> Unit,
    onClose: () -> Unit,
    isDoneEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onDone, enabled = isDoneEnabled) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Cancel") }
        },
        title = {
            Text("Edit Tick")
        },
        text = {
            EditTickLayout(
                tickSupport = uiTickSupport,
                onChangeTick = onChangeTick,
                onDone = { if (isDoneEnabled) onDone() },
            )
        }
    )
}

/**
 * [AlertDialog] meant to take up the whole screen
 * and its content are literally [EditTickScreen]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTickScreenDialog(
    uiTickSupport: UiTickSupport,
    onChangeTick: (UiTickSupport) -> Unit,
    onDone: () -> Unit,
    onClose: () -> Unit,
    isDoneEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onClose,
        modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        EditTickScreen(
            uiTickSupport = uiTickSupport,
            onChangeTick = onChangeTick,
            onDone = onDone,
            onClose = onClose,
            isDoneEnabled = isDoneEnabled,
        )
    }

}

@Composable
private fun EditTickLayout(
    tickSupport: UiTickSupport,
    onChangeTick: (UiTickSupport) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val fc = remember { FocusRequester() }
                Text(
                    text = "Amount",
                    modifier = Modifier.alignByBaseline(),
                )
                OutlinedTextField(
                    value = tickSupport.amountInput,
                    onValueChange = { onChangeTick(tickSupport.copy(amountInput = it)) },
                    isError = tickSupport.amountError,
                    supportingText = { tickSupport.amountHelp?.let { Text(it) } },
                    keyboardActions = KeyboardActions { onDone() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .alignByBaseline()
                        .focusRequester(fc)
                        .focusable()
                )

                LaunchedEffect(Unit) {
                    fc.requestFocus()
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditTickScreenPreview() {
    val tick = UiTickSupport(id = 1L, parentId = 2L)
    EditTickScreen(
        uiTickSupport = tick,
        onChangeTick = {},
        onDone = {},
        onClose = {},
        isDoneEnabled = true
    )
}

@Preview
@Composable
private fun EditTickScreenDialogPreview() {
    val tick = UiTickSupport(id = 1L, parentId = 2L)
    EditTickScreenDialog(
        uiTickSupport = tick,
        onChangeTick = {},
        onDone = {},
        onClose = {},
        isDoneEnabled = true
    )
}
