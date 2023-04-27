package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.calamarfederal.messyink.feature_counter.presentation.test_start.TestStartScreen
import org.calamarfederal.messyink.feature_counter.presentation.test_start.TestStartViewModel

internal object TestStartNode : CounterNavNode("test_start") {

    fun NavGraphBuilder.testStart(
        navHostController: NavHostController,
        onEntry: @Composable (NavBackStackEntry) -> Unit = {},
    ) {
        composable(
            route = TestStartNode.route,
            arguments = TestStartNode.arguments,
            deepLinks = TestStartNode.deepLinks,
        ) { entry ->
            onEntry(entry)

            val viewModel: TestStartViewModel = hiltViewModel()
            val counters by viewModel.countersState.collectAsState()
            val ticksSum by viewModel.tickSumByCounterId.collectAsState()

            TestStartScreen(
                counters = counters,
                ticksSum = ticksSum,
                onCreateCounter = viewModel::testCreateCounter,
                onAddTick = viewModel::testAddTick,
            )
        }
    }
}

internal fun NavHostController.toTestStartNode() = navigate(TestStartNode.route)
