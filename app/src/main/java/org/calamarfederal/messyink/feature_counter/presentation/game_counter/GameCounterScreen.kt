package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import kotlin.math.absoluteValue

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
    onNavigateUp: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            GameCounterAppBar(
                onReset = onReset,
                onUndo = onUndo,
                onRedo = onRedo,
                onNavigateUp = onNavigateUp,
            )
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
    modifier: Modifier = Modifier,
) {
    var showAmountPrompt by rememberSaveable { mutableStateOf(false) }
    VerticalGameCounter(
        counter = counter,
        tickSum = tickSum,
        onAddTick = onAddTick,
        onAddCustomTick = { showAmountPrompt = true },
        modifier = modifier
    )
    CustomTickEntryDialog(
        visible = showAmountPrompt,
        onDismiss = { showAmountPrompt = false },
        onAddTick = onAddTick,
    )
}

@Composable
private fun VerticalGameCounter(
    counter: UiCounter,
    tickSum: Double,
    onAddTick: (Double) -> Unit,
    onAddCustomTick: () -> Unit,
    modifier: Modifier = Modifier,
    mainAmount: Double = 5.00,
    mediumAmount: Double = 2.00,
    smallAmount: Double = 1.00,
    mainWeight: Float = 10f,
    mediumWeight: Float = 7.5f,
    smallWeight: Float = 5f,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MainTickButton(
            amount = mainAmount,
            onClick = onAddTick,
            modifier = Modifier
                .weight(mainWeight)
                .fillMaxWidth()
        )
        MiddleTickButton(
            amount = mediumAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(mediumWeight)
                .fillMaxWidth()
        )
        SmallestTickButton(
            amount = smallAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(smallWeight)
                .fillMaxWidth()
        )
        CounterCenter(
            counter = counter,
            tickSum = tickSum,
            onAddCustomTick = onAddCustomTick,
            modifier = Modifier.weight(2 * mainWeight),
        )
        SmallestTickButton(
            amount = -smallAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(smallWeight)
                .fillMaxWidth()
        )
        MiddleTickButton(
            amount = -mediumAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(mediumWeight)
                .fillMaxWidth()
        )
        MainTickButton(
            amount = -mainAmount,
            onClick = onAddTick,
            modifier = Modifier
                .weight(mainWeight)
                .fillMaxWidth()
        )
    }
}


@Composable
private fun MainTickButton(
    amount: Double,
    onClick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onClick(amount) },
        modifier = modifier,
    ) {
        Icon(if (amount < 0) Icons.Filled.Remove else Icons.Filled.Add, "add $amount")
        Text("${amount.absoluteValue}")
    }
}


@Composable
private fun MiddleTickButton(
    amount: Double,
    onClick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick = { onClick(amount) },
        modifier = modifier,
    ) {
        Icon(if (amount < 0) Icons.Filled.Remove else Icons.Filled.Add, "add $amount")
        Text("${amount.absoluteValue}")
    }
}

@Composable
private fun SmallestTickButton(
    amount: Double,
    onClick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = { onClick(amount) },
        modifier = modifier,
    ) {
        Icon(if (amount < 0) Icons.Filled.Remove else Icons.Filled.Add, "add $amount")
        Text("${amount.absoluteValue}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterCenter(
    counter: UiCounter,
    tickSum: Double,
    onAddCustomTick: () -> Unit,
    modifier: Modifier = Modifier,
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
        }
    }
}
