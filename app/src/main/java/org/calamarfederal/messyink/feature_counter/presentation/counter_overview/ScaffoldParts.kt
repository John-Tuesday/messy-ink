package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
internal fun CounterOverviewFAB(
    expanded: Boolean,
    onCreateCounter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = onCreateCounter,
        expanded = expanded,
        text = { Text("Create Counter") },
        icon = { Icon(Icons.Filled.Add, "create counter") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CounterOverviewAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text("Counters") },
        scrollBehavior = scrollBehavior,
    )
}
