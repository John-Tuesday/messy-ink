package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import kotlin.math.roundToInt

@Preview
@Composable
private fun PreviewDetailsLayout() {
    val counter = previewUiCounters.first()
    var tickSum = 0.00
    val ticks = previewUiTicks(counter.id).onEach { tickSum += it.amount }.take(7).toList()
    TabbedCounterDetailsLayout(
        counter = previewUiCounters.first(),
        ticks = ticks,
        totalSum = tickSum,
        totalAverage = tickSum / ticks.size,
    )
}

@Composable
fun TabbedCounterDetailsLayout(
    counter: UiCounter,
    ticks: List<UiTick>,
    totalSum: Double?,
    totalAverage: Double?,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var dragTotal by remember(selectedIndex) { mutableStateOf(0f) }
    val dragThreshold = with(LocalDensity.current) { 32.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(selectedIndex) {
                detectHorizontalDragGestures(
                    onDragStart = {},
                    onDragCancel = { dragTotal = 0f },
                    onDragEnd = {
                        if (dragTotal <= dragThreshold)
                            selectedIndex = selectedIndex
                                .dec()
                                .coerceAtLeast(1)
                        else if (dragTotal >= -dragThreshold)
                            selectedIndex = selectedIndex
                                .inc()
                                .coerceAtMost(0)
                        dragTotal = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragTotal += dragAmount
                    }
                )
            }
    ) {

        AnimatedContent(
            targetState = selectedIndex,
            modifier = Modifier.fillMaxWidth(),
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { width ->
                    if (initialState > targetState) -width
                    else width
                }) togetherWith slideOutHorizontally { width ->
                    if (initialState > targetState) width
                    else -width
                }
            },
            label = "tabrows",
        ) {
            when (it) {
                0 -> TickDetailsLayout(ticks = ticks)
                1 -> GameCounterTab(counter = counter, tickSum = totalSum)
                else -> {}
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        TabRow(
            selectedTabIndex = selectedIndex,
        ) {
            Tab(
                selected = selectedIndex == 0,
                onClick = { selectedIndex = 0 },
                text = { Text("TickDetails") }
            )
            Tab(
                selected = selectedIndex == 1,
                onClick = { selectedIndex = 1 },
                text = { Text("GameCounter") }
            )
        }
    }
}
