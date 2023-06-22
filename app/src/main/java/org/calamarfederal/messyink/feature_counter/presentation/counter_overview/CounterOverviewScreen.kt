package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme


/**
 * # Overview of all [UiCounter]
 *
 * provides brief stats and quick actions for each [UiCounter]. Nexus for more detailed screens
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CounterOverviewScreen(
    counters: List<UiCounter>,
    tickSums: Map<Long, Double>,
    onCounterIncrement: (UiCounter) -> Unit,
    onCounterDecrement: (UiCounter) -> Unit,
    onDeleteCounter: (UiCounter) -> Unit,
    onClearCounterTicks: (UiCounter) -> Unit,
    onCreateCounter: () -> Unit,
    onNavigateToCounterDetails: (Long) -> Unit,
    onNavigateToCounterGameMode: (Long) -> Unit,
    onNavigateToCounterEdit: (Long) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fabExpand by remember { derivedStateOf { scrollBehavior.state.collapsedFraction < 0.9f } }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { CounterOverviewAppBar(scrollBehavior = scrollBehavior) },
        floatingActionButton = {
            CounterOverviewFAB(
                expanded = fabExpand,
                onCreateCounter = onCreateCounter,
                modifier = Modifier.testTag(CounterOverviewTestTags.CreateCounterFab)
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            CounterOverviewLayout(
                counters = counters,
                tickSums = tickSums,
                onCounterIncrement = onCounterIncrement,
                onCounterDecrement = onCounterDecrement,
                onViewHistory = { onNavigateToCounterDetails(it.id) },
                onViewInFull = { onNavigateToCounterGameMode(it.id) },
                onEditCounter = { onNavigateToCounterEdit(it.id) },
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
    onCounterIncrement: (UiCounter) -> Unit,
    onCounterDecrement: (UiCounter) -> Unit,
    onClearCounterTicks: (UiCounter) -> Unit,
    onDeleteCounter: (UiCounter) -> Unit,
    onViewHistory: (UiCounter) -> Unit,
    onViewInFull: (UiCounter) -> Unit,
    onEditCounter: (UiCounter) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandIndex by remember { mutableLongStateOf(-1L) }

    LazyColumn(
        modifier = modifier.testTag(CounterOverviewTestTags.CountersContainer),
        verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items = counters, key = { it.id }) { counter ->
            Box(Modifier.padding(horizontal = 16.dp)) {
                var showOptions by remember { mutableStateOf(false) }
                val haptic = LocalHapticFeedback.current

                CounterListCard(
                    counter = counter,
                    amount = tickSums[counter.id],
//                    expanded = expandIndex == counter.id,
                    onIncrement = { onCounterIncrement(counter) },
                    onDecrement = { onCounterDecrement(counter) },
                    onHistory = { onViewHistory(counter) },
                    onEditCounter = { onEditCounter(counter) },
                    onViewInFull = { onViewInFull(counter) },
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showOptions = true
                                expandIndex = counter.id
                            },
                            onClick = {
                                expandIndex = if (expandIndex == counter.id) -1 else counter.id
                            },
                            onDoubleClick = { onViewHistory(counter) }
                        )
                        .testTag(CounterOverviewTestTags.CounterItem)
                )

                CounterOptions(
                    visible = showOptions,
                    onDismiss = { showOptions = false },
                    onDetails = { onViewHistory(counter); showOptions = false },
                    onGameMode = { onViewInFull(counter); showOptions = false },
                    onEdit = { onEditCounter(counter); showOptions = false; },
                    onDelete = { onDeleteCounter(counter); showOptions = false },
                    onClear = { onClearCounterTicks(counter); showOptions = false },
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
            onCounterIncrement = {},
            onCounterDecrement = {},
            onDeleteCounter = {},
            onClearCounterTicks = {},
            onCreateCounter = {},
            onNavigateToCounterDetails = {},
            onNavigateToCounterGameMode = {},
            onNavigateToCounterEdit = {},
        )
    }
}
