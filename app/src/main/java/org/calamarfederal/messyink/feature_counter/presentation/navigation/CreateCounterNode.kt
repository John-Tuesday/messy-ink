package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.data.model.NOID
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterScreen
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterViewModel

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

    @Composable
    fun CreateCounterScreenBuilder(
        onCancel: () -> Unit,
        onDone: () -> Unit,
        viewModel: CreateCounterViewModel = hiltViewModel(),
        counterId: Long = NOID,
    ) {
        LaunchedEffect(counterId) {
            viewModel.loadCounter(counterId)
        }
        val uiState by viewModel.createCounterUiState.collectAsStateWithLifecycle()

        CreateCounterScreen(
            counterName = uiState.name,
            counterNameError = uiState.nameHelpState.isError,
            counterNameHelp = uiState.nameHelpState.help,
            isEditCounter = counterId != NOID,
            onNameChange = viewModel::changeName,
            onCancel = { viewModel.discardCounter(); onCancel() },
            onDone = { viewModel.finalizeCounter(); onDone() },
        )
    }

    fun NavGraphBuilder.createCounter(
        onCancel: () -> Unit,
        onDone: () -> Unit,
    ) {
        composable(
            route = CreateCounterNode.route,
            arguments = CreateCounterNode.arguments,
            deepLinks = CreateCounterNode.deepLinks,
        ) { entry ->
            CreateCounterScreenBuilder(
                onCancel = onDone,
                onDone = onCancel,
                viewModel = hiltViewModel(entry),
                counterId = entry.arguments?.getLong(INIT_COUNTER_ID) ?: NOID,
            )
        }
    }
}
