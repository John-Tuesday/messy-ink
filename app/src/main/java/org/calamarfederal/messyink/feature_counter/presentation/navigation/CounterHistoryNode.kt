package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryViewModel
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryScreen

/**
 * # History and Details
 * ## of a Counter, including its ticks
 */
internal object CounterHistoryNode : CounterNavNode {
    private const val BASE_ROUTE = "counter_history"

    /**
     * Used as a key in navigation arguments or saved state handel
     */
    const val COUNTER_ID = "counter_id"

    override val arguments = listOf(navArgument(COUNTER_ID) { type = NavType.LongType })
    override val route = "$BASE_ROUTE/{$COUNTER_ID}"

    fun NavHostController.navigateToCounterHistory(counterId: Long) {
        navigate("$BASE_ROUTE/$counterId")
    }

    fun NavGraphBuilder.counterHistory(
        navController: NavHostController,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        subNavNode { entry ->
            onEntry(entry)

            val viewModel: CounterHistoryViewModel = hiltViewModel(entry)

            val counter by viewModel.counter.collectAsState()
            val ticks by viewModel.ticks.collectAsState()
            val tickSum by viewModel.tickSum.collectAsState()
            val tickAverage by viewModel.tickAverage.collectAsState()
            val graphRange by viewModel.graphRange.collectAsState()
            val graphDomain by viewModel.graphDomain.collectAsState()
            val graphDomainLimits by viewModel.graphDomainLimits.collectAsState()
            val graphDomainOptions = viewModel.graphDomainOptions

            CounterHistoryScreen(
                counter = counter,
                ticks = ticks,
                tickSum = tickSum,
                tickAverage = tickAverage,
                graphRange = graphRange,
                graphDomain = graphDomain,
                graphDomainLimits = graphDomainLimits,
                graphDomainOptions = graphDomainOptions.toList(),
                changeGraphDomain = { viewModel.changeGraphDomain(it) },
                onAddTick = viewModel::addTick,
                onDeleteTick = viewModel::deleteTick,
                onResetCounter = viewModel::resetCounter,
                onCounterChange = viewModel::changeCounter,
                onNavigateUp = { navController.navigateUp() },
            )
        }
    }
}
