package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.compose.charts.LineGraph
import org.calamarfederal.messyink.common.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiTicks
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import kotlin.math.min

@Composable
internal fun TicksOverTimeLayout(
    ticks: List<UiTick>,
    domain: TimeDomain,
    domainOptions: List<TimeDomainTemplate>,
    changeDomain: (TimeDomain) -> Unit,
    modifier: Modifier = Modifier,
    minAmount: Double = min(0.00, ticks.minOf { it.amount }),
    maxAmount: Double = ticks.maxOf { it.amount },
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            TickAmountOverTime(
                ticks = ticks,
                minTime = domain.min,
                maxTime = domain.max,
                minAmount = minAmount,
                maxAmount = maxAmount,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    TextButton(
                        onClick = { expanded = true },
                        modifier = Modifier.padding(ButtonDefaults.TextButtonWithIconContentPadding)
                    ) {
                        Text(domain.label)
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Icon(
                            if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        for (opt in domainOptions) {
                            DropdownMenuItem(
                                text = { Text(opt.label) },
                                onClick = { changeDomain(opt.domain); expanded = false },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun TickAmountOverTime(
    ticks: List<UiTick>,
    modifier: Modifier = Modifier,
    graphModifier: Modifier = Modifier,
    minTime: Instant = ticks.minOf { it.timeForData },
    maxTime: Instant = ticks.maxOf { it.timeForData },
    minAmount: Double = min(0.00, ticks.minOf { it.amount }),
    maxAmount: Double = ticks.maxOf { it.amount },
) {
    val timeRange = maxTime - minTime
    val amountRange = maxAmount - minAmount

    LineGraph(
        modifier = modifier,
        graphModifier = graphModifier,
        lineGraphPoints = ticks.map {
            PointByPercent(
                x = (it.timeForData - minTime) / timeRange,
                y = (it.amount - minAmount) / amountRange,
            )
        },
        title = { Text("Amount Over Time") },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
private fun TickAmountOverTimePreview() {
    MessyInkTheme {
        Scaffold { padding ->
            TicksOverTimeLayout(
                ticks = previewUiTicks(1L).take(10).toList(),
                domain = TimeDomainTemplate.WeekAgo.domain,
                domainOptions = TimeDomainTemplate.Defaults,
                changeDomain = {},
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            )
        }
    }
}
