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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@Preview
@Composable
private fun CounterOverviewPreview() {
    MessyInkTheme {
        CounterOverviewScreen(
            counters = previewUiCounters.take(5).toList(),
            tickSums = mapOf(),
            onDeleteCounter = {},
            onClearCounterTicks = {},
        )
    }
}

/**
 * Screen for browsing Counters
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CounterOverviewScreen(
    counters: List<UiCounter>,
    tickSums: Map<Long, Double>,
    onDeleteCounter: (UiCounter) -> Unit,
    onClearCounterTicks: (UiCounter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            CounterOverviewLayout(
                counters = counters,
                tickSums = tickSums,
                onDeleteCounter = onDeleteCounter,
                onClearCounterTicks = onClearCounterTicks,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CounterOverviewLayout(
    counters: List<UiCounter>,
    tickSums: Map<Long, Double>,
    onClearCounterTicks: (UiCounter) -> Unit,
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
                        onClick = {},
                    )
                )

                CounterOptionsPopup(
                    visible = showOptions,
                    onDismiss = { showOptions = false },
                    onDelete = { onDeleteCounter(counter) },
                    onClear = { onClearCounterTicks(counter) },
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }
    }
}
