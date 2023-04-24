package org.calamarfederal.messyink.common.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink

interface SubNavNode {
    val route: String
    val arguments: List<NamedNavArgument> get() = listOf()
    val deepLinks: List<NavDeepLink> get() = listOf()
}
