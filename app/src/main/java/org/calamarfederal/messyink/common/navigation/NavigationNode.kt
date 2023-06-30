package org.calamarfederal.messyink.common.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink

/**
 * # Navigation Points for use in a [NavigationRoot]
 *
 * intended to implemented as a sealed class per graph.
 *
 */
interface NavigationNode {
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
}
