package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick



@Composable
internal fun TickDetailsLayout(
    ticks: List<UiTick>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = ticks, key = {it.id}) {tick ->
            TickListItem(tick)
        }
    }
}

@Composable
private fun TickListItem(
    tick: UiTick,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text("${tick.amount}") },
    )
}
