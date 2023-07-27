package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewScreen
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewViewModel

/**
 * # Counter Overview Node
 *
 * Provide high level summary of all Counters and act as a nexus for navigation
 */
internal data object CounterOverviewNode : CounterGraphNode {
    override val route: String = "counter_overview"

    fun NavController.navigateToCounterOverview(
        navOptions: NavOptions? = navOptions { launchSingleTop = true },
    ) {
        navigate(this@CounterOverviewNode.route, navOptions)
    }

    fun NavGraphBuilder.counterOverview(
        onNavigateToCreateCounter: () -> Unit,
        onNavigateToCounterDetails: (Long) -> Unit,
        onNavigateToCounterGameMode: (Long) -> Unit,
        onNavigateToCounterEdit: (Long) -> Unit,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        composable(
            route = CounterOverviewNode.route,
            arguments = CounterOverviewNode.arguments,
            deepLinks = CounterOverviewNode.deepLinks,
        ) { entry ->
            onEntry(entry)

            val viewModel: CounterOverviewViewModel = hiltViewModel()
            val counters by viewModel.countersState.collectAsStateWithLifecycle()
            val tickSums by viewModel.ticksSumState.collectAsStateWithLifecycle()

            CounterOverviewScreen(
                counters = counters,
                tickSums = tickSums,
                onCounterIncrement = { viewModel.incrementCounter(it.id) },
                onCounterDecrement = { viewModel.decrementCounter(it.id) },
                onDeleteCounter = { viewModel.deleteCounter(it.id) },
                onClearCounterTicks = { viewModel.clearCounterTicks(it.id) },
                onCreateCounter = { onNavigateToCreateCounter() },
                onNavigateToCounterDetails = onNavigateToCounterDetails,
                onNavigateToCounterGameMode = onNavigateToCounterGameMode,
                onNavigateToCounterEdit = onNavigateToCounterEdit,
            )
        }
    }
}
