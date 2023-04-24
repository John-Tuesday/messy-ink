package org.calamarfederal.messyink.feature_notes.presentation.note_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem
import org.calamarfederal.messyink.feature_notes.presentation.state.uiNoteBriefPreviews
import org.calamarfederal.messyink.feature_notes.presentation.state.uiNoteItemPreviews
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@Preview()
@Composable
private fun NoteEditPreview() {
    MessyInkTheme {
        NoteEditScreen(
            note = uiNoteBriefPreviews.first(),
            items = uiNoteItemPreviews().take(5).toList(),
            selectMode = false,
            selectedItems = setOf(),
            onSelectItem = {},
            onDeselectItem = {},
            onSelectAll = {},
            onDeselectAll = {},
            onDeleteSelection = {},
            onNavigateUp = {},
            onNoteChange = {},
            onNoteDelete = {},
            onNoteItemCreate = {},
            onNoteItemChange = { _, _ -> },
            onNoteItemDelete = { _, _ -> },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    note: UiNoteBrief,
    items: List<UiNoteItem>,
    selectMode: Boolean,
    selectedItems: Set<UiNoteItem>,
    onSelectItem: (UiNoteItem) -> Unit,
    onDeselectItem: (UiNoteItem) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDeleteSelection: () -> Unit,
    onNavigateUp: () -> Unit,
    onNoteChange: (UiNoteBrief) -> Unit,
    onNoteDelete: () -> Unit,
    onNoteItemCreate: () -> Unit,
    onNoteItemChange: (Int, UiNoteItem) -> Unit,
    onNoteItemDelete: (Int, UiNoteItem) -> Unit,
) {
    Scaffold(
        topBar = {
            NoteEditAppBar(
                isSelectMode = selectMode,
                selectionSize = selectedItems.size,
                onSelectAll = onSelectAll,
                onDeselectAll = onDeselectAll,
                onNavigateUp = onNavigateUp,
                onDeleteSelection = onDeleteSelection,
                onDeleteNote = onNoteDelete,
            )
        },
        floatingActionButton = {
            NoteEditFab(selectMode = selectedItems.isNotEmpty(), createItem = onNoteItemCreate)
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .fillMaxSize()
        ) {
            NoteEditLayout(
                note = note,
                items = items,
                selectedItems = selectedItems,
                selectMode = selectMode,
                onSelectItem = onSelectItem,
                onDeselectItem = onDeselectItem,
                onNoteChange = onNoteChange,
                onItemChange = onNoteItemChange,
                onItemDelete = onNoteItemDelete,
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteEditLayout(
    note: UiNoteBrief,
    items: List<UiNoteItem>,
    selectedItems: Set<UiNoteItem>,
    selectMode: Boolean,
    onSelectItem: (UiNoteItem) -> Unit,
    onDeselectItem: (UiNoteItem) -> Unit,
    onNoteChange: (UiNoteBrief) -> Unit,
    onItemChange: (Int, UiNoteItem) -> Unit,
    onItemDelete: (Int, UiNoteItem) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        item(-note.id) {
            TextField(
                value = note.title,
                onValueChange = { onNoteChange(note.copy(title = it)) },
                textStyle = MaterialTheme.typography.titleLarge,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Next) },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
            val isSelected by remember(selectedItems) { derivedStateOf { selectedItems.contains(item) } }
            EditableNoteItem(
                item = item,
                onItemChange = { onItemChange(index, it) },
                isSelected = isSelected,
                selectMode = selectMode,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}
