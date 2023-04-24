package org.calamarfederal.messyink.feature_notes.presentation.note_view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filterIsInstance
import org.calamarfederal.messyink.feature_notes.presentation.common.NoteItemListItem
import org.calamarfederal.messyink.feature_notes.presentation.state.UiCheckState
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@Preview
@Composable
private fun PreviewNoteViewScreen() {
    MessyInkTheme {
        NoteViewScreen(
            note = UiNoteBrief(title = "Test Title", id = 1L),
            onDeleteNote = {},
            items = (0 until 10).map {
                UiNoteItem(
                    subtitle = "subtitle: $it",
                    name = "name: $it",
                    field = "field: $it",
                    description = "description: $it",
                    checked = UiCheckState.values().let { v -> v[it % v.size] },
                    noteId = 1L,
                    id = it.toLong(),
                )
            },
            selectedItems = setOf(),
            onSelectItem = {},
            onDeselectItem = {},
            onDeleteSelection = {},
            onDeselectAll = {},
            onOpenDrawer = {},
            onSelectAll = {},
            toNoteEdit = {},
            deleteItem = {_, _ ->},
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun NoteViewScreen(
    note: UiNoteBrief,
    onDeleteNote: () -> Unit,
    items: List<UiNoteItem>,
    selectedItems: Set<UiNoteItem>,
    onSelectItem: (UiNoteItem) -> Unit,
    onDeselectItem: (UiNoteItem) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDeleteSelection: () -> Unit,
    onOpenDrawer: () -> Unit,
    toNoteEdit: () -> Unit,
    deleteItem: (Int, UiNoteItem) -> Unit,
) {
    MessyInkTheme {
        var fabExpanded by remember { mutableStateOf(true) }
        Scaffold(
            topBar = {
                NoteViewAppBar(
                    isSelectMode = selectedItems.isNotEmpty(),
                    selectionSize = selectedItems.size,
                    onDeselectAll = onDeselectAll,
                    onSelectAll = onSelectAll,
                    onOpenDrawer = onOpenDrawer,
                    onDeleteNote = onDeleteNote,
                    onDeleteSelection = onDeleteSelection,
                )
            },
            floatingActionButton = {
                NoteViewFAB(
                    isSelectMode = selectedItems.isNotEmpty(),
                    onCreateNewItem = toNoteEdit,
                    expanded = fabExpanded,
                )
            },
            floatingActionButtonPosition = if (fabExpanded) FabPosition.Center else FabPosition.End,
        ) { padding ->
            NoteViewLayout(
                note = note,
                items = items,
                selectedItems = selectedItems,
                isSelectMode = selectedItems.isNotEmpty(),
                onSelectItem = onSelectItem,
                onDeselectItem = onDeselectItem,
                onEditItem = toNoteEdit,
                onDeleteItem = deleteItem,
                setFabExpand = { fabExpanded = it },
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .fillMaxSize(),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteViewLayout(
    note: UiNoteBrief,
    items: List<UiNoteItem>,
    selectedItems: Set<UiNoteItem>,
    isSelectMode: Boolean,
    onSelectItem: (UiNoteItem) -> Unit,
    onDeselectItem: (UiNoteItem) -> Unit,
    onEditItem: () -> Unit,
    onDeleteItem: (Int, UiNoteItem) -> Unit,
    setFabExpand: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        val listState = rememberLazyListState()
        LaunchedEffect(listState.isScrollInProgress) {
            setFabExpand(listState.isScrollInProgress)
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
                Divider()
            }
            itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
                val isSelected by remember { derivedStateOf { selectedItems.contains(item) } }

                var showOptions by remember { mutableStateOf(false) }
                val haptic = LocalHapticFeedback.current
                val interactionSource = remember { MutableInteractionSource() }
                val press by interactionSource.interactions.filterIsInstance<PressInteraction.Press>()
                    .collectAsState(initial = PressInteraction.Press(Offset.Unspecified))
                Box(modifier = Modifier.wrapContentSize(align = Alignment.TopStart)) {
                    NoteViewItemListItem(
                        item = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .combinedClickable(
                                interactionSource = interactionSource,
                                indication = LocalIndication.current,
                                onLongClick = {
                                    showOptions = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onClick = {
                                    if (isSelectMode) {
                                        if (isSelected) onSelectItem(item)
                                        else onDeselectItem(item)
                                    }
                                },
                            )
                    )
                    NoteViewItemOptions(
                        expanded = showOptions,
                        onDismiss = { showOptions = false },
                        onDeleteItem = { onDeleteItem(index, item) },
                        offset = with(LocalDensity.current) {
                            if (press.pressPosition.isSpecified) DpOffset(
                                press.pressPosition.x.toDp(),
                                -press.pressPosition.y.toDp(),
                            )
                            else DpOffset.Unspecified
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteViewItemListItem(
    item: UiNoteItem,
    modifier: Modifier = Modifier,
) {
    NoteItemListItem(item = item, modifier = modifier)
}

@Composable
private fun NoteViewItemOptions(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onDeleteItem: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Unspecified,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismiss() },
        offset = offset,
        modifier = modifier,
    ) {
        DropdownMenuItem(
            leadingIcon = { Icon(Icons.Filled.Delete, null) },
            text = { Text("Delete Item") },
            onClick = onDeleteItem
        )
    }
}
