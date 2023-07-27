package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import android.annotation.SuppressLint
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import org.calamarfederal.messyink.common.presentation.time.toUtcMillis
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.epochMillisToDate

/**
 * Two stage Dialog. First [DatePicker], then [TimePicker]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    onDismissRequest: () -> Unit,
    onSubmit: (LocalDateTime) -> Unit,
    initialDateTime: LocalDateTime? = null,
    selectableDates: SelectableDates = object : SelectableDates {},
) {
    var inputDate by remember { mutableStateOf<LocalDate?>(null) }
    val initialDateMillis by remember(initialDateTime) {
        derivedStateOf { initialDateTime?.date?.toUtcMillis() }
    }
    val initialTime by remember(initialDateTime) {
        derivedStateOf { initialDateTime?.time }
    }

    DateGetter(
        visible = inputDate == null,
        onDismiss = onDismissRequest,
        onSubmit = { inputDate = it },
        dateState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
            selectableDates = selectableDates
        ),
    )
    TimeGetter(
        visible = inputDate != null,
        onDismiss = { inputDate = null },
        onSubmit = { onSubmit(inputDate!!.atTime(it)) },
        timeState = rememberTimePickerState(
            initialHour = initialTime?.hour ?: 0,
            initialMinute = initialTime?.minute ?: 0,
        )
    )
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateGetter(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    dateState: DatePickerState = rememberDatePickerState(),
) {
    if (visible) {
        val confirmEnabled by derivedStateOf { dateState.selectedDateMillis != null }
        DatePickerDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let {
                            onSubmit(epochMillisToDate(it, TimeZone.UTC))
                        }
                    },
                    enabled = confirmEnabled,
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeGetter(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    timeState: TimePickerState = rememberTimePickerState(),
) {
    if (visible) {
        DatePickerDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onSubmit(LocalTime(hour = timeState.hour, minute = timeState.minute))
                    },
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = timeState)
        }
    }
}
