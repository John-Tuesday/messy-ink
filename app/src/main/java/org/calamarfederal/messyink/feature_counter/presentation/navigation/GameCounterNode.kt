package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterScreen
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterViewModel
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.navigateToEditCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID

/**
 * # Game Counter Navigation Node
 */
internal object GameCounterNode : CounterNavNode {
    private const val BASE_ROUTE = "game_counter"
    private const val COUNTER_ID = GameCounterViewModel.COUNTER_ID

    override val arguments = listOf(
        navArgument(COUNTER_ID) { type = NavType.LongType; defaultValue = NOID },
    )
    override val route = "$BASE_ROUTE/${arguments.toArgPlaceholder()}"

    fun NavHostController.navigateToGameCounter(
        counterId: Long,
        navOptions: NavOptions = navOptions {},
    ) {
        navigate("$BASE_ROUTE/$counterId", navOptions)
    }

    fun NavGraphBuilder.gameCounterNode(
        navHostController: NavHostController,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        subNavNode { entry ->
            onEntry(entry)

            val viewModel: GameCounterViewModel = hiltViewModel(entry)

            val counter by viewModel.counter.collectAsState()
            val tickSum by viewModel.tickSum.collectAsState()
            val primaryIncrement by viewModel.primaryIncrement.collectAsState()
            val secondaryIncrement by viewModel.secondaryIncrement.collectAsState()

            GameCounterScreen(
                counter = counter,
                tickSum = tickSum,
                primaryIncrement = primaryIncrement,
                onChangePrimaryIncrement = viewModel::changePrimaryIncrement,
                secondaryIncrement = secondaryIncrement,
                onChangeSecondaryIncrement = viewModel::changeSecondaryIncrement,
                onAddTick = viewModel::addTick,
                onUndo = viewModel::undoTick,
                onRedo = viewModel::redoTick,
                onReset = viewModel::restartCounter,
                onEditCounter = { navHostController.navigateToEditCounter(counter.id) },
            )
        }
    }
}
