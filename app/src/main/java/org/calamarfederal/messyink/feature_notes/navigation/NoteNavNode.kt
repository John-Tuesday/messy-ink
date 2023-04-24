package org.calamarfederal.messyink.feature_notes.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.NavType.Companion
import androidx.navigation.navArgument
import org.calamarfederal.messyink.common.navigation.SubNavNode

sealed class NoteNavNode : SubNavNode {
    val parentRoute: String = NoteNavHost.rootRoute
}




