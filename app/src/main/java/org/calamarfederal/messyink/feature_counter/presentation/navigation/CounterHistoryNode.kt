package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryScreen
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryViewModel

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

    @Composable
    fun CounterHistoryScreenBuilder(
        onNavigateUp: () -> Unit,
        onNavigateToEditTick: (Long) -> Unit,
        viewModel: CounterHistoryViewModel = hiltViewModel(),
    ) {

        val ticks by viewModel.allTicksState.collectAsStateWithLifecycle()
        val tickSort by viewModel.tickSortState.collectAsStateWithLifecycle()
        val tickGraphState by viewModel.tickGraphState.collectAsStateWithLifecycle()

        CounterHistoryScreen(
            ticks = ticks,
            graphState = tickGraphState,
            changeGraphDomain = { viewModel.changeGraphZoom(domain = it) },
            changeGraphDomainToFit = { viewModel.changeGraphZoom() },
            onDeleteTick = viewModel::deleteTick,
            onEditTick = onNavigateToEditTick,
            tickSort = tickSort,
            onChangeSort = viewModel::changeTickSort,
            onNavigateUp = onNavigateUp,
        )
    }

    fun NavGraphBuilder.counterHistory(
        onNavigateUp: () -> Unit,
        onNavigateToEditTick: (Long) -> Unit,
    ) {
        composable(
            route = CounterHistoryNode.route,
            arguments = CounterHistoryNode.arguments,
            deepLinks = CounterHistoryNode.deepLinks,
        ) { entry ->
            val viewModel: CounterHistoryViewModel = hiltViewModel(entry)

            CounterHistoryScreenBuilder(
                onNavigateUp = onNavigateUp,
                onNavigateToEditTick = onNavigateToEditTick,
                viewModel = viewModel,
            )
        }
    }
}
