package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterScreen
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterViewModel
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID

internal object CreateCounterNode : CounterNavNode {
    private const val BASE_ROUTE = "create_counter"
    const val INIT_COUNTER_ID = "init_counter_id"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(INIT_COUNTER_ID) { type = NavType.LongType; defaultValue = NOID }
    )
    override val route: String = "$BASE_ROUTE?$INIT_COUNTER_ID={$INIT_COUNTER_ID}"

    /**
     * Navigate to stand-alone counter creation
     */
    fun NavHostController.navigateToCreateCounter(
        navOptions: NavOptions = navOptions { launchSingleTop = true },
    ) {
        navigate(this@CreateCounterNode.route, navOptions)
    }

    fun NavHostController.navigateToEditCounter(
        counterId: Long,
        navOptions: NavOptions = navOptions { launchSingleTop = true },
    ) {
        navigate("$BASE_ROUTE?$INIT_COUNTER_ID=$counterId", navOptions)
    }

    fun NavGraphBuilder.createCounter(
        navController: NavHostController,
        onCancel: () -> Unit,
        onDone: () -> Unit,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        subNavNode { entry ->
            onEntry(entry)

            val viewModel: CreateCounterViewModel = hiltViewModel(entry)
            val counterSupport by viewModel.counterSupport.collectAsState()

            CreateCounterScreen(
                counterSupport = counterSupport,
                onNameChange = viewModel::changeName,
                onCancel = { viewModel.discardCounter(); onCancel() },
                onDone = { viewModel.finalizeCounter(); onDone() },
            )
        }
    }
}
