package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryScreen
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryViewModel
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.TimeDomain

/**
 * # History and Details
 * ## of a Counter, including its ticks
 */
internal data object CounterHistoryNode : CounterGraphNode {
    private const val BASE_ROUTE = "counter_history"

    /**
     * Used as a key in navigation arguments or saved state handel
     */
    const val COUNTER_ID = "counter_id"

    override val arguments = listOf(navArgument(COUNTER_ID) { type = NavType.LongType })
    override val route = "$BASE_ROUTE/{$COUNTER_ID}"

    fun NavController.navigateToCounterHistory(
        counterId: Long,
        navOptions: NavOptions? = null,
    ) {
        navigate("$BASE_ROUTE/$counterId", navOptions = navOptions)
    }

    fun NavGraphBuilder.counterHistory(
        onNavigateUp: () -> Unit,
        onNavigateToEditTick: (Long) -> Unit,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        composable(
            route = CounterHistoryNode.route,
            arguments = CounterHistoryNode.arguments,
            deepLinks = CounterHistoryNode.deepLinks,
        ) { entry ->
            onEntry(entry)

            val viewModel: CounterHistoryViewModel = hiltViewModel(entry)

            val ticks by viewModel.allTicksState.collectAsStateWithLifecycle()
            val tickSort by viewModel.tickSortState.collectAsStateWithLifecycle()
            val tickGraphState by viewModel.tickGraphState.collectAsStateWithLifecycle()
            val graphDomain by remember(tickGraphState.currentDomain) {
                derivedStateOf { TimeDomain(tickGraphState.currentDomain) }
            }
            val graphDomainLimits by remember(tickGraphState.domainBounds) {
                derivedStateOf { TimeDomain(tickGraphState.domainBounds) }
            }

            CounterHistoryScreen(
                ticks = ticks,
                graphPoints = tickGraphState.graphPoints,
                graphRange = tickGraphState.currentRange,//graphRange,
                graphDomain = graphDomain,
                graphDomainLimits = graphDomainLimits,
                changeGraphDomain = { viewModel.changeGraphZoom(domain = it) },
                onDeleteTick = viewModel::deleteTick,
                onEditTick = onNavigateToEditTick,
                tickSort = tickSort,
                onChangeSort = viewModel::changeTickSort,
                onNavigateUp = onNavigateUp,
            )
        }
    }
}
