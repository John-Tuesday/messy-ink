package org.calamarfederal.messyink.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * # Navigation Points for use in a [SubNavOwner]
 *
 * intended to implemented as a sealed class per graph.
 *
 */
interface SubNavNode {
    /**
     * Route to this Node
     */
    val route: String
    /**
     * Arguments for navigating to this node
     */
    val arguments: List<NamedNavArgument> get() = listOf()
    /**
     * Deep links associated with this node
     */
    val deepLinks: List<NavDeepLink> get() = listOf()

    /**
     * Convenience function for starting composable logic
     */
    fun NavGraphBuilder.subNavNode(content: @Composable (NavBackStackEntry) -> Unit) {
        composable(
            route = this@SubNavNode.route,
            arguments = this@SubNavNode.arguments,
            deepLinks = this@SubNavNode.deepLinks,
            content = content
        )
    }
}
