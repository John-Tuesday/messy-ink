package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toInstant
import org.calamarfederal.messyink.common.compose.charts.GraphSize2d
import org.calamarfederal.messyink.common.compose.charts.LineGraph
import org.calamarfederal.messyink.common.compose.charts.PointByPercent
import org.calamarfederal.messyink.common.compose.localToString
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeZoneGetter
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainAgoTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.ui.theme.MessyInkTheme


@Composable
internal fun TicksOverTimeLayout(
    ticks: List<UiTick>,
    range: ClosedRange<Double>,
    domain: TimeDomain,
    domainOptions: List<TimeDomainTemplate>,
    changeDomain: (TimeDomain) -> Unit,
    modifier: Modifier = Modifier,
    graphSize: GraphSize2d = GraphSize2d(),
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            var showPointInfo by remember { mutableStateOf(false) }

//          Graph: Amount v Time
            LineGraph(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showPointInfo = !showPointInfo },
                lineGraphPoints = ticks.map {
                    PointByPercent(
                        x = (it.timeForData - domain.start) / (domain.endInclusive - domain.start),
                        y = (it.amount - range.start) / (range.endInclusive - range.start),
                    )
                },
                pointInfo = {
                    if (showPointInfo) ticks[it].amount.toString() else null
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
                changeDomain = changeDomain,
                modifier = Modifier.fillMaxWidth(),
            )

//             Incomplete:
//             * 2 value Slider to quickly adjust the domain
//             * should be bounded by and iterate through the real data points
//            RangeSlider(
//                value = 0f .. 1f,
//                onValueChange = {},
//                enabled = false,
//                colors = SliderDefaults.colors(
//                    thumbColor = MaterialTheme.colorScheme.tertiary,
//                    activeTrackColor = MaterialTheme.colorScheme.tertiary,
//                ),
//                modifier = Modifier.safeGesturesPadding()
//            )

            DomainDropdownMenu(
                domainLabel = domain.label,
                domainOptions = domainOptions,
                onClick = { changeDomain(it.domain()) },
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DomainBoundsAndPicker(
    domain: TimeDomain,
    changeDomain: (TimeDomain) -> Unit,
    modifier: Modifier = Modifier,
) {
    var setDomainMin by remember { mutableStateOf(false) }
    var setDomainMax by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(onClick = { setDomainMin = true }) {
            Text(domain.start.localToString())
        }
        TextButton(onClick = { setDomainMax = true }) {
            Text(domain.endInclusive.localToString())
        }
    }
    TimeDomainPicker(
        visible = setDomainMax || setDomainMin,
        initial = with(CurrentTimeZoneGetter()) {
            if (setDomainMax)
                domain.endInclusive.toLocalDateTime()
            else
                domain.start.toLocalDateTime()
        },
        onCancel = { setDomainMax = false; setDomainMin = false },
        onSubmit = {
            val bound = it.toInstant(CurrentTimeZoneGetter())
            if (setDomainMax)
                changeDomain(domain.copy(endInclusive = bound))
            else
                changeDomain(domain.copy(start = bound))
            setDomainMax = false
            setDomainMin = false
        },
    )
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
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                null,
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDomainPicker(
    visible: Boolean,
    initial: LocalDateTime,
    onCancel: () -> Unit,
    onSubmit: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Set Time",
    state: TimePickerState = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
    ),
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onCancel,
            confirmButton = {
                TextButton(onClick = {
                    onSubmit(
                        LocalDateTime(
                            date = initial.date,
                            time = LocalTime(hour = state.hour, minute = state.minute)
                        )
                    )
                }) {
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
}

@Preview
@Composable
private fun TickAmountOverTimePreview() {
    MessyInkTheme {
        Surface {
            val ticks = previewUiTicks(1L).take(10).toList()
            val domain = TimeDomain(
                domain = ticks.last().timeForData .. ticks.first().timeForData,
                label = "Squeeze"
            )
            TicksOverTimeLayout(
                ticks = ticks,
                range = ticks.first().amount .. ticks.last().amount,
                domain = domain,
                domainOptions = TimeDomainAgoTemplate.Defaults,
                changeDomain = {},
            )
        }
    }
}
