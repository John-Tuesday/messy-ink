package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterScreen
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterViewModel

internal object CreateCounterNode : CounterNavNode {
    override val route: String = "create_counter"

    /**
     * Navigate to stand-alone counter creation
     */
    fun NavHostController.navigateToCreateCounter(
        navOptions: NavOptions = navOptions {
            launchSingleTop = true
        },
    ) {
        navigate(this@CreateCounterNode.route, navOptions)
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
