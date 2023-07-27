package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.common.presentation.format.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.ui.theme.MaterialLevel
import org.calamarfederal.messyink.ui.theme.toMaterialLevelCiel


/**
 * Provide a description of each tick and enable modification on long press
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TickLogsLayout(
    ticks: List<UiTick>,
    onDelete: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    sort: TickSort,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = ticks, key = { it.id }) { tick ->
            var showOptions by remember { mutableStateOf(false) }
            val haptic = LocalHapticFeedback.current
            Box {
                TickListItem(
                    tick = tick,
                    sort = sort,
                    selected = showOptions,
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showOptions = true
                            },
                            onClick = { showOptions = false },
                        )
                        .testTag(CounterHistoryTestTags.TickLogEntry),
                )
                TickOptions(
                    visible = showOptions,
                    onDismiss = { showOptions = false },
                    onDelete = { onDelete(tick.id); showOptions = false },
                    onEdit = { onEdit(tick.id); showOptions = false },
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
    sort: TickSort? = null,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    dateTimeFormat: DateTimeFormat = DateTimeFormat(),
) {
    ListItem(modifier = modifier, headlineContent = {
        Text(
            text = tick.amount.toStringAllowShorten(),
            style = MaterialTheme.typography.titleLarge.let {
                if (selected) it + TextStyle(
                    fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic
                ) else it
            },
        )
    }, supportingContent = {
        val style = MaterialTheme.typography.titleSmall.let {
            if (selected) it + TextStyle(
                fontWeight = FontWeight.Medium, fontStyle = FontStyle.Normal
            ) else it + TextStyle(
                fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic
            )
        }
        val pickedMod = TextStyle(fontWeight = FontWeight.SemiBold)
        val timeForData by remember(tick.timeForData, timeZone, dateTimeFormat) {
            derivedStateOf {
                "data: ${
                    tick.timeForData.toLocalDateTime(timeZone).formatToString(dateTimeFormat)
                }"
            }
        }
        val timeModified by remember(tick.timeModified, timeZone, dateTimeFormat) {
            derivedStateOf {
                "modified: ${
                    tick.timeModified.toLocalDateTime(timeZone).formatToString(dateTimeFormat)
                }"
            }
        }
        val timeCreated by remember(tick.timeCreated, timeZone, dateTimeFormat) {
            derivedStateOf {
                "created: ${
                    tick.timeCreated.toLocalDateTime(timeZone).formatToString(dateTimeFormat)
                }"
            }
        }
        Column {

            Text(
                text = timeForData,
                style = style.merge(if (sort == TickSort.TimeForData) pickedMod else null)
            )
            Text(
                text = timeModified,
                style = style.merge(if (sort == TickSort.TimeModified) pickedMod else null)
            )
            Text(
                text = timeCreated,
                style = style.merge(if (sort == TickSort.TimeCreated) pickedMod else null)
            )
        }
    }, tonalElevation = LocalAbsoluteTonalElevation.current.let {
        if (selected) it.toMaterialLevelCiel().coerceAtLeast(MaterialLevel(3)).elevation
        else it
    })
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
            ElevatedCard(modifier = Modifier.testTag(CounterHistoryTestTags.TickLogOptions)) {
                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    InputChip(
                        selected = false,
                        onClick = onEdit,
                        border = null,
                        label = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Filled.Edit, "edit tick") },
                        modifier = Modifier.testTag(CounterHistoryTestTags.TickLogEntryEdit),
                    )
                    Divider(
                        Modifier
                            .fillMaxHeight()
                            .padding(vertical = 4.dp)
                            .width(1.dp)
                    )
                    InputChip(
                        selected = false,
                        onClick = onDelete,
                        border = null,
                        label = { Text("Delete") },
                        leadingIcon = { Icon(Icons.Filled.DeleteForever, "delete forever") },
                        modifier = Modifier.testTag(CounterHistoryTestTags.TickLogEntryDelete),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TickLogsScreenPreview() {
    TickLogsLayout(
        ticks = previewUiTicks(1L).take(15).toList(),
        sort = TickSort.TimeForData,
        onDelete = {},
        onEdit = {},
    )
}
