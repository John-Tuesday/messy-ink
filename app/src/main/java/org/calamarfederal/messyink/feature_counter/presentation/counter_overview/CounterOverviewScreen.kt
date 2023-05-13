package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FabPosition.Companion
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme


/**
 * # Screen for browsing Counters at a high level
 *
 * should allow navigation to most if not all other (major) screens
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CounterOverviewScreen(
    counters: List<UiCounter>,
    tickSums: Map<Long, Double>,
    onDeleteCounter: (UiCounter) -> Unit,
    onClearCounterTicks: (UiCounter) -> Unit,
    onCreateCounter: () -> Unit,
    onNavigateToCounterDetails: (Long) -> Unit,
    onNavigateToCounterGameMode: (Long) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
//    var fabExpand by remember(scrollBehavior.state.collapsedFraction ) { mutableStateOf(counters.isEmpty() || tickSums.isEmpty()) }
    val fabExpand by remember { derivedStateOf { scrollBehavior.state.collapsedFraction < 0.9f } }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { CounterOverviewAppBar(scrollBehavior = scrollBehavior) },
        floatingActionButton = {
            CounterOverviewFAB(
                expanded = fabExpand,
                onCreateCounter = onCreateCounter
            )
        },
        floatingActionButtonPosition = if (fabExpand) FabPosition.Center else FabPosition.End,
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            CounterOverviewLayout(
                counters = counters,
                tickSums = tickSums,
                onCounterDetails = { onNavigateToCounterDetails(it.id) },
                onCounterGameMode = { onNavigateToCounterGameMode(it.id) },
                onDeleteCounter = onDeleteCounter,
                onClearCounterTicks = onClearCounterTicks,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CounterOverviewLayout(
    counters: List<UiCounter>,
    tickSums: Map<Long, Double>,
    onClearCounterTicks: (UiCounter) -> Unit,
    onCounterDetails: (UiCounter) -> Unit,
    onCounterGameMode: (UiCounter) -> Unit,
    onDeleteCounter: (UiCounter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items = counters, key = { it.id }) { counter ->
            Box {
                var showOptions by remember { mutableStateOf(false) }
                val haptic = LocalHapticFeedback.current

                CounterListItem(
                    counter = counter,
                    summaryNumber = tickSums[counter.id],
                    selected = showOptions,
                    modifier = Modifier.combinedClickable(
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showOptions = true
                        },
                        onClick = { onCounterDetails(counter) },
                    )
                )

                CounterOptions(
                    visible = showOptions,
                    onDismiss = { showOptions = false },
                    onDetails = { onCounterDetails(counter); showOptions = false },
                    onGameMode = { onCounterGameMode(counter); showOptions = false },
                    onDelete = { onDeleteCounter(counter); showOptions = false },
                    onClear = { onClearCounterTicks(counter); showOptions = false },
                    modifier = Modifier.safeContentPadding(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CounterOverviewPreview() {
    MessyInkTheme {
        CounterOverviewScreen(
            counters = previewUiCounters.take(5).toList(),
            tickSums = mapOf(),
            onDeleteCounter = {},
            onClearCounterTicks = {},
            onCreateCounter = {},
            onNavigateToCounterDetails = {},
            onNavigateToCounterGameMode = {},
        )
    }
}
