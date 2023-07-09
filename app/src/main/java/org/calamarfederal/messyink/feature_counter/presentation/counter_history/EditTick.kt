package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport

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
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        )
    }
}

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
            EditTickLayout(tickSupport = uiTickSupport, onChangeTick = onChangeTick)
        }
    )
}

@Composable
fun EditTickLayout(
    tickSupport: UiTickSupport,
    onChangeTick: (UiTickSupport) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Amount",
                    modifier = Modifier.alignByBaseline(),
                )
                OutlinedTextField(
                    value = tickSupport.amountInput,
                    onValueChange = { onChangeTick(tickSupport.copy(amountInput = it)) },
                    isError = tickSupport.amountError,
                    supportingText = { tickSupport.amountHelp?.let { Text(it) } },
                    modifier = Modifier.alignByBaseline()
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditTickPreview() {
    val tick = UiTickSupport(id = 1L, parentId = 2L)
    EditTickScreen(
        uiTickSupport = tick,
        onChangeTick = {},
        onDone = {},
        onClose = {},
        isDoneEnabled = true
    )
}
