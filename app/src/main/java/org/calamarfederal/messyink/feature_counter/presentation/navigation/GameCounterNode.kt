package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterScreen
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterViewModel
import org.calamarfederal.messyink.feature_counter.data.model.NOID

/**
 * # Game Mode Node
 * ## designed to track stats in games like player health in MtG
 */
internal data object GameCounterNode : CounterGraphNode {
    private const val BASE_ROUTE = "game_counter"
    const val COUNTER_ID = "counter_id"

    override val arguments = listOf(
        navArgument(COUNTER_ID) { type = NavType.LongType; defaultValue = NOID },
    )

    override val route: String = "$BASE_ROUTE/${
        arguments.joinToString(
            separator = "/",
            prefix = "{",
            postfix = "}",
        ) { it.name }
    }"

    fun NavController.navigateToGameCounter(
        counterId: Long,
        navOptions: NavOptions? = null,
    ) {
        navigate("$BASE_ROUTE/$counterId", navOptions)
    }

    @Composable
    fun GameCounterScreenBuilder(
        onNavigateUp: () -> Unit,
        viewModel: GameCounterViewModel = hiltViewModel(),
    ) {
        val counter by viewModel.counter.collectAsStateWithLifecycle()
        val tickSum by viewModel.tickSum.collectAsStateWithLifecycle()
        val primaryIncrement by viewModel.primaryIncrement.collectAsStateWithLifecycle()
        val secondaryIncrement by viewModel.secondaryIncrement.collectAsStateWithLifecycle()

        GameCounterScreen(
            counterName = counter.name,
            tickSum = tickSum,
            primaryIncrement = primaryIncrement,
            secondaryIncrement = secondaryIncrement,
            onAddTick = viewModel::addTick,
            onNavigateUp = onNavigateUp,
        )
    }

    fun NavGraphBuilder.gameCounterNode(
        onNavigateUp: () -> Unit,
    ) {
        composable(
            route = GameCounterNode.route,
            arguments = GameCounterNode.arguments,
            deepLinks = GameCounterNode.deepLinks,
        ) { entry ->
            GameCounterScreenBuilder(
                onNavigateUp = onNavigateUp,
                viewModel = hiltViewModel(entry),
            )
        }
    }
}
