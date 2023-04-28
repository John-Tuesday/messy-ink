package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
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
            )
        }
    }
}

@Composable
private fun CounterOverviewLayout(
    counters: List<UiCounter>,
    tickSums: Map<Long, Double>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(items = counters, key = { it.id }) { counter ->
            CounterListItem(
                counter = counter,
                summaryNumber = tickSums[counter.id],
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterListItem(
    counter: UiCounter,
    modifier: Modifier = Modifier,
    summaryNumber: Double? = null,
) {
    ListItem(
        modifier = modifier,
        headlineText = { Text(text = counter.name) },
        leadingContent = summaryNumber?.let { { Text(text = it.toStringAllowShorten()) } },
    )
}
