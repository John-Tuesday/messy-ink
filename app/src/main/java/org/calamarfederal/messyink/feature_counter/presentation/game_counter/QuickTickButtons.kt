package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventPass.Initial
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withTimeoutOrNull
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CompactTickButtons(
    centerSlot: @Composable () -> Unit,
    onAddTick: (Double) -> Unit,
    onChangePrimaryAmount: (Double) -> Unit,
    onChangeSecondaryAmount: (Double) -> Unit,
    modifier: Modifier = Modifier,
    primaryWeight: Float = 10f,
    primaryAmount: Double = 5.00,
    secondaryWeight: Float = 7.5f,
    secondaryAmount: Double = 1.00,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val haptic = LocalHapticFeedback.current
        Button(
            onClick = { onAddTick(primaryAmount); println("button click :(") },
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Add, "add $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
        FilledTonalButton(
            onClick = { onAddTick(secondaryAmount) },
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Add, "add $secondaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(secondaryAmount.toStringAllowShorten(), style = textStyle)
        }
        Box(modifier = Modifier.weight(primaryWeight * 2)) {
            centerSlot()
        }
        FilledTonalButton(
            onClick = { onAddTick(-secondaryAmount) },
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Remove, "subtract $secondaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(secondaryAmount.toStringAllowShorten(), style = textStyle)
        }
        Button(
            onClick = { onAddTick(-primaryAmount) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
            ),
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Remove, "subtract $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
    }
}

@Composable
private fun ButtonFacade(
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    button: @Composable () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    Box(modifier = modifier
        .pointerInput(onClick, onLongClick) {
            awaitEachGesture {
                val pass = Initial
                awaitFirstDown(pass = pass)
                val result = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                    waitForUpOrCancellation() != null
                }
                when (result) {
                    true  -> onClick()
                    null  -> onLongClick()
                    false -> Unit
                }
                awaitPointerEvent(pass = pass).changes.forEach { it.consume() }
//            awaitLongPressOrCancellation(id)?.let {
//                haptic.performHapticFeedback(LongPress)
//                onLongClick()
//            }
            }
        }
        .semantics(mergeDescendants = true) {
            this.onLongClick(label = null, action = { onLongClick(); true })
        }
    )
}

