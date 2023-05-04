package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.GameCounter
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.TestScreen
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.TickDetails

/**
 * # Tabbed Counter Details Screen
 *
 * ## focus on [counter] and display its details at various levels of depth, as well as
 * ## offer tabs for basic interaction
 *
 * [ticks] should be the ticks of [counter]
 * if the [ticks] is empty [tickSum] and [tickAverage] may be null
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun TabbedCounterDetailsScreen(
    counter: UiCounter,
    ticks: List<UiTick>,
    tickSum: Double?,
    tickAverage: Double?,
    onAddTick: (Double) -> Unit,
    onDeleteTick: (Long) -> Unit,
    onResetCounter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState()
    val currentIndex by remember { derivedStateOf(calculation = pagerState::currentPage) }
    val offsetFraction by remember { derivedStateOf(calculation = pagerState::currentPageOffsetFraction) }
    val tabScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            CounterDetailsTabRow(
                selectedIndex = selectedIndex,
                onChangeSelect = { tabScope.launch { pagerState.animateScrollToPage(it) } },
                indicator = tabIndicator(
                    selectedIndex = selectedIndex,
                    currentIndex = currentIndex,
                    offsetFraction = offsetFraction,
                    onChangeSelect = { selectedIndex = it }
                ),
            )
        },
        bottomBar = {},
    ) { padding ->
        DetailsLayout(
            counter = counter,
            ticks = ticks,
            tickSum = tickSum,
            tickAverage = tickAverage,
            onAddTick = onAddTick,
            onDeleteTick = onDeleteTick,
            onResetCounter = onResetCounter,
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DetailsLayout(
    counter: UiCounter,
    ticks: List<UiTick>,
    tickSum: Double?,
    tickAverage: Double?,
    onAddTick: (Double) -> Unit,
    onDeleteTick: (Long) -> Unit,
    onResetCounter: () -> Unit,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(),
    userScrollEnabled: Boolean = true,
) {
    HorizontalPager(
        modifier = modifier,
        state = state,
        pageCount = CounterDetailsTab.values().size,
        userScrollEnabled = userScrollEnabled,
        key = { it },
    ) {
        when (CounterDetailsTab.fromIndex(it)) {
            TickDetails -> TickDetailsLayout(
                ticks = ticks,
                onDelete = onDeleteTick,
                onEdit = {},
            )

            GameCounter -> GameCounterTab(
                counter = counter,
                tickSum = tickSum,
                onAddTick = onAddTick,
                onResetCounter = onResetCounter,
            )

            TestScreen  -> {
                Surface() {
                    Text("Testing :P")
                }
            }
        }
    }
}

@Preview
@Composable
private fun TabbedPreview() {
    val counter = previewUiCounters.first()
    var tickSum: Double? = null
    val ticks = previewUiTicks(counter.id).take(7).apply {
        tickSum = sumOf { it.amount }
    }.toList()

    TabbedCounterDetailsScreen(
        counter = counter,
        ticks = ticks,
        tickSum = tickSum,
        tickAverage = tickSum?.div(ticks.size),
        onAddTick = {},
        onDeleteTick = {},
        onResetCounter = {},
    )
}
