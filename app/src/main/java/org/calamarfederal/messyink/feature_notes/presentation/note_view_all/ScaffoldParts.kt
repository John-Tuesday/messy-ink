package org.calamarfederal.messyink.feature_notes.presentation.note_view_all

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
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
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief

@Composable
internal fun ViewAllFab(
    expanded: Boolean,
    visible: Boolean,
    onCreateNote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible = visible) {
        ExtendedFloatingActionButton(
            modifier = modifier.animateContentSize(),
            expanded = expanded,
            text = { Text("Create Note") },
            icon = { Icon(Icons.Filled.Create, "create note") },
            onClick = onCreateNote,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ViewAllTopBar(
    selectMode: Boolean,
    selectionSize: Int,
    onDeselectAll: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { if (selectMode) Text("Selection") else Text("All Notes") },
        navigationIcon = {
            if (selectMode) {
                IconButton(onClick = onDeselectAll) {
                    Icon(Icons.Filled.Clear, "clear selection")
                }
            }
        },
        actions = {
            AnimatedVisibility(visible = selectMode, modifier = Modifier.animateContentSize()) {
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
                    DropdownMenuItem(text = { Text("Select All") }, onClick = onSelectAll)
                }
            }
        }
    )
}
