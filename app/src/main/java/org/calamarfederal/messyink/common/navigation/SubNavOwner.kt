package org.calamarfederal.messyink.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController


/**
 * # Navigation Sub-graph Owner
 *
 * intended to be implemented as a stateless object at the Feature-Level
 *
 */
interface SubNavOwner <T : SubNavNode> {
    /**
     * route to this, i.e. the owner of the nested graph
     *
     * should be const
     */
    val rootRoute: String
    /**
     * The typical first screen / subcomponent when entering this
     *
     * should be const
     */
    val defaultStart: T

    /**
     * Convenience function for creating the owned [NavHost]
     *
     * often the [navController] is hoisted
     */
    @Composable
    fun SubNavHost(
        modifier: Modifier = Modifier,
        start: T = defaultStart,
        navController: NavHostController = rememberNavController(),
        builder: NavGraphBuilder.() -> Unit,
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = start.route,
            route = rootRoute,
            builder = builder,
        )
    }
}
