package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.EditTickScreen

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

            val counter by viewModel.counter.collectAsState()
            val ticks by viewModel.ticks.collectAsState()
            val tickEdit by viewModel.editTickSupport.collectAsState()
            val tickSum by viewModel.tickSum.collectAsState()
            val tickAverage by viewModel.tickAverage.collectAsState()
            val graphPoints by viewModel.graphPoints.collectAsState()
            val graphRange by viewModel.graphRange.collectAsState()
            val graphDomain by viewModel.graphDomain.collectAsState()
            val graphDomainLimits by viewModel.graphDomainLimits.collectAsState()
//            val graphDomainOptions = viewModel.graphDomainOptions

            CounterHistoryScreen(
                counter = counter,
                ticks = ticks,
                tickEdit = tickEdit,
                tickSum = tickSum,
                tickAverage = tickAverage,
                graphPoints = graphPoints,
                graphRange = graphRange,
                graphDomain = graphDomain,
                graphDomainLimits = graphDomainLimits,
                changeGraphDomain = { viewModel.changeGraphDomain(it) },
                onAddTick = viewModel::addTick,
                onDeleteTick = viewModel::deleteTick,
                onEditTick = viewModel::startTickEdit,
                onCancelEditTick = viewModel::discardTickEdit,
                onFinalizeEditTick = viewModel::finalizeTickEdit,
                onEditTickChanged = viewModel::updateTickEdit,
                onResetCounter = viewModel::resetCounter,
                onCounterChange = viewModel::changeCounter,
                onNavigateUp = onNavigateUp,
            )
        }
    }
}
