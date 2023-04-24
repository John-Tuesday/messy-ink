package org.calamarfederal.messyink.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController


interface SubNavOwner <T : SubNavNode> {
    val rootRoute: String
    val defaultStart: T

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
