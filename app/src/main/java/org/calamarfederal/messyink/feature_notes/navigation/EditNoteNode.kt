package org.calamarfederal.messyink.feature_notes.navigation

import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import org.calamarfederal.messyink.feature_notes.presentation.NoteDrawer
import org.calamarfederal.messyink.feature_notes.presentation.note_edit.NoteEditScreen
import org.calamarfederal.messyink.feature_notes.presentation.NotesViewEditModel

object EditNoteNode : NoteNavNode() {
    internal const val noteId = "note_id"
    internal const val baseRoute = "notes_edit"

    override val route: String = "$baseRoute/{$noteId}"
    override val arguments: List<NamedNavArgument> =
        listOf(navArgument(noteId) { NavType.StringType })
}

fun NavHostController.toEditNote(noteId: Long) {
    navigate("${EditNoteNode.baseRoute}/$noteId", navOptions { launchSingleTop = true })
}

fun NavHostController.toEditNewNote() {
    navigate("${EditNoteNode.baseRoute}/create", navOptions { launchSingleTop = true })
}

fun NavGraphBuilder.editNote(
    navController: NavHostController,
    drawerContent: NoteDrawer,
    navigateToViewNote: (Long) -> Unit,
    navigateToViewAll: () -> Unit,
) {
    composable(
        route = EditNoteNode.route,
        arguments = EditNoteNode.arguments,
        deepLinks = EditNoteNode.deepLinks
    ) { entry ->
        val parent = remember(entry) { navController.getBackStackEntry(EditNoteNode.parentRoute) }
        val viewModel: NotesViewEditModel = hiltViewModel(parent)

        val noteId: Long? = entry.arguments?.getString(EditNoteNode.noteId)?.toLongOrNull()
        LaunchedEffect(noteId) { viewModel.loadOrCreateNoteFromId(noteId) }

        val allNotes by viewModel.allNotes.collectAsState()
        val note by viewModel.pickedNoteBrief.collectAsState()
        val items by viewModel.pickedNoteItems.collectAsState()
        val selectedItems by viewModel.selectedNoteItems.collectAsState()
        val selectMode by viewModel.isSelectMode.collectAsState()

        val dispatcher = LocalView.current.findOnBackInvokedDispatcher()
        val onBack by remember { derivedStateOf { OnBackInvokedCallback{ viewModel.deselectAll() } }}
        DisposableEffect(dispatcher, onBack, selectMode) {
            if (selectedItems.isNotEmpty()) {
                dispatcher?.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    onBack
                )
            }
            onDispose{
                dispatcher?.unregisterOnBackInvokedCallback(onBack)
            }
        }

        if (note == null) {
            Text("edit note: note is null... waiting...", modifier = Modifier.fillMaxSize())
        } else {
            drawerContent {
                ListOfNotesDrawerContent(
                    selected = note?.id,
                    notes = allNotes,
                    onClick = { navigateToViewNote(it.id) })
            }
            NoteEditScreen(
                note = note!!,
                items = items,
                selectedItems = selectedItems,
                selectMode = selectMode,
                onNoteChange = viewModel::updateTargetedNote,
                onNoteDelete = { viewModel.deleteTargetNote(note!!) },
                onNoteItemCreate = viewModel::createAddNoteItem,
                onNoteItemChange = viewModel::updateTargetedNoteItem,
                onNoteItemDelete = viewModel::deleteTargetNoteItem,
                onDeleteSelection = viewModel::deleteSelectedItems,
                onDeselectAll = viewModel::deselectAll,
                onDeselectItem = viewModel::deselectItem,
                onSelectItem = viewModel::selectItem,
                onSelectAll = { viewModel.selectItems(items) },
                onNavigateUp = { navigateToViewNote(note!!.id) },
            )
        }
    }
}


