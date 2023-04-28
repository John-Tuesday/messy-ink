package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterScreen
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterViewModel
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID

/**
 * # Game Counter Navigation Node
 */
internal object GameCounterNode : CounterNavNode(route = "game_counter") {
    private const val COUNTER_ID = GameCounterViewModel.COUNTER_ID_KEY

    override val arguments: List<NamedNavArgument> =
        listOf(navArgument(COUNTER_ID) { type = NavType.LongType; defaultValue = NOID })

    fun NavGraphBuilder.gameCounterNode(
        navHostController: NavHostController,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        subNavNode { entry ->
            onEntry(entry)

            val onNavigateUp by remember(entry) {
                derivedStateOf<(() -> Unit)?> {
                    navHostController.previousBackStackEntry?.let {
                        { navHostController.navigateUp() }
                    }
                }
            }

            val viewModel: GameCounterViewModel = hiltViewModel()

            val id by remember(entry) {
                derivedStateOf {
                    (entry.arguments?.getLong(COUNTER_ID, NOID) ?: NOID).also { println(it) }
                }
            }

            val counter by viewModel.counter.collectAsState()
            val tickSum by viewModel.tickSum.collectAsState()

            GameCounterScreen(
                counter = counter,
                tickSum = tickSum,
                onAddTick = viewModel::addTick,
                onUndo = viewModel::undoTick,
                onRedo = viewModel::redoTick,
                onReset = viewModel::restartCounter,
                onNavigateUp = onNavigateUp,
            )
        }
    }
}
