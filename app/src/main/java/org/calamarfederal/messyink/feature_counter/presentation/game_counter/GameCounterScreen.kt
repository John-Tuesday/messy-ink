package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import kotlin.math.absoluteValue

internal data class GameCounterColors(
    val primaryIncrementColors: ButtonColors,
    val secondaryIncrementColors: ButtonColors,
    val primaryDecrementColors: ButtonColors,
    val secondaryDecrementColors: ButtonColors,
)

@Composable
internal fun defaultGameCounterColors(
    primaryIncrementColors: ButtonColors = ButtonDefaults.buttonColors(),
    secondaryIncrementColors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    secondaryDecrementColors: ButtonColors = ButtonDefaults.filledTonalButtonColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ),
    primaryDecrementColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
    ),
) = GameCounterColors(
    primaryIncrementColors = primaryIncrementColors,
    secondaryIncrementColors = secondaryIncrementColors,
    primaryDecrementColors = primaryDecrementColors,
    secondaryDecrementColors = secondaryDecrementColors,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameCounterScreen(
    counterName: String,
    tickSum: Double,
    primaryIncrement: Double = 5.00,
    secondaryIncrement: Double = 1.00,
    onAddTick: (Double) -> Unit,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            GameCounterTopAppBar(
                onNavigateUp = onNavigateUp,
            )
        }
    ) { padding ->
        GameCounterLayout(
            counterName = counterName,
            tickSum = tickSum,
            primaryIncrement = primaryIncrement.absoluteValue,
            secondaryIncrement = secondaryIncrement.absoluteValue,
            onAddTick = onAddTick,
            modifier = Modifier
                .padding(16.dp)
                .padding(padding)
                .consumeWindowInsets(padding)
                .fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameCounterTopAppBar(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Filled.ArrowBack, "navigate up")
            }
        }
    )
}

@Composable
private fun GameCounterLayout(
    counterName: String,
    tickSum: Double,
    primaryIncrement: Double,
    secondaryIncrement: Double,
    onAddTick: (Double) -> Unit,
    modifier: Modifier = Modifier,
    primaryWeight: Float = 10f,
    secondaryWeight: Float = 7.5f,
    buttonTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
    colors: GameCounterColors = defaultGameCounterColors(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onAddTick(primaryIncrement) },
            colors = colors.primaryIncrementColors,
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth()
                .testTag(GameCounterTestTags.PrimaryIncButton)
        ) {
            Text(text = "+ $primaryIncrement", style = buttonTextStyle)
        }
        Button(
            onClick = { onAddTick(secondaryIncrement) },
            colors = colors.secondaryIncrementColors,
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth()
                .testTag(GameCounterTestTags.SecondaryIncButton)
        ) {
            Text(text = "+ $secondaryIncrement", style = buttonTextStyle)
        }
        Box(
            modifier = Modifier.weight(primaryWeight + secondaryWeight),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "$tickSum",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.testTag(GameCounterTestTags.TotalSumText)
                )
                Text(
                    text = counterName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag(GameCounterTestTags.CounterNameText)
                )
            }
        }
        Button(
            onClick = { onAddTick(secondaryIncrement) },
            colors = colors.secondaryDecrementColors,
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth()
                .testTag(GameCounterTestTags.SecondaryDecButton)
        ) {
            Text(text = "- $secondaryIncrement", style = buttonTextStyle)
        }
        Button(
            onClick = { onAddTick(-primaryIncrement) },
            colors = colors.primaryDecrementColors,
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth()
                .testTag(GameCounterTestTags.PrimaryDecButton)
        ) {
            Text(text = "- $primaryIncrement", style = buttonTextStyle)
        }

    }
}

@Preview
@Composable
private fun GameCounterScreen2Preview() {
    MessyInkTheme {
        GameCounterScreen(
            counterName = "counter name",
            tickSum = 0.00,
            onAddTick = {},
            onNavigateUp = {},
        )
    }
}
