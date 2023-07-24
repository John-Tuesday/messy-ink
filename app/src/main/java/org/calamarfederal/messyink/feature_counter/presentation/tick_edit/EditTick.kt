package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import android.annotation.SuppressLint
import android.app.TimePickerDialog
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.presentation.compose.LocalTimeZone
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.common.presentation.time.toUtcMillis
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.epochMillisToDate
import org.calamarfederal.messyink.feature_counter.presentation.state.error

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
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text("Edit Tick") },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag(EditTickTestTags.CloseButton)
                    ) {
                        Icon(Icons.Filled.Close, "discard changes")
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = onDone,
//                        enabled = isDoneEnabled,
                        enabled = !uiTickSupport.error,
                        modifier = Modifier.testTag(EditTickTestTags.SubmitButton)
                    ) {
                        Icon(Icons.Filled.Done, "save changes")
                    }
                }
            )
        },
    ) { padding ->
        EditTickLayout(
            tickSupport = uiTickSupport,
            onChangeTick = onChangeTick,
            onDone = { if (!uiTickSupport.error) onDone() },
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
        )
    }

}

@Composable
private fun EditTickLayout(
    tickSupport: UiTickSupport,
    onChangeTick: (UiTickSupport) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    timeZone: TimeZone = LocalTimeZone.current,
) {
    Surface(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            var pickerOpenT by remember {
                mutableStateOf<TickSort.TimeType?>(null)
            }
            AmountRow(tickSupport = tickSupport, onChangeTick = onChangeTick, onDone = onDone)
            TimeRow(
                time = tickSupport.timeForDataInput.toLocalDateTime(timeZone),
                isError = tickSupport.timeForDataError,
                helpText = tickSupport.timeForDataHelp,
                isPickerOpen = pickerOpenT == TickSort.TimeType.TimeForData,
                requestDialogChange = {
                    pickerOpenT = if (it) TickSort.TimeType.TimeForData else null
                },
                changeTime = {
                    onChangeTick(tickSupport.copy(timeForDataInput = it))
                    pickerOpenT = null
                },
                label = "Time",
                testTag = EditTickTestTags.TimeForDataField,
            )
            TimeRow(
                time = tickSupport.timeModifiedInput.toLocalDateTime(timeZone),
                isError = tickSupport.timeModifiedError,
                helpText = tickSupport.timeModifiedHelp,
                isPickerOpen = pickerOpenT == TickSort.TimeType.TimeModified,
                requestDialogChange = {
                    pickerOpenT = if (it) TickSort.TimeType.TimeModified else null
                },
                changeTime = {
                    onChangeTick(tickSupport.copy(timeModifiedInput = it))
                    pickerOpenT = null
                },
                label = "Modified",
                testTag = EditTickTestTags.TimeModifiedField,
            )
            TimeRow(
                time = tickSupport.timeCreatedInput.toLocalDateTime(timeZone),
                isError = tickSupport.timeCreatedError,
                helpText = tickSupport.timeCreatedHelp,
                isPickerOpen = pickerOpenT == TickSort.TimeType.TimeCreated,
                requestDialogChange = {
                    pickerOpenT = if (it) TickSort.TimeType.TimeCreated else null
                },
                changeTime = {
                    onChangeTick(tickSupport.copy(timeCreatedInput = it))
                    pickerOpenT = null
                },
                label = "Created",
                testTag = EditTickTestTags.TimeCreatedField,
            )
        }
    }
}

@Composable
private fun AmountRow(
    tickSupport: UiTickSupport,
    onChangeTick: (UiTickSupport) -> Unit,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
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
                .testTag(EditTickTestTags.AmountField)
        )

        LaunchedEffect(Unit) {
            fc.requestFocus()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeRow(
    time: LocalDateTime,
    isPickerOpen: Boolean,
    requestDialogChange: (Boolean) -> Unit,
    changeTime: (Instant) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    helpText: String? = null,
    helpStyle: TextStyle = TextStyle(
        color = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current,
        fontStyle = FontStyle.Italic
    ),
    testTag: String = "",
    label: String = "Time",
    dateTimeFormatter: DateTimeFormat = DateTimeFormat(),
    timeZone: TimeZone = LocalTimeZone.current,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                modifier = Modifier.alignByBaseline(),
            )
            TextButton(
                onClick = { requestDialogChange(true) },
                modifier = Modifier
                    .alignByBaseline()
                    .testTag(testTag)
                    .semantics { if (isError) error("Invalid Date") },
            ) {
                Text(
                    text = time.formatToString(dateTimeFormatter),
                    color = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current
                )
                helpText?.let {
                    Text(
                        text = it,
                        style = helpStyle,
                    )
                }
            }
            if (isPickerOpen) {
                DateTimePicker(
                    initialDateTime = time,
                    onDismissRequest = { requestDialogChange(false) },
                    onSubmit = { changeTime(it.toInstant(timeZone)) },
                )
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
    )
}
