package org.calamarfederal.messyink.feature_counter.presentation.test_start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Preview
@Composable
private fun TestStartPreview() {
    TestStartScreen(
        counters = previewUiCounters.take(5).toList(),
        ticksSum = mapOf(),
        onCreateCounter = {},
        onAddTick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TestStartScreen(
    counters: List<UiCounter>,
    ticksSum: Map<Long, Double>,
    onCreateCounter: () -> Unit,
    onAddTick: (UiTick) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateCounter) {
                Icon(Icons.Filled.Create, "spawn counter")
            }
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            TestStartLayout(
                counters = counters,
                ticksSum = ticksSum,
                onAddTick = onAddTick,
            )
        }
    }
}

@Composable
private fun TestStartLayout(
    counters: List<UiCounter>,
    ticksSum: Map<Long, Double>,
    onAddTick: (UiTick) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = counters, key = { it.id }) { counter ->
            TestCounterShow(
                counter = counter,
                ticksSum = ticksSum[counter.id] ?: 0.0,
                onAddTick = { onAddTick(UiTick(amount = it, parentId = counter.id, id = NOID)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestCounterShow(
    counter: UiCounter,
    ticksSum: Double,
    onAddTick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        leadingContent = {},
        overlineText = { Text(counter.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
        headlineText = { PlusMinusButtons(ticksSum = ticksSum, onAddTick = onAddTick) },
        supportingText = {TickSumText(text = "$ticksSum")},
        trailingContent = {
        },
    )
}

@Composable
private fun TickSumText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Normal)
}

@Composable
private fun PlusMinusButtons(
    ticksSum: Double,
    onAddTick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
//        Text(
//            text = "$ticksSum",
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.padding(4.dp)
//        )
        FilledTonalIconButton(
            onClick = { onAddTick(-1.0) },
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
        ) {
            Icon(Icons.Filled.Remove, "add negative default tick")
        }
        FilledIconButton(
            onClick = { onAddTick(1.0) },
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
        ) {
            Icon(Icons.Filled.Add, "add default tick")
        }
    }
}
