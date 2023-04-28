package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipBorder
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import org.calamarfederal.messyink.ui.theme.TonalElevation

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

                CounterOptions(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterOptions(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible = visible, modifier = modifier) {
        Popup(
            onDismissRequest = onDismiss,
            alignment = Alignment.BottomCenter,
        ) {
            ElevatedCard {
                Row {
                    InputChip(
                        selected = false,
                        onClick = onClear,
                        border = null,
                        label = { Text("Clear") },
                        leadingIcon = { Icon(Icons.Filled.ClearAll, "clear all") },
                    )
//                    Divider()
                    InputChip(
                        selected = false,
                        onClick = onDelete,
                        border = null,
                        label = { Text("Delete") },
                        leadingIcon = {
                            Icon(Icons.Filled.DeleteForever, "delete and its contents")
                        }
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterListItem(
    counter: UiCounter,
    modifier: Modifier = Modifier,
    summaryNumber: Double? = null,
    selected: Boolean = false,
) {
    ListItem(
        modifier = modifier,
        headlineText = { Text(text = counter.name) },
        leadingContent = summaryNumber?.let { { Text(text = it.toStringAllowShorten()) } },
        tonalElevation = if (!selected)
            LocalAbsoluteTonalElevation.current
        else
            TonalElevation.heightOfNext(LocalAbsoluteTonalElevation.current, minimumLayer = 2)
    )
}
