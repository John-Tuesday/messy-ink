package org.calamarfederal.messyink.feature_notes.navigation

import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import org.calamarfederal.messyink.feature_notes.presentation.NoteDrawer
import org.calamarfederal.messyink.feature_notes.presentation.note_view.NoteViewScreen
import org.calamarfederal.messyink.feature_notes.presentation.NotesViewEditModel


object ViewNoteNode : NoteNavNode() {
    internal const val noteId = "note_id"
    internal const val baseRoute = "notes_view"

    override val route: String = "$baseRoute/{$noteId}"
    override val arguments: List<NamedNavArgument> =
        listOf(navArgument(noteId) { NavType.StringType })
}

fun NavHostController.toViewNote(noteId: Long) {
    navigate("${ViewNoteNode.baseRoute}/$noteId", navOptions { launchSingleTop = true })
}

fun NavGraphBuilder.viewNote(
    navController: NavHostController,
    drawerContent: NoteDrawer,
    onOpenDrawer: () -> Unit,
    navigateToNoteEdit: (Long) -> Unit,
    navigateToNoteView: (Long) -> Unit,
    navigateToViewAll: () -> Unit,
    onDoesNotExist: () -> Unit,
) {
    composable(
        route = ViewNoteNode.route,
        arguments = ViewNoteNode.arguments,
        deepLinks = ViewNoteNode.deepLinks,
    ) { entry ->
        val owner = remember(entry) { navController.getBackStackEntry(ViewNoteNode.parentRoute) }
        val viewModel: NotesViewEditModel = hiltViewModel(owner)

        val noteId: Long? = entry.arguments?.getString(ViewNoteNode.noteId)?.toLongOrNull()

        LaunchedEffect(noteId) {
            if (noteId == null) onDoesNotExist()
            else viewModel.loadNoteFromId(noteId, onDoesNotExist)
        }

        val note by viewModel.pickedNoteBrief.collectAsState()
        val allNotes by viewModel.allNotes.collectAsState()
        val items by viewModel.pickedNoteItems.collectAsState()
        val selectedItems by viewModel.selectedNoteItems.collectAsState()

        val dispatcher = LocalView.current.findOnBackInvokedDispatcher()
        val onBack by remember { derivedStateOf { OnBackInvokedCallback{ viewModel.deselectAll() } }}
        DisposableEffect(dispatcher, onBack, selectedItems.size) {
            if (selectedItems.isNotEmpty()) {
                dispatcher?.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    onBack
                )
            }
            onDispose {
                dispatcher?.unregisterOnBackInvokedCallback(onBack)
            }
        }

        if (note == null) {
            Text("note is null ...", modifier = Modifier.fillMaxSize())
        } else {
            drawerContent {
                ListOfNotesDrawerContent(
                    selected = note?.id,
                    notes = allNotes,
                    onClick = { navigateToNoteView(it.id) }
                )
            }
            NoteViewScreen(
                note = note!!,
                onDeleteNote = { viewModel.deleteTargetNote(note!!); navigateToViewAll() },
                items = items,
                selectedItems = selectedItems,
                onSelectItem = viewModel::selectItem,
                onDeselectItem = viewModel::deselectItem,
                onSelectAll = { viewModel.selectItems(items) },
                onDeselectAll = viewModel::deselectAll,
                onDeleteSelection = viewModel::deleteSelectedItems,
                onOpenDrawer = onOpenDrawer,
                toNoteEdit = { navigateToNoteEdit(note!!.id)},
                deleteItem = viewModel::deleteTargetNoteItem,
            )
        }
    }
}

@Composable
private fun RowScope.ViewNoteActions(
    onDeleteNote: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Filled.MoreVert, "expand options")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(text = { Text("Delete") }, onClick = onDeleteNote)
    }
}
