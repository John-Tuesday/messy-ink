package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterOverviewNode.counterOverview
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.createCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.navigateToCreateCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.gameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.navigateToGameCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.TabbedCounterDetails.navigateToTabbedDetails
import org.calamarfederal.messyink.feature_counter.presentation.navigation.TabbedCounterDetails.tabbedCounterDetails
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
            counterOverview(
                navController,
                onNavigateToCreateCounter = { navController.navigateToCreateCounter() },
                onNavigateToCounterDetails = { navController.navigateToTabbedDetails(it) },
                onNavigateToCounterGameMode = { navController.navigateToGameCounter(it) }
            )
            gameCounterNode(navController)
            tabbedCounterDetails(navController)
            createCounter(
                navController,
                onCancel = { navController.popBackStack() },
                onDone = { navController.popBackStack() },
                onDelete = { navController.popBackStack() },
            )
        }
    }
}
