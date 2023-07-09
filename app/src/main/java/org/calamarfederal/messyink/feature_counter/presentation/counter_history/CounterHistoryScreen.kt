package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryTab.TickLogs
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryTab.TickGraphs
import org.calamarfederal.messyink.feature_counter.presentation.state.AllTime
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.error

/**
 * # Counter History and Details Screen
 *
 * ## focus on [counter] and display its details at various levels of depth, as well as
 * ## offer tabs for basic interaction
 *
 * [ticks] should be the ticks of [counter]
 * if the [ticks] is empty [tickSum] and [tickAverage] may be null
 */
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CounterHistoryScreen(
    counter: UiCounter,
    ticks: List<UiTick>,
    tickEdit: UiTickSupport?,
    tickSum: Double?,
    tickAverage: Double?,
    graphPoints: List<PointByPercent>,
    graphRange: ClosedRange<Double>,
    graphDomain: TimeDomain,
    graphDomainLimits: TimeDomain,
    changeGraphDomain: (TimeDomain) -> Unit,
    onAddTick: (Double) -> Unit,
    onDeleteTick: (Long) -> Unit,
    onEditTick: (Long) -> Unit,
    onEditTickChanged: (UiTickSupport) -> Unit,
    onCancelEditTick: () -> Unit,
    onFinalizeEditTick: () -> Unit,
    onResetCounter: () -> Unit,
    onCounterChange: (UiCounter) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = CounterHistoryTab::size)
    val currentIndex by remember { derivedStateOf(calculation = pagerState::currentPage) }
    val tabScope = rememberCoroutineScope()

    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topBarState,
        snapAnimationSpec = tween(easing = LinearOutSlowInEasing),
    )

    LaunchedEffect(currentIndex) {
        topBarState.contentOffset = 0f
        topBarState.heightOffset = 0f
    }

    Scaffold(
        modifier = modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            HistoryTopBar(
                selectedIndex = currentIndex,
                onNavigateUp = onNavigateUp,
                scrollBehavior = topBarScrollBehavior,
            )
        },
        bottomBar = {
            CounterHistoryNavBar(
                selectedIndex = currentIndex,
                onChangeSelect = { tabScope.launch { pagerState.animateScrollToPage(it) } },
            )
        },
    ) { padding ->
        TabbedLayout(
            counter = counter,
            ticks = ticks,
            tickSupport = tickEdit,
            tickSum = tickSum,
            tickAverage = tickAverage,
            onAddTick = onAddTick,
            onDeleteTick = onDeleteTick,
            onEditTick = onEditTick,
            onEditTickChanged = onEditTickChanged,
            onFinalizeEditTick = onFinalizeEditTick,
            onCancelEditTick = onCancelEditTick,
            onResetCounter = onResetCounter,
            state = pagerState,
            onCounterChange = onCounterChange,
            graphPoints = graphPoints,
            graphDomain = graphDomain,
            graphDomainLimits = graphDomainLimits,
            changeGraphDomain = changeGraphDomain,
            graphRange = graphRange,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TabbedLayout(
    counter: UiCounter,
    ticks: List<UiTick>,
    tickSupport: UiTickSupport?,
    tickSum: Double?,
    tickAverage: Double?,
    onAddTick: (Double) -> Unit,
    onDeleteTick: (Long) -> Unit,
    onEditTick: (Long) -> Unit,
    onEditTickChanged: (UiTickSupport) -> Unit,
    onCancelEditTick: () -> Unit,
    onFinalizeEditTick: () -> Unit,
    onResetCounter: () -> Unit,
    onCounterChange: (UiCounter) -> Unit,
    graphPoints: List<PointByPercent>,
    graphDomain: TimeDomain,
    graphDomainLimits: TimeDomain,
    changeGraphDomain: (TimeDomain) -> Unit,
    graphRange: ClosedRange<Double>,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(pageCount = CounterHistoryTab::size),
    userScrollEnabled: Boolean = false,
) {
    HorizontalPager(
        modifier = modifier,
        state = state,
        pageSpacing = 0.dp,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = false,
        contentPadding = PaddingValues(0.dp),
        beyondBoundsPageCount = 0,
        pageSize = PageSize.Fill,
        flingBehavior = PagerDefaults.flingBehavior(state = state),
        key = { it },
        pageContent = { index ->
            when (CounterHistoryTab.fromIndex(index)) {
                TickLogs -> TickLogsLayout(
                    ticks = ticks,
                    onDelete = onDeleteTick,
                    onEdit = onEditTick,
                )

                TickGraphs -> TicksOverTimeLayout(
                    graphPoints = graphPoints,
                    pointInfo = { "${ticks[it].amount}" },
                    range = graphRange,
                    domain = graphDomain,
                    domainLimits = graphDomainLimits,
                    changeDomain = changeGraphDomain,
                )
            }
        }
    )

    if (tickSupport != null) {
        EditTickScreenDialog(
            uiTickSupport = tickSupport,
            onChangeTick = onEditTickChanged,
            onDone = onFinalizeEditTick,
            onClose = onCancelEditTick,
            isDoneEnabled = !tickSupport.error,
        )
//        ModalBottomSheet(onDismissRequest = onCancelEditTick) {
//            EditTickScreen(
//                uiTickSupport = tickSupport,
//                onChangeTick = onEditTickChanged,
//                onDone = onFinalizeEditTick,
//                onClose = onCancelEditTick,
//                isDoneEnabled = !tickSupport.error,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    selectedIndex: Int,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = { Text(CounterHistoryTab.fromIndex(selectedIndex).displayName) },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Filled.ArrowBack, "navigate up")
            }
        },
    )
}

@Preview
@Composable
private fun CounterHistoryScreenPreview() {
    val counter = previewUiCounters.first()
    var tickSum: Double? = null
    val ticks = previewUiTicks(counter.id).take(7).apply {
        tickSum = sumOf { it.amount }
    }.toList()
    val range = ticks.minOf { it.amount } .. ticks.maxOf { it.amount }

    CounterHistoryScreen(
        counter = counter,
        ticks = ticks,
        tickEdit = null,
        graphPoints = listOf(),
        tickSum = tickSum,
        tickAverage = tickSum?.div(ticks.size),
        graphRange = range,
        graphDomain = TimeDomain.AllTime,
        graphDomainLimits = TimeDomain.AllTime,
        changeGraphDomain = {},
        onAddTick = {},
        onDeleteTick = {},
        onEditTickChanged = {},
        onFinalizeEditTick = {},
        onCancelEditTick = {},
        onEditTick = {},
        onResetCounter = {},
        onCounterChange = {},
        onNavigateUp = {},
    )
}
