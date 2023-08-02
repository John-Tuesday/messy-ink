package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import android.annotation.SuppressLint
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.R
import org.calamarfederal.messyink.common.presentation.compose.LocalTimeZone
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.feature_counter.data.model.TickSort

/**
 * Screen Version of Edit Tick. Uses [Scaffold]
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditTickScreen(
    editTickState: EditTickUiState,
    onDone: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onChangeAmount: (TextFieldValue) -> Unit = {},
    onChangeTimeForData: (Instant) -> Unit = {},
    onChangeTimeModified: (Instant) -> Unit = {},
    onChangeTimeCreated: (Instant) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.tick_edit_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag(EditTickTestTags.CloseButton)
                    ) {
                        Icon(Icons.Filled.Close, stringResource(R.string.cancel))
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = onDone,
                        enabled = !editTickState.anyError,
                        modifier = Modifier.testTag(EditTickTestTags.SubmitButton)
                    ) {
                        Icon(Icons.Filled.Done, stringResource(R.string.save))
                    }
                }
            )
        },
    ) { padding ->
        EditTickLayout(
            editTickState = editTickState,
            onChangeAmount = onChangeAmount,
            onChangeTimeCreated = onChangeTimeCreated,
            onChangeTimeForData = onChangeTimeForData,
            onChangeTimeModified = onChangeTimeModified,
            onDone = { if (!editTickState.anyError) onDone() },
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        )
    }
}

@Composable
private fun EditTickLayout(
    editTickState: EditTickUiState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    onChangeAmount: (TextFieldValue) -> Unit = {},
    onChangeTimeForData: (Instant) -> Unit = {},
    onChangeTimeModified: (Instant) -> Unit = {},
    onChangeTimeCreated: (Instant) -> Unit = {},
    timeZone: TimeZone = LocalTimeZone.current,
) {
    Surface(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            var pickerOpenT by remember {
                mutableStateOf<TickSort?>(null)
            }
            val amountFocusRequester = remember { FocusRequester() }
            AmountRow(
                amountInput = editTickState.amountInput,
                onAmountChange = onChangeAmount,
                isError = editTickState.amountHelpState.isError,
                helpText = editTickState.amountHelpState.help,
                focusRequester = amountFocusRequester,
                onDone = onDone,
            )
            TimeRow(
                time = editTickState.timeForData.toLocalDateTime(timeZone),
                isError = editTickState.timeForDataHelpState.isError,
                helpText = editTickState.timeForDataHelpState.help,
                isPickerOpen = pickerOpenT == TickSort.TimeForData,
                requestDialogChange = {
                    pickerOpenT = if (it) TickSort.TimeForData else null
                },
                changeTime = {
                    onChangeTimeForData(it)
                    pickerOpenT = null
                },
                label = stringResource(R.string.time_for_data),
                testTag = EditTickTestTags.TimeForDataField,
            )
            TimeRow(
                time = editTickState.timeModified.toLocalDateTime(timeZone),
                isError = editTickState.timeModifiedHelpState.isError,
                helpText = editTickState.timeModifiedHelpState.help,
                isPickerOpen = pickerOpenT == TickSort.TimeModified,
                requestDialogChange = {
                    pickerOpenT = if (it) TickSort.TimeModified else null
                },
                changeTime = {
                    onChangeTimeModified(it)
                    pickerOpenT = null
                },
                label = stringResource(R.string.time_modified),
                testTag = EditTickTestTags.TimeModifiedField,
            )
            TimeRow(
                time = editTickState.timeCreated.toLocalDateTime(timeZone),
                isError = editTickState.timeCreatedHelpState.isError,
                helpText = editTickState.timeCreatedHelpState.help,
                isPickerOpen = pickerOpenT == TickSort.TimeCreated,
                requestDialogChange = {
                    pickerOpenT = if (it) TickSort.TimeCreated else null
                },
                changeTime = {
                    onChangeTimeCreated(it)
                    pickerOpenT = null
                },
                label = stringResource(R.string.time_created),
                testTag = EditTickTestTags.TimeCreatedField,
            )
            LaunchedEffect(Unit) {
                amountFocusRequester.requestFocus()
            }
        }
    }
}

@Composable
private fun AmountRow(
    amountInput: TextFieldValue,
    onAmountChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
    isError: Boolean = false,
    helpText: String? = null,
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.tick_amount_input_label),
            modifier = Modifier.alignByBaseline(),
        )
        OutlinedTextField(
            value = amountInput,
            onValueChange = onAmountChange,
            isError = isError,
            supportingText = { helpText?.let { Text(it) } },
            keyboardActions = KeyboardActions { onDone() },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .alignByBaseline()
                .focusRequester(focusRequester)
                .testTag(EditTickTestTags.AmountField)
        )
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
    label: String = "",
    dateTimeFormatter: DateTimeFormat = DateTimeFormat(),
    timeZone: TimeZone = LocalTimeZone.current,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            val invalidDate = stringResource(R.string.tick_help_invalid_date)
            Text(
                text = label,
                modifier = Modifier.alignByBaseline(),
            )
            TextButton(
                onClick = { requestDialogChange(true) },
                modifier = Modifier
                    .alignByBaseline()
                    .testTag(testTag)
                    .semantics { if (isError) error(helpText ?: invalidDate) },
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
    EditTickScreen(
        editTickState = MutableEditTickUiState(),
        onDone = { /*TODO*/ },
        onClose = { /*TODO*/ }
    )
}
