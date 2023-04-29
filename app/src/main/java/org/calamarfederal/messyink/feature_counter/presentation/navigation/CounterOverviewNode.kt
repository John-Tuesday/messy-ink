package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewScreen
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewViewModel

internal object CounterOverviewNode : CounterNavNode(route = "counter_overview") {
    fun NavHostController.navigateToCounterOverview() {
        navigate(this@CounterOverviewNode.route, navOptions { launchSingleTop = true })
    }

    fun NavGraphBuilder.counterOverview(
        navController: NavHostController,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        subNavNode { entry ->
            onEntry(entry)

            val viewModel: CounterOverviewViewModel = hiltViewModel()
            val counters by viewModel.countersState.collectAsState()
            val tickSums by viewModel.ticksSumState.collectAsState()
            val selectedCounter by viewModel.selectedCounter.collectAsState()
            val ticksOfSelected by viewModel.ticksOfSelectedCounter.collectAsState()

            CounterOverviewScreen(
                counters = counters,
                tickSums = tickSums,
                selectedCounter = selectedCounter,
                ticksOfSelected = ticksOfSelected ?: listOf(),
                onSelectCounter = viewModel::selectCounter,
                onDeleteCounter = { viewModel.deleteCounter(it.id) },
                onClearCounterTicks = { viewModel.clearCounterTicks(it.id) },
            )
        }
    }
}
