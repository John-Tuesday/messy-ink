package org.calamarfederal.messyink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.common.navigation.NavigationNode
import org.calamarfederal.messyink.common.navigation.NavigationRoot
import org.calamarfederal.messyink.navigation.FeatureCounter.featureCounter


/**
 * Top level 'feature' node
 */
sealed class MessyInkNavNode constructor(
    override val route: String,
) : NavigationNode {
    companion object
}

/**
 * App entry nav host
 */
object MessyInkNavHost : NavigationRoot<MessyInkNavNode> {
    override val rootRoute: String = "messy_ink_root"
    override val defaultStart: MessyInkNavNode = FeatureCounter

    /**
     * Calls [NavHost] and creates the navigation graphs
     */
    @Composable
    fun NavHostGraph(navController: NavHostController = rememberNavController()) {
        NavHost(
            navController = navController,
            startDestination = defaultStart.route,
            route = rootRoute
        ) {
            featureCounter(navController)
        }
    }
}
