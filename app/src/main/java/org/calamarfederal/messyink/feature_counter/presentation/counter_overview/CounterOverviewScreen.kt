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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@Preview
@Composable
private fun CounterOverviewPreview() {
    MessyInkTheme {
        CounterOverviewScreen(
            counters = previewUiCounters.take(5).toList(),
            tickSums = mapOf(),
            selectedCounter = null,
            ticksOfSelected = listOf(),
            onSelectCounter = {},
            onDeleteCounter = {},
            onClearCounterTicks = {},
            onNavigateToCounterDetails = {},
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
    selectedCounter: UiCounter?,
    ticksOfSelected: List<UiTick>,
    onSelectCounter: (UiCounter) -> Unit,
    onDeleteCounter: (UiCounter) -> Unit,
    onClearCounterTicks: (UiCounter) -> Unit,
    onNavigateToCounterDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberStandardBottomSheetState(initialValue = Hidden, skipHiddenState = false)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val bottomSheetScope = rememberCoroutineScope()

    LaunchedEffect(selectedCounter?.id) {
        if (selectedCounter == null && sheetState.isVisible) {
            bottomSheetScope.launch {
                sheetState.hide()
            }
        } else if (selectedCounter != null) {
            bottomSheetScope.launch {
                sheetState.show()
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            CounterDetails(
                counter = selectedCounter ?: UiCounter(name = "???", id = 100L),
                ticks = ticksOfSelected,
                ticksSum = null,
            )
        },
    ) { sheetPadding ->
        Scaffold(
            modifier = modifier
                .padding(sheetPadding)
                .consumeWindowInsets(sheetPadding),
        ) { padding ->
            Surface(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                CounterOverviewLayout(
                    counters = counters,
                    tickSums = tickSums,
                    onSelectCounter = {
                        if (selectedCounter?.id != it.id)
                            onSelectCounter(it)
                        else onNavigateToCounterDetails(it.id)
                    },
                    onDeleteCounter = onDeleteCounter,
                    onClearCounterTicks = onClearCounterTicks,
                    modifier = modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CounterOverviewLayout(
    counters: List<UiCounter>,
    tickSums: Map<Long, Double>,
    onSelectCounter: (UiCounter) -> Unit,
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
                        onClick = {
                            onSelectCounter(counter)
                        },
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
