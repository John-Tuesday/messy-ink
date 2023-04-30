package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.animation.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority.Default
import androidx.compose.foundation.MutatePriority.PreventUserInput
import androidx.compose.foundation.MutatePriority.UserInput
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Dp.Companion
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.GameCounter
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.TestScreen
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.TickDetails

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalTransitionApi::class)
@Composable
fun TabbedCounterDetailsScreen(
    counter: UiCounter,
    ticks: List<UiTick>,
    tickSum: Double?,
    tickAverage: Double?,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val listScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            CounterDetailsTabRow(
                selectedIndex = selectedIndex,
                onChangeSelect = {
                    listScope.launch { listState.animateScrollToItem(it) }
                },
                indicator = testIndicator(selectedIndex, listState) {
                    selectedIndex = it
                    listScope.launch { listState.stopScroll(Default); listState.animateScrollToItem(it) }
                },
                modifier = Modifier,
            )
        },
        bottomBar = {},
    ) { padding ->
        DetailsLayoutTest(
            counter = counter,
            ticks = ticks,
            tickSum = tickSum,
            tickAverage = tickAverage,
            listState = listState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DetailsLayoutTest(
    counter: UiCounter,
    ticks: List<UiTick>,
    tickSum: Double?,
    tickAverage: Double?,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val snappingLayout = remember(listState) { SnapLayoutInfoProvider(listState) }
    val density = LocalDensity.current
    val flingBehavior = remember(snappingLayout, density) {
        tabDetailsFlingBehavior(snappingLayout, density)
    }

    LazyRow(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier,
    ) {
        items(items = CounterDetailsTab.values(), key = { it }) {
            when (it) {
                TickDetails -> TickDetailsLayout(
                    ticks = ticks,
                    modifier = Modifier.fillParentMaxSize()
                )

                GameCounter -> GameCounterTab(
                    counter = counter,
                    tickSum = tickSum,
                    modifier = Modifier.fillParentMaxSize()
                )

                TestScreen  -> {
                    Surface(modifier = Modifier.fillParentMaxSize()) {
                        Text("Testing :P")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun tabDetailsFlingBehavior(
    snapLayout: SnapLayoutInfoProvider,
    density: Density,
) = SnapFlingBehavior(
    snapLayoutInfoProvider = snapLayout,
    lowVelocityAnimationSpec = SpringSpec(stiffness = Spring.StiffnessMediumLow),
    highVelocityAnimationSpec = SplineBasedFloatDecayAnimationSpec(density).generateDecayAnimationSpec(),
    snapAnimationSpec = SpringSpec(),
    density = density,
    shortSnapVelocityThreshold = 64.dp,
)
