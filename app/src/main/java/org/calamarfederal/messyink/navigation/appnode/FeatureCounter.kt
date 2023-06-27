package org.calamarfederal.messyink.navigation.appnode

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterFeatureEntry
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterNavHost

/**
 * # Count, track, and graph Feature
 */
object FeatureCounter : MessyInkNavNode(route = CounterNavHost.rootRoute) {
    /**
     * Entry point builder
     */
    fun NavGraphBuilder.featureCounter(
        navHostController: NavHostController,
    ) {
        composable(
            route = FeatureCounter.route,
            arguments = FeatureCounter.arguments,
            deepLinks = FeatureCounter.deepLinks,
        ) { entry ->
            CounterFeatureEntry(
                backStackEntry = entry,
                parentHostController = navHostController,
            )
        }
    }
}
