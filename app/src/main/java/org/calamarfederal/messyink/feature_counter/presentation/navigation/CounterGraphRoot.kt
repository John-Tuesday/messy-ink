package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import androidx.navigation.navigation
import org.calamarfederal.messyink.common.navigation.NavigationNode
import org.calamarfederal.messyink.common.navigation.NavigationRoot
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode.counterHistory
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode.navigateToCounterHistory
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterOverviewNode.counterOverview
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterOverviewNode.navigateToCounterOverview
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.createCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.navigateToCreateCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode.navigateToEditCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.EditTickNode.editTick
import org.calamarfederal.messyink.feature_counter.presentation.navigation.EditTickNode.navigateToEditTick
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.gameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.navigateToGameCounter

/**
 * # Counter Feature-level [SubNavHost]
 *
 * defines the entry point for the feature
 */
object CounterGraphRoot : NavigationRoot<CounterGraphNode>, NavigationNode {
    override val rootRoute: String = "counter_feature"
    override val route: String get() = rootRoute
    override val defaultStart: CounterGraphNode = CounterOverviewNode
    override val arguments: List<NamedNavArgument> = listOf()
    override val deepLinks: List<NavDeepLink> = listOf()

    /**
     * Feature entry point
     *
     * uses [navigation] and sets all the [CounterGraphNode]
     */
    fun NavGraphBuilder.counterGraph(navController: NavController) {
        navigation(
            route = rootRoute,
            startDestination = defaultStart.route,
            arguments = arguments,
            deepLinks = deepLinks,
        ) {
            counterOverview(
                onNavigateToCreateCounter = { navController.navigateToCreateCounter() },
                onNavigateToCounterDetails = { navController.navigateToCounterHistory(it) },
                onNavigateToCounterGameMode = { navController.navigateToGameCounter(it) },
                onNavigateToCounterEdit = { navController.navigateToEditCounter(it) }
            )
            gameCounterNode(
                onNavigateUp = { if (!navController.navigateUp()) navController.navigateToCounterOverview() }
            )
            counterHistory(
                onNavigateUp = { navController.navigateUp() },
                onNavigateToEditTick = { navController.navigateToEditTick(it) }
            )
            editTick(
                onDone = { if (!navController.navigateUp()) navController.navigateToCounterOverview() },
                onCancel = { if (!navController.navigateUp()) navController.navigateToCounterOverview() },
            )
            createCounter(
                onCancel = { navController.navigateUp() },
                onDone = { navController.navigateUp() },
            )
        }
    }
}

/**
 * # Sealed base class for Navigation Points for use with [CounterGraphRoot]
 */
sealed interface CounterGraphNode : NavigationNode
