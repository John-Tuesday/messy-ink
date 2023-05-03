package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@Preview
@Composable
private fun GameCounterPreview() {
    MessyInkTheme {
        GameCounterScreen(
            counter = previewUiCounters.first(),
            tickSum = 5.00,
            onAddTick = {},
            onReset = {},
            onUndo = {},
            onRedo = {},
        )
    }
}

/**
 *  # Game Counter Screen
 *
 *  ## Display mode designed for tracking mainly simple integer data. (e.g. health in Magic the Gathering)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameCounterScreen(
    counter: UiCounter,
    tickSum: Double,
    onAddTick: (Double) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
//            GameCounterAppBar(
//                onReset = onReset,
//                onUndo = onUndo,
//                onRedo = onRedo,
//                onNavigateUp = onNavigateUp,
//            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .fillMaxSize()
        ) {
            GameCounterLayout(
                counter = counter,
                tickSum = tickSum,
                onAddTick = onAddTick,
                onRedo = onRedo,
                onReset = onReset,
                onUndo = onUndo,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun GameCounterLayout(
    counter: UiCounter,
    tickSum: Double,
    onAddTick: (Double) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAmountPrompt by rememberSaveable { mutableStateOf(false) }
    QuickTickButtons(
        modifier = modifier.fillMaxSize(),
        centerSlot = {
            CounterCenter(
                counter = counter,
                tickSum = tickSum,
                onAddCustomTick = { showAmountPrompt = true },
                onUndo = onUndo,
                onRedo = onRedo,
                onReset = onReset,
            )
        },
        onAddTick = onAddTick,
    )
    CustomTickEntryDialog(
        visible = showAmountPrompt,
        onDismiss = { showAmountPrompt = false },
        onAddTick = onAddTick,
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterCenter(
    counter: UiCounter,
    tickSum: Double,
    onAddCustomTick: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    enableUndo: Boolean = false,
    enableRedo: Boolean = false,
    enableReset: Boolean = false,
) {
    Surface(
        modifier = modifier,
        onClick = onAddCustomTick,
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
