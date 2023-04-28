package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterOverviewNode.counterOverview
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.gameCounterNode
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * # Navigation Graph Root for the Counter Feature
 *
 * the params are meant to mimic NavGraphBuilder::composable
 *
 * @param[backStackEntry] current back stack entry
 * @param[parentHostController] navController used by parent (Messy Ink)
 */
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
            counterOverview(navController)
            gameCounterNode(navController)
        }
    }
}
