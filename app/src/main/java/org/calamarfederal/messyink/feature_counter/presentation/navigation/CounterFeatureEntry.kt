package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.gameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.navigation.TestStartNode.testStart
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@Composable
fun CounterFeatureEntry(
    backStackEntry: NavBackStackEntry,
    parentHostController: NavHostController,
) {
    MessyInkTheme {
        val navController = rememberNavController()
        CounterNavHost.SubNavHost(
            navController = navController,
        ) {
            testStart(navController)
            gameCounterNode(navController)
        }
    }
}
