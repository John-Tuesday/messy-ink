package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.common.compose.charts.LineGraph
import org.calamarfederal.messyink.common.compose.charts.PointByPercent
import org.calamarfederal.messyink.common.compose.toDbgString
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import kotlin.math.min

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
    )
}

@Preview
@Composable
private fun TickAmountOverTimePreview() {
    MessyInkTheme {
        Surface {
            TickAmountOverTime(
                ticks = previewUiTicks(1L).take(10).toList(),
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
            )
        }
    }
}
