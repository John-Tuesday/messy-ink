package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavType.Companion
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsViewModel
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.TabbedCounterDetailsScreen

internal object TabbedCounterDetails : CounterNavNode() {
    private const val BASE_ROUTE = "tabbed_counter_details"
    private const val COUNTER_ID = CounterDetailsViewModel.COUNTER_ID

    override val arguments = listOf(navArgument(COUNTER_ID) { type = NavType.LongType })
    override val route = "$BASE_ROUTE/${arguments.asRouteString()}"

    fun NavHostController.navigateToTabbedDetails(counterId: Long) {
        navigate("$BASE_ROUTE/$counterId")
    }

    fun NavGraphBuilder.tabbedCounterDetails(
        navController: NavHostController,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        subNavNode { entry ->
            onEntry(entry)

            val viewModel: CounterDetailsViewModel = hiltViewModel(entry)

            val counter by viewModel.counter.collectAsState()
            val ticks by viewModel.ticks.collectAsState()
            val tickSum by viewModel.tickSum.collectAsState()
            val tickAverage by viewModel.tickAverage.collectAsState()

            TabbedCounterDetailsScreen(
                counter = counter,
                ticks = ticks,
                tickSum = tickSum,
                tickAverage = tickAverage,
            )
        }
    }
}
