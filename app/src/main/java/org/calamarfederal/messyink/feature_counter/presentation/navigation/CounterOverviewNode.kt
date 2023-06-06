package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewScreen
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewViewModel

/**
 * # Counter Overview Node
 *
 * Provide high level summary of all Counters and act as a nexus for navigation
 */
internal object CounterOverviewNode : CounterNavNode {
    override val route: String = "counter_overview"

    fun NavHostController.navigateToCounterOverview(
        navOptions: NavOptions = navOptions {
            launchSingleTop = true
        }
    ) {
        navigate(this@CounterOverviewNode.route, navOptions)
    }

    fun NavGraphBuilder.counterOverview(
        navController: NavHostController,
        onNavigateToCreateCounter: () -> Unit,
        onNavigateToCounterDetails: (Long) -> Unit,
        onNavigateToCounterGameMode: (Long) -> Unit,
        onNavigateToCounterEdit: (Long) -> Unit,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        subNavNode { entry ->
            onEntry(entry)

            val viewModel: CounterOverviewViewModel = hiltViewModel()
            val counters by viewModel.countersState.collectAsState()
            val tickSums by viewModel.ticksSumState.collectAsState()

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
