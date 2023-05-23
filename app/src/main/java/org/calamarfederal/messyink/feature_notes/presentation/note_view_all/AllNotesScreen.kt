package org.calamarfederal.messyink.feature_notes.presentation.note_view_all

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief
import org.calamarfederal.messyink.feature_notes.presentation.state.uiNoteBriefPreviews
import org.calamarfederal.messyink.ui.theme.MaterialLevel
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import org.calamarfederal.messyink.ui.theme.toMaterialLevelCiel

@Preview
@Composable
private fun PreviewAllNotesScreen() {
    MessyInkTheme {
        AllNotesScreen(
            notes = uiNoteBriefPreviews.take(5).toList(),
            onNoteDelete = {},
            selectedNotes = setOf(),
            onSelectNote = {},
            onSelectAll = {},
            onDeselectNote = {},
            onDeselectAll = {},
            onDeleteSelection = {},
            navigateToNoteView = {},
            navigateToNoteEdit = {},
            navigateToNoteCreate = {})
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AllNotesScreen(
    notes: List<UiNoteBrief>,
    onNoteDelete: (UiNoteBrief) -> Unit,
    selectedNotes: Set<UiNoteBrief>,
    onSelectNote: (UiNoteBrief) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectNote: (UiNoteBrief) -> Unit,
    onDeselectAll: () -> Unit,
    onDeleteSelection: () -> Unit,
    navigateToNoteView: (UiNoteBrief) -> Unit,
    navigateToNoteEdit: (UiNoteBrief) -> Unit,
    navigateToNoteCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    val dispatcher = LocalView.current.findOnBackInvokedDispatcher()
//    val onBack = remember(selected) { OnBackInvokedCallback { selected = setOf() } }
//
//    DisposableEffect(selectMode, dispatcher, onBack) {
//        if (selectMode) {
//            dispatcher?.registerOnBackInvokedCallback(
//                OnBackInvokedDispatcher.PRIORITY_OVERLAY,
//                onBack,
//            )
//        }
//        onDispose {
//            dispatcher?.unregisterOnBackInvokedCallback(onBack)
//        }
//    }

    var fabExpanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            ViewAllTopBar(
                selectMode = selectedNotes.isNotEmpty(),
                selectionSize = selectedNotes.size,
                onDeselectAll = onDeselectAll,
                onSelectAll = onSelectAll,
                onDeleteSelection = onDeleteSelection
            )
        },
        floatingActionButton = {
            ViewAllFab(
                expanded = fabExpanded,
                visible = selectedNotes.isNotEmpty(),
                onCreateNote = navigateToNoteCreate,
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            AllNotesLayout(
                notes = notes,
                selectedNotes = selectedNotes,
                selectMode = selectedNotes.isNotEmpty(),
                navigateToNoteView = navigateToNoteView,
                onSelectNote = onSelectNote,
                onDeselectNote = onDeselectNote,
                setFabVisibility = { fabExpanded = it },
            )
        }
    }

}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AllNotesLayout(
    notes: List<UiNoteBrief>,
    selectedNotes: Set<UiNoteBrief>,
    selectMode: Boolean,
    onSelectNote: (UiNoteBrief) -> Unit,
    onDeselectNote: (UiNoteBrief) -> Unit,
    navigateToNoteView: (UiNoteBrief) -> Unit,
    setFabVisibility: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState.isScrollInProgress) {
        setFabVisibility(listState.isScrollInProgress)
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        itemsIndexed(items = notes, key = { _, note -> note.id }) { index, note ->
            NoteListItem(
                note = note,
                selectMode = selectMode,
                isSelected = selectedNotes.contains(note),
                onSelectNote = { onSelectNote(note) },
                onDeselectNote = { onDeselectNote(note) },
                navigateToNoteView = { navigateToNoteView(note) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteListItem(
    note: UiNoteBrief,
    selectMode: Boolean,
    isSelected: Boolean,
    onSelectNote: () -> Unit,
    onDeselectNote: () -> Unit,
    navigateToNoteView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    ListItem(
        tonalElevation = LocalAbsoluteTonalElevation.current.let {
            if (isSelected) it.toMaterialLevelCiel().coerceAtLeast(MaterialLevel(3)).elevation
            else it
        },
        modifier = modifier.combinedClickable(
            onLongClick = {
                if (!selectMode) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSelectNote()
                } else Unit
            },
            onClick = {
                if (!selectMode) navigateToNoteView()
                else if (isSelected) onDeselectNote()
                else onSelectNote()
            },
        ),
        headlineContent = {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = if (selectMode) FontStyle.Italic else FontStyle.Normal,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .wrapContentSize()
            )
        },
        leadingContent = {
            AnimatedVisibility(visible = isSelected) {
                Icon(Icons.Filled.Check, null)
            }
        }
    )
}
