package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.presentation.compose.LocalTimeZone
import org.calamarfederal.messyink.common.presentation.compose.charts.GraphSize2d
import org.calamarfederal.messyink.common.presentation.compose.charts.LineGraph
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.common.presentation.format.omitWhen
import org.calamarfederal.messyink.common.presentation.time.toUtcMillis
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.epochMillisToDate
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.ui.theme.MessyInkTheme


@Composable
internal fun TicksOverTimeLayout(
    graphPoints: List<PointByPercent>,
    pointInfo: (Int) -> String,
    range: ClosedRange<Double>,
    domain: TimeDomain,
    domainLimits: TimeDomain,
    changeDomain: (TimeDomain) -> Unit,
    modifier: Modifier = Modifier,
    graphSize: GraphSize2d = GraphSize2d(),
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            var showPointInfo by remember { mutableStateOf(false) }

//          Graph: Amount v Time
            LineGraph(
                contentDescription = "Line graph of Tick amount over time",
                modifier = Modifier
                    .weight(1f)
                    .clickable { showPointInfo = !showPointInfo }
                    .testTag(CounterHistoryTestTags.TickGraph),
                lineGraphPoints = graphPoints,
                pointInfo = {
                    if (showPointInfo) pointInfo(it) else null
                },
                size = graphSize,
                title = {
                    Text(
                        text = "Amount vs Time",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                rangeSlotIndexed = {
                    if (it == 0 || it == graphSize.yAxisChunks - 1) {
                        Text(
                            text = if (it == 0) "${range.start}" else "${range.endInclusive}",
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight(align = if (it == 0) Alignment.Bottom else Alignment.Top)
                        )
                    }
                }
            )

//          Show current domain min and max and allow edit
            DomainBoundsAndPicker(
                domain = domain,
                domainLimits = domainLimits,
                changeDomain = changeDomain,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DomainDropdownMenu(
    domainLabel: String,
    domainOptions: List<TimeDomainTemplate>,
    onClick: (TimeDomainTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        var expanded by remember { mutableStateOf(false) }
        TextButton(onClick = { expanded = true }) {
            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(domainLabel)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            for (opt in domainOptions) {
                DropdownMenuItem(
                    text = { Text(opt.label) },
                    onClick = { onClick(opt); expanded = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
private fun DomainBoundsAndPicker(
    domain: TimeDomain,
    domainLimits: TimeDomain,
    changeDomain: (TimeDomain) -> Unit,
    modifier: Modifier = Modifier,
    timeZone: TimeZone = LocalTimeZone.current,
    dateTimeFormat: DateTimeFormat = DateTimeFormat().omitWhen(domain.start, domain.end, timeZone),
) {
    var openDomainPicker by remember { mutableStateOf(false) }
    val localDomain by remember {
        derivedStateOf {
            domain.start.toLocalDateTime(timeZone) to domain.end.toLocalDateTime(timeZone)
        }
    }
    val domainStart by remember {
        derivedStateOf {
            localDomain.first.formatToString(dateTimeFormat)
        }
    }
    val domainEnd by remember {
        derivedStateOf {
            localDomain.second.formatToString(dateTimeFormat)
        }
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(
            onClick = { openDomainPicker = true },
            modifier = Modifier.testTag(CounterHistoryTestTags.TickGraphDomainLower),
        ) {
            Text(text = domainStart)
        }
        TextButton(
            onClick = { openDomainPicker = true },
            modifier = Modifier.testTag(CounterHistoryTestTags.TickGraphDomainUpper),
        ) {
            Text(text = domainEnd)
        }
    }
    if (openDomainPicker) {
        val selectable = domainLimits.toSelectableDates(timeZone)
        val domainState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = localDomain.first.date.toUtcMillis(),
            initialSelectedEndDateMillis = localDomain.second.date.toUtcMillis(),
            selectableDates = selectable,
        )
        DomainDatePicker(
            state = domainState,
            onDismiss = { openDomainPicker = false },
            onFitToData = { changeDomain(domainLimits); openDomainPicker = false },
            onSubmit = {
                changeDomain(
                    TimeDomain(
                        it.start.atStartOfDayIn(timeZone) ..< it.endInclusive.plus(DatePeriod(days = 1))
                            .atStartOfDayIn(timeZone)
                    )
                )
                openDomainPicker = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDomainPicker(
    initial: LocalTime,
    onSubmit: (LocalTime) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    limit: ClosedRange<LocalTime>? = null,
    title: String = "Set Time",
    state: TimePickerState = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
    ),
) {
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(
                        LocalTime(
                            hour = state.hour,
                            minute = state.minute
                        )
                    )
                },
                enabled = limit?.contains(LocalTime(hour = state.hour, minute = state.minute))
                    ?: true
            ) {
                Text("Set")
            }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } },
        title = { Text(title) },
        text = {
            TimePicker(
                modifier = modifier,
                state = state
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
private fun SelectableDates.isValidSelection(utcMilliFirst: Long?, utcMilliSecond: Long?): Boolean {
    return isSelectableDate(utcMilliFirst ?: return false) && isSelectableDate(
        utcMilliSecond ?: return false
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DomainDatePicker(
    onDismiss: () -> Unit,
    onSubmit: (ClosedRange<LocalDate>) -> Unit,
    onFitToData: () -> Unit,
    state: DateRangePickerState = rememberDateRangePickerState(),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .safeGesturesPadding()
            .semantics {
                testTag = CounterHistoryTestTags.DomainDatePicker
                dismiss { onDismiss(); true }
            }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, "Close")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = onFitToData,
                            modifier = Modifier.testTag(CounterHistoryTestTags.DomainFitToData)
                        ) {
                            Text("Fit to Data")
                        }
                        TextButton(
                            onClick = {
                                onSubmit(
                                    epochMillisToDate(
                                        state.selectedStartDateMillis!!,
                                        TimeZone.UTC
                                    ).rangeTo(
                                        epochMillisToDate(
                                            state.selectedEndDateMillis!!,
                                            TimeZone.UTC
                                        )
                                    )
                                )
                            },
                            enabled = state.selectableDates.isValidSelection(
                                state.selectedStartDateMillis,
                                state.selectedEndDateMillis
                            ),
                            modifier = Modifier.testTag(CounterHistoryTestTags.DomainPickerSave)
                        ) {
                            Text(text = "Save")
                        }
                    }
                )
            },
        ) { padding ->
            Surface(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                    DateRangePicker(
                        state = state,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DomainDatePickerPreview() {
    MessyInkTheme {
        Surface {
            DomainDatePicker(
                onDismiss = {},
                onSubmit = {},
                onFitToData = {},
            )
        }
    }
}

@Preview
@Composable
private fun TickAmountOverTimePreview() {
    MessyInkTheme {
        Surface {
            val ticks = previewUiTicks(1L).take(10).toList()
            val domain = TimeDomain(ticks.last().timeForData .. ticks.first().timeForData)
            TicksOverTimeLayout(
                graphPoints = listOf(),
                pointInfo = { "" },
                range = ticks.first().amount .. ticks.last().amount,
                domain = domain,
                domainLimits = domain,
                changeDomain = {},
            )
        }
    }
}
