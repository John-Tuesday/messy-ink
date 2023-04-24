package org.calamarfederal.messyink.navigation.appnode

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import org.calamarfederal.messyink.feature_notes.navigation.NoteNavHost
import org.calamarfederal.messyink.feature_notes.presentation.FeatureNoteEntry
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

object FeatureNoteNode : MessyInkNavNode(route = NoteNavHost.rootRoute) {

    fun NavGraphBuilder.featureNoteNode(
        rootNavController: NavHostController
    ) {
        composable(
            route = FeatureNoteNode.route,
            arguments = FeatureNoteNode.arguments,
            deepLinks = FeatureNoteNode.deepLinks,
        ) {
            FeatureNoteEntry(rootNavController, it)
        }
    }
}


