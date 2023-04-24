package org.calamarfederal.messyink.feature_notes.navigation

import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import org.calamarfederal.messyink.feature_notes.presentation.note_view_all.AllNotesScreen
import org.calamarfederal.messyink.feature_notes.presentation.NoteDrawer
import org.calamarfederal.messyink.feature_notes.presentation.NotesViewEditModel

object ViewAllNode : NoteNavNode() {
    override val route: String = "notes_view_all"
}

fun NavHostController.toViewAll() {
    navigate(ViewAllNode.route, navOptions { launchSingleTop = true })
}

fun NavGraphBuilder.viewAllNotes(
    navController: NavHostController,
    drawerContent: NoteDrawer,
    navigateToNoteView: (Long) -> Unit,
    navigateToNoteEdit: (Long) -> Unit,
    navigateToNoteCreate: () -> Unit,
) {
    composable(
        route = ViewAllNode.route,
        arguments = ViewAllNode.arguments,
        deepLinks = ViewAllNode.deepLinks
    ) { entry ->
        val parent = remember(entry) { navController.getBackStackEntry(ViewAllNode.parentRoute) }
        val viewModel: NotesViewEditModel = hiltViewModel(parent)
        val notes by viewModel.allNotes.collectAsState()
        val selectedNotes by viewModel.selectedNoteBriefs.collectAsState()

        val dispatcher = LocalView.current.findOnBackInvokedDispatcher()
        val onBack by remember { derivedStateOf { OnBackInvokedCallback { viewModel.deselectAll() } } }
        DisposableEffect(dispatcher, onBack, selectedNotes.size) {
            if (selectedNotes.isNotEmpty()) {
                dispatcher?.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    onBack
                )
            }
            onDispose {
                dispatcher?.unregisterOnBackInvokedCallback(onBack)
            }
        }

        drawerContent(null)
        AllNotesScreen(
            notes = notes,
            onNoteDelete = viewModel::deleteTargetNote,
            selectedNotes = selectedNotes,
            onSelectNote = viewModel::selectNote,
            onSelectAll = { viewModel.selectNotes(notes) },
            onDeselectNote = viewModel::deselectNote,
            onDeselectAll = viewModel::deselectAll,
            onDeleteSelection = viewModel::deleteSelectedNotes,
            navigateToNoteView = { navigateToNoteView(it.id) },
            navigateToNoteEdit = { navigateToNoteEdit(it.id) },
            navigateToNoteCreate = navigateToNoteCreate,
        )
    }
}

