package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.presentation.compose.LocalTimeZone
import org.calamarfederal.messyink.common.presentation.compose.charts.GraphSize2d
import org.calamarfederal.messyink.common.presentation.compose.charts.LineGraph
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.common.presentation.format.omitWhen
import org.calamarfederal.messyink.common.presentation.time.toUtcMillis
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.TickGraphState
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TicksOverTimeLayout(
    tickSort: TickSort,
    graphState: TickGraphState,
    pointInfo: (Int) -> String,
    changeDomain: (ClosedRange<Instant>) -> Unit,
    changeDomainToFit: () -> Unit,
    modifier: Modifier = Modifier,
    graphSize: GraphSize2d = GraphSize2d(
        xAxisAt = -(graphState.currentRange.start / (graphState.currentRange.start - graphState.currentRange.endInclusive).absoluteValue).toFloat()
    ),
    timeZone: TimeZone = LocalTimeZone.current,
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
                lineGraphPoints = graphState.graphPoints,
                pointInfo = {
                    if (showPointInfo) pointInfo(it) else null
                },
                size = graphSize,
                title = {
                    Text(
                        text = "Amount vs ${
                            when (tickSort) {
                                TickSort.TimeForData -> "Time Data"
                                TickSort.TimeCreated -> "Time Created"
                                TickSort.TimeModified -> "Time Modified"
                            }
                        }",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                rangeSlotIndexed = {
                    if (it == 0 || it == graphSize.yAxisChunks - 1) {
                        Text(
                            text = if (it == 0) "${graphState.currentRange.start}" else "${graphState.currentRange.endInclusive}",
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight(align = if (it == 0) Alignment.Bottom else Alignment.Top)
                        )
                    }
                }
            )

//          Show current domain min and max and allow edit
            val localDomain by remember(graphState.currentDomain, timeZone) {
                derivedStateOf {
                    graphState.currentDomain.let {
                        it.start.toLocalDateTime(timeZone) .. it.endInclusive.toLocalDateTime(
                            timeZone
                        )
                    }
                }
            }
            val selectableDates by remember(graphState.domainBounds, timeZone) {
                derivedStateOf {
                    graphState.domainBounds.let {
                        selectableDatesInRange(
                            it.start.toLocalDateTime(timeZone).date,
                            it.endInclusive.toLocalDateTime(timeZone).date
                        )
                    }
                }
            }

            DomainBoundsAndPicker(
                localDomain = localDomain,
                selectableDates = selectableDates,
                changeDomain = {
                    changeDomain(
                        it.start.atStartOfDayIn(timeZone) .. it.endInclusive.atStartOfDayIn(timeZone)
                    )
                },
                changeDomainToFit = changeDomainToFit,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DomainBoundsAndPicker(
    localDomain: ClosedRange<LocalDateTime>,
    selectableDates: SelectableDates,
    changeDomain: (ClosedRange<LocalDate>) -> Unit,
    changeDomainToFit: () -> Unit,
    modifier: Modifier = Modifier,
    dateTimeFormat: DateTimeFormat = DateTimeFormat().omitWhen(
        localDomain.start,
        localDomain.endInclusive,
    ),
) {
    var openDomainPicker by remember { mutableStateOf(false) }
    val domainStart by remember(localDomain, dateTimeFormat) {
        derivedStateOf {
            localDomain.start.formatToString(dateTimeFormat)
        }
    }
    val domainEnd by remember(localDomain, dateTimeFormat) {
        derivedStateOf {
            localDomain.endInclusive.formatToString(dateTimeFormat)
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
        val domainState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = localDomain.start.date.toUtcMillis(),
            initialSelectedEndDateMillis = localDomain.endInclusive.date.toUtcMillis(),
            selectableDates = selectableDates,
        )
        DomainDatePicker(
            state = domainState,
            onDismiss = { openDomainPicker = false },
            onFitToData = { changeDomainToFit(); openDomainPicker = false },
            onSubmit = {
                changeDomain(it)
                openDomainPicker = false
            },
        )
    }
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
            TicksOverTimeLayout(
                tickSort = TickSort.TimeForData,
                graphState = TickGraphState(),
                pointInfo = { "" },
                changeDomain = {},
                changeDomainToFit = {},
            )
        }
    }
}
