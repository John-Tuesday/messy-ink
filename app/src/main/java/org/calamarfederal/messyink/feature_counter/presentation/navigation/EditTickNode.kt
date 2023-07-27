package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.tick_edit.EditTickScreen
import org.calamarfederal.messyink.feature_counter.presentation.tick_edit.EditTickViewModel

/**
 * Edit tick
 */
internal data object EditTickNode : CounterGraphNode {
    private const val BASE_ROUTE = "counter_edit_tick"
    internal const val TICK_ID = "edit_tick_id"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(name = TICK_ID) { type = NavType.LongType; defaultValue = NOID }
    )

    override val route: String = "$BASE_ROUTE/{$TICK_ID}"

    private fun makeRoute(tickId: Long): String = "$BASE_ROUTE/$tickId"

    /**
     * navigate to edit tick of [tickId]
     */
    fun NavController.navigateToEditTick(tickId: Long) {
        navigate(route = makeRoute(tickId)) { launchSingleTop = true }
    }

//    fun NavController.navigateToCreateEditTick() {
//        navigate(route = makeRoute(NOID)) { launchSingleTop = true }
//    }

    fun NavGraphBuilder.editTick(
        onDone: () -> Unit,
        onCancel: () -> Unit,
    ) {
        composable(
            route = EditTickNode.route,
            arguments = arguments,
        ) {
            val viewModel: EditTickViewModel = hiltViewModel(it)

            val uiState by viewModel.editTickUiState.collectAsStateWithLifecycle()

            EditTickScreen(
                editTickState = uiState,
                onChangeAmount = viewModel::onAmountChanged,
                onChangeTimeModified = viewModel::onTimeModifiedChanged,
                onChangeTimeCreated = viewModel::onTimeCreatedChanged,
                onChangeTimeForData = viewModel::onTimeForDataChanged,
                onDone = { viewModel.finalizeTick(); onDone() },
                onClose = onCancel,
            )
        }
    }
}
