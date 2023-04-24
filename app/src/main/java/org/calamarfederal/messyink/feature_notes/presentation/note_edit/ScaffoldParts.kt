package org.calamarfederal.messyink.feature_notes.presentation.note_edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteEditAppBar(
    isSelectMode: Boolean,
    selectionSize: Int,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onNavigateUp: () -> Unit,
    onDeleteSelection: () -> Unit,
    onDeleteNote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text("Edit") },
        navigationIcon = {
            if (isSelectMode) {
                IconButton(onClick = onDeselectAll) {
                    Icon(Icons.Filled.Clear, "clear selection")
                }
            } else {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.Filled.ArrowBack, "navigate up")
                }
            }
        },
        actions = {
            if (isSelectMode) {
                IconButton(onClick = onDeleteSelection) {
                    BadgedBox(badge = { Badge(modifier = Modifier.animateContentSize()) { Text("$selectionSize") } }) {
                        Icon(Icons.Filled.Delete, "delete selection")
                    }
                }
            }

            Box {
                var expanded by remember { mutableStateOf(false) }

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.MoreVert, "show more options")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.animateContentSize()
                ) {
                    Divider()
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Filled.Delete, "delete note") },
                        text = { Text("Delete Note") },
                        onClick = onDeleteNote,
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Select all") }, onClick = onSelectAll)
                }
            }
        }
    )
}

@Composable
internal fun NoteEditFab(
    selectMode: Boolean,
    createItem: () -> Unit,
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = !selectMode,
    ) {
        ExtendedFloatingActionButton(
            modifier = buttonModifier,
            text = { Text("Add Item") },
            icon = { Icon(Icons.Filled.Create, "create new item") },
            onClick = createItem,
        )
    }
}
