package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryViewModel
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryScreen

/**
 * # History and Details
 * ## of a Counter, including its ticks
 */
internal object CounterHistoryNode : CounterGraphNode {
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

    @OptIn(ExperimentalMaterial3Api::class)
    fun NavGraphBuilder.counterHistory(
        onNavigateUp: () -> Unit,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        composable(
            route = CounterHistoryNode.route,
            arguments = CounterHistoryNode.arguments,
            deepLinks = CounterHistoryNode.deepLinks,
        ) { entry ->
            onEntry(entry)

            val viewModel: CounterHistoryViewModel = hiltViewModel(entry)

            val ticks by viewModel.allTicksState.collectAsState()
            val tickSort by viewModel.tickSortState.collectAsState()
            val tickEdit by viewModel.editTickSupportState.collectAsState()
            val graphPoints by viewModel.tickGraphPointsState.collectAsState()
            val graphRange by viewModel.amountRangeState.collectAsState()
            val graphDomain by viewModel.timeDomainState.collectAsState()
            val graphDomainLimits by viewModel.domainLimitState.collectAsState()

            CounterHistoryScreen(
                ticks = ticks,
                tickEdit = tickEdit,
                graphPoints = graphPoints,
                graphRange = graphRange,
                graphDomain = graphDomain,
                graphDomainLimits = graphDomainLimits,
                changeGraphDomain = { viewModel.changeGraphZoom(domain = it) },
                onDeleteTick = viewModel::deleteTick,
                onEditTick = viewModel::startTickEdit,
                onCancelEditTick = viewModel::discardTickEdit,
                onFinalizeEditTick = viewModel::finalizeTickEdit,
                onEditTickChanged = viewModel::updateTickEdit,
                tickSort = tickSort,
                onChangeSort = viewModel::changeTickSort,
                onNavigateUp = onNavigateUp,
            )
        }
    }
}
