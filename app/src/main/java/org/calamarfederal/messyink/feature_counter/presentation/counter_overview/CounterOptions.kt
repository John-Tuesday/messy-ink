package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.More
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup


/**
 * Popup showing quick options to manipulate counter
 *
 * @param[alignment] hoisted Popup alignment
 * @param[offset] hoisted Popup offset
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CounterOptionsPopup(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDetails: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter,
    offset: IntOffset = IntOffset.Zero,
) {
    AnimatedVisibility(visible = visible, modifier = modifier) {
        Popup(
            onDismissRequest = onDismiss,
            alignment = alignment,
            offset = offset,
        ) {
            ElevatedCard {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    InputChip(
                        selected = false,
                        onClick = onDetails,
                        border = null,
                        label = { Text("Details") },
                        leadingIcon = { Icon(Filled.More, "show more details") }
                    )

                    Divider(Modifier.fillMaxHeight().width(1.dp))

                    InputChip(
                        selected = false,
                        onClick = onClear,
                        border = null,
                        label = { Text("Clear") },
                        leadingIcon = { Icon(Filled.ClearAll, "clear all") },
                    )

                    InputChip(
                        selected = false,
                        onClick = onDelete,
                        border = null,
                        label = { Text("Delete") },
                        leadingIcon = {
                            Icon(Filled.DeleteForever, "delete and its contents")
                        }
                    )
                }
            }
        }
    }

}
