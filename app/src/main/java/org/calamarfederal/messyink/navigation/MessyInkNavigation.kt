package org.calamarfederal.messyink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.navigation.appnode.FeatureCounter.featureCounter
import org.calamarfederal.messyink.navigation.appnode.MessyInkNavNode
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * Wrapper for [MessyInkNavHost.SubNavHost]
 */
@Composable
fun MessyInkEntry(
    modifier: Modifier = Modifier,
    startDestination: MessyInkNavNode = MessyInkNavHost.defaultStart
) {
    MessyInkTheme {
        val navController = rememberNavController()
        MessyInkNavHost.SubNavHost(
            start = startDestination,
            navController = navController,
        ) {
            featureCounter(navController)
        }
    }
}
