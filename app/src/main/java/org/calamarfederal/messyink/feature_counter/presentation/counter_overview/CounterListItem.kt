package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import org.calamarfederal.messyink.ui.theme.toMaterialLevelCiel

/**
 * Counter details for use in a (vertical) list
 *
 * Provide summary details and easy access to common features
 *
 * Having a hard time settling on the exact features to include and how it should be presented
 */
@Composable
internal fun CounterListCard(
    counter: UiCounter,
    amount: Double?,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onViewInFull: () -> Unit,
    onEditCounter: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    tonalElevation: Dp = LocalAbsoluteTonalElevation.current.toMaterialLevelCiel().inc().elevation,
) {
    Surface(
        shape = shape,
        tonalElevation = tonalElevation,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(
                    text = counter.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alignByBaseline()
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$amount",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alignByBaseline()
                )
            }
            IncButtons(
                onIncrement = onIncrement,
                onDecrement = onDecrement,
                modifier = Modifier
                    .height(Max)
                    .padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.align(alignment = Alignment.End)
            ) {
                ExtendedOptions(
                    onSettings = onEditCounter,
                    onHistory = onHistory,
                    onExpand = onViewInFull,
                )
            }
        }
    }
}

@Composable
private fun RowScope.ExtendedOptions(
    onSettings: () -> Unit,
    onHistory: () -> Unit,
    onExpand: () -> Unit,
) {
    TextButton(onClick = onHistory) {
        Text("History")
        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        Icon(Icons.Filled.Timeline, null)
    }
    TextButton(onClick = onExpand) {
        Text("Full Screen")
        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        //Material 3 Icon ExpandContent is missing???
        // Considering: ExpandContent, OpenInFull, FitScreen, Fullscreen
        Icon(Icons.Filled.Fullscreen, null)
    }
//    TextButton(onClick = onSettings) {
////        Text("Configure")
////        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
//        Icon(Icons.Filled.Settings, "configure counter")
//    }

}

@Composable
private fun IncButtons(
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.height(Max)) {
//        val buttonSize = 60.dp
        FilledIconButton(
            onClick = onDecrement,
            shape = MaterialTheme.shapes.large,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ),
            modifier = Modifier
                .weight(1f, fill = true)
                .fillMaxHeight()
//                .height(buttonSize)
        ) {
            Icon(Icons.Filled.Remove, "decrement counter")
        }
        Spacer(Modifier.weight(1f / 3f, fill = true))
        FilledIconButton(
            onClick = onIncrement,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .weight(1f, fill = true)
                .fillMaxHeight()
//                .height(buttonSize)
        ) {
            Icon(Icons.Filled.Add, "increase counter")
        }
    }
}

@Preview
@Composable
private fun PreviewCounterListCard() {
    MessyInkTheme {
        CounterListCard(
            counter = previewUiCounters.first(),
            amount = 99.00,
            onIncrement = {},
            onDecrement = {},
            onViewInFull = {},
            onEditCounter = {},
            onHistory = {},
        )
    }
}
