package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterScreen
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterViewModel
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID

/**
 * # Create / Edit Counter Node
 */
internal data object CreateCounterNode : CounterGraphNode {
    private const val BASE_ROUTE = "create_counter"
    const val INIT_COUNTER_ID = "init_counter_id"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(INIT_COUNTER_ID) { type = NavType.LongType; defaultValue = NOID }
    )
    override val route: String = "$BASE_ROUTE?$INIT_COUNTER_ID={$INIT_COUNTER_ID}"

    /**
     * Navigate to stand-alone counter creation
     */
    fun NavController.navigateToCreateCounter(
        navOptions: NavOptions? = navOptions { launchSingleTop = true },
    ) {
        navigate(this@CreateCounterNode.route, navOptions)
    }

    fun NavController.navigateToEditCounter(
        counterId: Long,
        navOptions: NavOptions? = navOptions { launchSingleTop = true },
    ) {
        navigate("$BASE_ROUTE?$INIT_COUNTER_ID=$counterId", navOptions)
    }

    fun NavGraphBuilder.createCounter(
        onCancel: () -> Unit,
        onDone: () -> Unit,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        composable(
            route = CreateCounterNode.route,
            arguments = CreateCounterNode.arguments,
            deepLinks = CreateCounterNode.deepLinks,
        ) { entry ->
            onEntry(entry)

            val viewModel: CreateCounterViewModel = hiltViewModel(entry)
            val counterId = entry.arguments?.getLong(INIT_COUNTER_ID)

            CreateCounterScreen(
                counterName = viewModel.counterName,
                counterNameError = viewModel.counterNameError,
                counterNameHelp = viewModel.counterHelp,
                isEditCounter = counterId != null && counterId != NOID,
                onNameChange = viewModel::changeName,
                onCancel = { viewModel.discardCounter(); onCancel() },
                onDone = { viewModel.finalizeCounter(); onDone() },
            )
        }
    }
}
