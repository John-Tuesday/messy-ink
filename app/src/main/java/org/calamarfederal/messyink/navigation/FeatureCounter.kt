package org.calamarfederal.messyink.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterGraphRoot
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterGraphRoot.counterGraph

/**
 * # Count, track, and graph Feature
 */
object FeatureCounter : MessyInkNavNode(route = CounterGraphRoot.rootRoute) {
    /**
     * Entry point builder
     */
    fun NavGraphBuilder.featureCounter(
        navController: NavController,
    ) {
        counterGraph(navController)
    }
}
