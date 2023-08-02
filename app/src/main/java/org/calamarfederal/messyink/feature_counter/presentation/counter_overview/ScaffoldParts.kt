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
import androidx.compose.ui.res.stringResource
import org.calamarfederal.messyink.R


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
        text = { Text(stringResource(R.string.counter_create_nav_label)) },
        icon = { Icon(Icons.Filled.Add, null) },
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
        title = { Text(stringResource(R.string.counter_overview_title)) },
        scrollBehavior = scrollBehavior,
    )
}
