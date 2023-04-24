package org.calamarfederal.messyink.feature_notes.presentation.note_view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteViewAppBar(
    onDeleteNote: () -> Unit,
    isSelectMode: Boolean,
    selectionSize: Int,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDeleteSelection: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { if (isSelectMode) Text("Selection") else Text("Note View") },
        navigationIcon = {
            IconButton(onClick = { if (isSelectMode) onDeselectAll() else onOpenDrawer() }) {
                if (isSelectMode)
                    Icon(Icons.Filled.Close, "clear selection")
                else
                    Icon(Icons.Filled.Menu, "open side sheet")
            }
        },
        actions = {
            if (isSelectMode) {
                IconButton(onClick = onDeleteSelection) {
                    BadgedBox(badge = { Badge { Text("$selectionSize") } }) {
                        Icon(Icons.Filled.Delete, "delete selection")
                    }
                }
            }
            Box {
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.MoreVert, "more options")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Filled.List, null) },
                        text = { Text("Select all") },
                        onClick = onSelectAll,
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Filled.Delete, null) },
                        text = { Text("Delete Note") },
                        onClick = onDeleteNote,
                    )
                }
            }
        }
    )
}


@Composable
internal fun NoteViewFAB(
    isSelectMode: Boolean,
    expanded: Boolean,
    onCreateNewItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isSelectMode) {
        ExtendedFloatingActionButton(
            modifier = modifier,
            text = { Text("New Item") },
            icon = { Icon(Icons.Filled.Create, "create new item") },
            onClick = onCreateNewItem,
            expanded = expanded,
        )
    }
}
