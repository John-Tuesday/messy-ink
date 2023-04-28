package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks

@Preview
@Composable
private fun PreviewDetails() {
    val counter = previewUiCounters.first()
    val ticks = previewUiTicks(1L).take(15).toList()
    val ticksSum = ticks.sumOf { it.amount }
    CounterDetails(
        counter = counter,
        ticks = ticks,
        ticksSum = ticksSum,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CounterDetails(
    counter: UiCounter,
    ticks: List<UiTick>,
    ticksSum: Double?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        stickyHeader(key = counter.id) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Start)) {
                Text(text = counter.name)
                Text(text = ticksSum?.toStringAllowShorten() ?: "--")
            }
        }
        items(items = ticks, key = { it.id }) { tick ->
            TickListItem(tick = tick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TickListItem(
    tick: UiTick,
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    supportStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    headlineStyle: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            Text(
                text = "${tick.id}"
            )
        },
        headlineContent = {
            Text(
                text = tick.amount.toStringAllowShorten(),
                style = LocalTextStyle.current + headlineStyle
            )
        },
        supportingContent = {
            Row {
                Text(
                    text = "created: ${tick.timeCreated.toLocalDateTime(timeZone).date}",
                    style = LocalTextStyle.current + supportStyle,
                )
                Text(
                    text = "for data: ${tick.timeForData.toLocalDateTime(timeZone).date}",
                    style = LocalTextStyle.current + supportStyle,
                )
            }
        },
    )
}
