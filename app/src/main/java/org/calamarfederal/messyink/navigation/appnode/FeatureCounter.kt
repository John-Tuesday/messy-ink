package org.calamarfederal.messyink.navigation.appnode

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterFeatureEntry
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterNavHost

object FeatureCounter : MessyInkNavNode(route = CounterNavHost.rootRoute) {
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
