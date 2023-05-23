package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.compose.charts.BasicLineGraph
import org.calamarfederal.messyink.common.compose.charts.LineGraph
import org.calamarfederal.messyink.common.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import kotlin.math.min

@Composable
internal fun TicksOverTimeLayout(
    ticks: List<UiTick>,
    modifier: Modifier = Modifier,
    minTime: Instant = ticks.minOf { it.timeForData },
    maxTime: Instant = ticks.maxOf { it.timeForData },
    minAmount: Double = min(0.00, ticks.minOf { it.amount }),
    maxAmount: Double = ticks.maxOf { it.amount },
) {
    Surface(modifier = modifier.fillMaxSize()) {
        TickAmountOverTime(
            ticks = ticks,
            minTime = minTime,
            maxTime = maxTime,
            minAmount = minAmount,
            maxAmount = maxAmount,
        )
    }
}

@Composable
fun TickAmountOverTime(
    ticks: List<UiTick>,
    modifier: Modifier = Modifier,
    minTime: Instant = ticks.minOf { it.timeForData },
    maxTime: Instant = ticks.maxOf { it.timeForData },
    minAmount: Double = min(0.00, ticks.minOf { it.amount }),
    maxAmount: Double = ticks.maxOf { it.amount },
) {
    val timeRange = maxTime - minTime
    val amountRange = maxAmount - minAmount

    LineGraph(
        modifier = modifier,
        lineGraphPoints = ticks.map {
            PointByPercent(
                x = (it.timeForData - minTime) / timeRange,
                y = (it.amount - minAmount) / amountRange,
            )
        },
        title = { Text("Amount Over Time") },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
private fun TickAmountOverTimePreview() {
    MessyInkTheme {
        Scaffold {
            TicksOverTimeLayout(
                ticks = previewUiTicks(1L).take(10).toList(),
                modifier = Modifier.padding(it).consumeWindowInsets(it).padding(16.dp),
            )
        }
    }
}
