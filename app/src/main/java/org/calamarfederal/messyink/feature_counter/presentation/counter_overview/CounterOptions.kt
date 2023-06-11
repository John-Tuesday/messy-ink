package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.More
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * present all quick options for a particular counter (from a list of more)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CounterOptions(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDetails: () -> Unit,
    onGameMode: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier,
        ) {
            Column {
                ListItem(
                    headlineContent = { Text("Game Mode") },
                    leadingContent = { Icon(Filled.Ballot, "game mode") },
                    modifier = Modifier.clickable(onClick = onGameMode),
                )
                ListItem(
                    headlineContent = { Text("History") },
                    leadingContent = { Icon(Filled.History, "show details") },
                    modifier = Modifier.clickable(onClick = onDetails),
                )
                ListItem(
                    headlineContent = { Text("Clear All Ticks") },
                    leadingContent = { Icon(Filled.ClearAll, "clear all ticks") },
                    modifier = Modifier.clickable(onClick = onClear),
                )
                Divider()
                ListItem(
                    headlineContent = { Text("Edit Counter") },
                    leadingContent = { Icon(Filled.Edit, "edit counter") },
                    modifier = Modifier.clickable(onClick = onEdit),
                )
                ListItem(
                    headlineContent = { Text("Delete Counter") },
                    leadingContent = { Icon(Filled.DeleteForever, "delete counter") },
                    modifier = Modifier.clickable(onClick = onDelete),
                )
            }
        }
    }
}
