package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterScreen_2

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

    internal fun buildDestination(counterId: Long) = "$BASE_ROUTE/$counterId"

    fun NavController.navigateToGameCounter(
        counterId: Long,
        navOptions: NavOptions? = null,
    ) {
        navigate("$BASE_ROUTE/$counterId", navOptions)
    }

    fun NavGraphBuilder.gameCounterNode(
        onEditCounter: (Long) -> Unit,
        onNavigateUp: () -> Unit,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        composable(
            route = GameCounterNode.route,
            arguments = GameCounterNode.arguments,
            deepLinks = GameCounterNode.deepLinks,
        ) { entry ->
            onEntry(entry)

            val viewModel: GameCounterViewModel = hiltViewModel(entry)

            val counter by viewModel.counter.collectAsStateWithLifecycle()
            val tickSum by viewModel.tickSum.collectAsStateWithLifecycle()
            val primaryIncrement by viewModel.primaryIncrement.collectAsStateWithLifecycle()
            val secondaryIncrement by viewModel.secondaryIncrement.collectAsStateWithLifecycle()

            GameCounterScreen_2(
                counterName = counter.name,
                tickSum = tickSum,
                onAddTick = viewModel::addTick,
                onNavigateUp = onNavigateUp,

            )
//            GameCounterScreen(
//                counter = counter,
//                tickSum = tickSum,
//                primaryIncrement = primaryIncrement,
//                onChangePrimaryIncrement = viewModel::changePrimaryIncrement,
//                secondaryIncrement = secondaryIncrement,
//                onChangeSecondaryIncrement = viewModel::changeSecondaryIncrement,
//                onAddTick = viewModel::addTick,
//                onUndo = viewModel::undoTick,
//                onRedo = viewModel::redoTick,
//                onReset = viewModel::restartCounter,
//                onEditCounter = { onEditCounter(counter.id) },
//            )
        }
    }
}
