package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import org.calamarfederal.messyink.common.navigation.SubNavNode
import org.calamarfederal.messyink.common.navigation.SubNavOwner
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode.counterHistory
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode.navigateToCounterHistory
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterOverviewNode.counterOverview
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.createCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.navigateToCreateCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.navigateToEditCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.gameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.navigateToGameCounter

/**
 * # Counter Feature-level [SubNavHost]
 *
 * defines the entry point for the feature
 */
object CounterNavHost : SubNavOwner<CounterNavNode> {
    override val rootRoute: String = "counter_feature"
    override val defaultStart: CounterNavNode = CounterOverviewNode

    fun NavGraphBuilder.counterGraph(navController: NavController) {
        navigation(
            route = rootRoute,
            startDestination = defaultStart.route,
        ) {
            counterOverview(
                onNavigateToCreateCounter = { navController.navigateToCreateCounter() },
                onNavigateToCounterDetails = { navController.navigateToCounterHistory(it) },
                onNavigateToCounterGameMode = { navController.navigateToGameCounter(it) },
                onNavigateToCounterEdit = { navController.navigateToEditCounter(it) }
            )
            gameCounterNode(
                onEditCounter = { navController.navigateToEditCounter(it) },
            )
            counterHistory(
                onNavigateUp = { navController.navigateUp() }
            )
            createCounter(
                onCancel = { navController.navigateUp() },
                onDone = { navController.navigateUp() },
            )
        }
    }
}

/**
 * # Sealed base class for Navigation Points for use with [CounterNavHost]
 */
sealed interface CounterNavNode : SubNavNode
