package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import org.calamarfederal.messyink.R


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
        BackHandler(onBack = onDismiss)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier
                .semantics(mergeDescendants = true) {
                    testTag = CounterOverviewTestTags.CounterOptions

                    dismiss { onDismiss(); true }
                },
        ) {
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.counter_game_nav_label)) },
                    leadingContent = { Icon(Filled.Ballot, null) },
                    modifier = Modifier.clickable(onClick = onGameMode),
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.counter_history_nav_label)) },
                    leadingContent = { Icon(Filled.History, null) },
                    modifier = Modifier.clickable(onClick = onDetails),
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.counter_clear_all)) },
                    leadingContent = { Icon(Filled.ClearAll, null) },
                    modifier = Modifier.clickable(onClick = onClear),
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text(stringResource(R.string.counter_edit_nav_label)) },
                    leadingContent = { Icon(Filled.Edit, null) },
                    modifier = Modifier.clickable(onClick = onEdit),
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.counter_delete)) },
                    leadingContent = { Icon(Filled.DeleteForever, null) },
                    modifier = Modifier.clickable(onClick = onDelete),
                )
            }
        }
    }
}
