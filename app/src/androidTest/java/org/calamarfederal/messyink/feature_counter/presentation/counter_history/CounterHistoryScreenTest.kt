package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.generateCounters
import org.calamarfederal.messyink.feature_counter.data.generateTicks
import org.calamarfederal.messyink.feature_counter.data.toCounter
import org.calamarfederal.messyink.feature_counter.data.toTick
import org.calamarfederal.messyink.feature_counter.domain.use_case.toUI
import org.calamarfederal.messyink.feature_counter.domain.use_case.toUi
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CounterHistoryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    lateinit var counterState: MutableStateFlow<UiCounter>
    lateinit var ticksState: MutableStateFlow<List<UiTick>>

    @Before
    fun setUp() {
        counterState = MutableStateFlow(generateCounters().take(1).first().toCounter().toUI())
        ticksState = MutableStateFlow(
            generateTicks(parentId = counterState.value.id).take(10).map { it.toTick().toUi() }
                .toList()
        )

        composeRule.setContent {
            val counter by counterState.collectAsState()
            val ticks by ticksState.collectAsState()
            val tickSum by remember {
                derivedStateOf { ticks.sumOf { it.amount } }
            }
            val tickAverage by remember {
                derivedStateOf { ticks.sumOf { it.amount } / ticks.size }
            }
            val graphRange by remember {
                derivedStateOf { ticks.minOf { it.amount } .. ticks.maxOf { it.amount } }
            }
            val graphDomain by remember {
                derivedStateOf { TimeDomain(ticks.minOf { it.timeForData } .. ticks.maxOf { it.timeForData }) }
            }
            val graphDomainLimits by remember {
                derivedStateOf { TimeDomain(Instant.DISTANT_PAST, Instant.DISTANT_FUTURE, true) }
            }

            CounterHistoryScreen(
                counter = counter,
                ticks = ticks,
                graphPoints = listOf(),
                tickSum = tickSum,
                tickAverage = tickAverage,
                graphRange = graphRange,
                graphDomain = graphDomain,
                graphDomainLimits = graphDomainLimits,
                graphDomainOptions = listOf(),
                changeGraphDomain = {},
                onAddTick = {},
                onDeleteTick = {},
                onResetCounter = {},
                onCounterChange = {},
                onNavigateUp = {},
            )
        }
    }

    @Test
    fun `Click navigation button to Show graph shows graph`() {
        composeRule.onNode(hasClickAction() and hasText(CounterHistoryTab.TickGraphs.displayName))
            .performClick()

        composeRule.onNodeWithTag(CounterHistoryTestTags.TickGraph).assertIsDisplayed()
    }

    @Test
    fun `Click navigation button to Show list shows list`() {
        composeRule.onNode(hasClickAction() and hasText(CounterHistoryTab.TickLogs.displayName))
            .performClick()

        composeRule.onNodeWithTag(CounterHistoryTestTags.TickGraph).assertIsNotDisplayed()
    }
}
