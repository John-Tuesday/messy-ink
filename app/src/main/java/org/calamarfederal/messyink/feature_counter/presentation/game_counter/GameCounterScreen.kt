package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.TickButton.Primary
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme


/**
 *  # Game Counter Screen
 *
 *  ## Display mode designed for tracking mainly simple integer data. (e.g. health in Magic the Gathering)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameCounterScreen(
    counter: UiCounter,
    tickSum: Double,
    primaryIncrement: Double,
    onChangePrimaryIncrement: (Double) -> Unit,
    secondaryIncrement: Double,
    onChangeSecondaryIncrement: (Double) -> Unit,
    onAddTick: (Double) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    onEditCounter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .fillMaxSize(),
        ) {
            GameCounterLayout(
                counter = counter,
                tickSum = tickSum,
                primaryIncrement = primaryIncrement,
                onChangePrimaryIncrement = onChangePrimaryIncrement,
                secondaryIncrement = secondaryIncrement,
                onChangeSecondaryIncrement = onChangeSecondaryIncrement,
                onAddTick = onAddTick,
                onRedo = onRedo,
                onReset = onReset,
                onUndo = onUndo,
                onEditCounter = onEditCounter,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
internal fun GameCounterLayout(
    counter: UiCounter,
    tickSum: Double,
    primaryIncrement: Double,
    onChangePrimaryIncrement: (Double) -> Unit,
    secondaryIncrement: Double,
    onChangeSecondaryIncrement: (Double) -> Unit,
    onAddTick: (Double) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    onEditCounter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var customIncrementPrompt by rememberSaveable { mutableStateOf(false) }
    var editIncrementOf by rememberSaveable(primaryIncrement, secondaryIncrement) {
        mutableStateOf<TickButton?>(null)
    }
    CompactTickButtons(
        modifier = modifier.fillMaxSize(),
        centerSlot = {
            CounterCenter(
                counter = counter,
                tickSum = tickSum,
                onAddCustomTick = { customIncrementPrompt = true },
                onUndo = onUndo,
                onRedo = onRedo,
                onReset = onReset,
                onEditCounter = onEditCounter,
                modifier = Modifier.testTag(GameCounterTestTags.SummaryBox)
            )
        },
        onAddTick = onAddTick,
        onEditIncrement = { editIncrementOf = it },
        primaryAmount = primaryIncrement,
        secondaryAmount = secondaryIncrement,
    )
    AnimatedVisibility(
        visible = customIncrementPrompt,
        label = "custom tick"
    ) {
        EditIncrementDialog(
            currentAmount = 0.00,
            onChangeAmount = onAddTick,
            onDismissRequest = { customIncrementPrompt = false })
    }
    AnimatedContent(
        targetState = editIncrementOf,
        label = "edit tick amount"
    ) { tickButton ->
        if (tickButton != null) {
            EditIncrementDialog(
                currentAmount = if (tickButton == Primary) primaryIncrement else secondaryIncrement,
                onChangeAmount = if (tickButton == Primary) onChangePrimaryIncrement else onChangeSecondaryIncrement,
                onDismissRequest = { editIncrementOf = null })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CounterCenter(
    counter: UiCounter,
    tickSum: Double,
    onAddCustomTick: () -> Unit,
    onEditCounter: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    enableUndo: Boolean = false,
    enableRedo: Boolean = false,
    enableReset: Boolean = false,
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        modifier = modifier
            .combinedClickable(
                onClick = { onAddCustomTick() },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEditCounter()
                }
            ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            val sumStr = remember(tickSum) {
                if (tickSum.toLong().toDouble() == tickSum)
                    tickSum.toLong().toString()
                else
                    tickSum.toString()
            }
            Text(
                text = sumStr,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = counter.name,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
            )
            AnimatedEditActions(
                onRedo = onRedo,
                onUndo = onUndo,
                onReset = onReset,
                enableRedo = enableRedo,
                enableUndo = enableUndo,
                enableReset = enableReset,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * Currently always hidden because there is not support for Undo / Redo / Reset
 */
@Composable
private fun AnimatedEditActions(
    onRedo: () -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    enableRedo: Boolean,
    enableUndo: Boolean,
    enableReset: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            this@Row.AnimatedVisibility(visible = enableUndo) {
                IconButton(onClick = onUndo) {
                    Icon(Icons.Filled.Undo, "undo")
                }
            }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            this@Row.AnimatedVisibility(visible = enableReset) {
                IconButton(onClick = onReset) {
                    Icon(Icons.Filled.RestartAlt, "reset")
                }
            }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            this@Row.AnimatedVisibility(visible = enableRedo) {
                IconButton(onClick = onRedo) {
                    Icon(Icons.Filled.Redo, "redo")
                }
            }
        }
    }
}

/**
 * ## Preview
 */
@Preview
@Composable
private fun GameCounterPreview() {
    MessyInkTheme {
        GameCounterScreen(
            counter = previewUiCounters.first(),
            tickSum = 5.00,
            primaryIncrement = 5.00,
            onChangePrimaryIncrement = {},
            secondaryIncrement = 1.00,
            onChangeSecondaryIncrement = {},
            onAddTick = {},
            onReset = {},
            onUndo = {},
            onEditCounter = {},
            onRedo = {},
        )
    }
}
