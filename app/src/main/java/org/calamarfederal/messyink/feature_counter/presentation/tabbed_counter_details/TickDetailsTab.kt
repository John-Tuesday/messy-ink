package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.ui.theme.TonalElevation


@OptIn(ExperimentalFoundationApi::class)
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
            val haptic = LocalHapticFeedback.current
            Box {
                TickListItem(
                    tick = tick,
                    selected = showOptions,
                    modifier = Modifier.combinedClickable(
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showOptions = true
                        },
                        onClick = { showOptions = false },
                    )
                )
                TickOptions(
                    visible = showOptions,
                    onDismiss = { showOptions = false },
                    onDelete = { onDelete(tick.id) },
                    onEdit = { onEdit(tick.id) },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun TickListItem(
    tick: UiTick,
    selected: Boolean,
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = tick.amount.toStringAllowShorten(),
                style = MaterialTheme.typography.titleLarge.let {
                    if (selected) it + TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic
                    ) else it
                },
            )
        },
        supportingContent = {
            Text(
                text = "${tick.timeForData.toLocalDateTime(timeZone).date}",
                style = MaterialTheme.typography.titleSmall.let {
                    if (selected) it + TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Normal
                    ) else it + TextStyle(
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic
                    )
                }
            )
        },
        tonalElevation = LocalAbsoluteTonalElevation.current.let {
            if (selected) TonalElevation.heightOfNext(it, minimumLayer = 3)
            else it
        }
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
                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    InputChip(
                        selected = false,
                        onClick = onEdit,
                        border = null,
                        label = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Filled.Edit, "edit tick") }
                    )
                    Divider(Modifier.fillMaxHeight().padding(vertical = 4.dp).width(1.dp))
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
