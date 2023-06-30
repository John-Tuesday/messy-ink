package org.calamarfederal.messyink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.common.navigation.SubNavNode
import org.calamarfederal.messyink.common.navigation.SubNavOwner
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterNavHost
import org.calamarfederal.messyink.navigation.FeatureCounter.featureCounter
import org.calamarfederal.messyink.ui.theme.MessyInkTheme


/**
 * Top level 'feature' node
 */
sealed class MessyInkNavNode constructor(
    override val route: String,
) : SubNavNode {
    companion object
}

/**
 * App entry nav host
 */
object MessyInkNavHost : SubNavOwner<MessyInkNavNode> {
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
