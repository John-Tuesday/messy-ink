package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick


@Composable
internal fun TickDetailsLayout(
    ticks: List<UiTick>,
    onDelete: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = ticks, key = { it.id }) { tick ->
            var showOptions by remember { mutableStateOf(false) }
            Box {
                TickListItem(tick)
                TickOptions(
                    visible = showOptions,
                    onDismiss = { showOptions = false },
                    onDelete = { onDelete(tick.id) },
                    onEdit = { onEdit(tick.id) },
                )
            }
        }
    }
}

@Composable
private fun TickListItem(
    tick: UiTick,
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text("${tick.amount}") },
        supportingContent = { Text("${tick.timeForData.toLocalDateTime(timeZone).date}") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TickOptions(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset.Zero,
    alignment: Alignment = Alignment.BottomCenter,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
    ) {
        Popup(
            onDismissRequest = onDismiss,
            alignment = alignment,
            offset = offset,
        ) {
            ElevatedCard {
                Row {
                    InputChip(
                        selected = false,
                        onClick = onEdit,
                        border = null,
                        label = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Filled.Edit, "edit tick") }
                    )
                    InputChip(
                        selected = false,
                        onClick = onDelete,
                        border = null,
                        label = { Text("Delete") },
                        leadingIcon = { Icon(Icons.Filled.DeleteForever, "delete forever") }
                    )
                }
            }
        }
    }
}
