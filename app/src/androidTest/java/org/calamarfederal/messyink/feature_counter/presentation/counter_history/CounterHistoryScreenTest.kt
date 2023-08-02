package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import kotlinx.coroutines.flow.MutableStateFlow
import org.calamarfederal.messyink.R
import org.calamarfederal.messyink.feature_counter.data.generateCounters
import org.calamarfederal.messyink.feature_counter.data.generateTicks
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.TickGraphState
import org.calamarfederal.messyink.feature_counter.data.repository.toCounter
import org.calamarfederal.messyink.feature_counter.data.repository.toTick
import org.calamarfederal.messyink.feature_counter.di.TestTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.days

class CounterHistoryScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    lateinit var counterState: MutableStateFlow<Counter>
    lateinit var ticksState: MutableStateFlow<List<Tick>>
    lateinit var tickSortState: MutableStateFlow<TickSort>
    lateinit var tickGraphState: MutableStateFlow<TickGraphState>

    private val tickLogNavButton
        get() = composeRule.onNode(
            hasText(composeRule.activity.getString(R.string.tick_log)) and hasClickAction()
        )
    private val tickGraphNavButton
        get() = composeRule
            .onNode(hasText(composeRule.activity.getString(R.string.tick_graph)) and hasClickAction())

    @Before
    fun setUp() {
        counterState = MutableStateFlow(generateCounters().take(1).first().toCounter())
        val tickStartTime = TestTime
        val tickEndTime = tickStartTime + 10.days
        ticksState = MutableStateFlow(
            generateTicks(
                startTime = tickStartTime,
                stepTime = 1.days,
                amount = { it.toDouble() },
                parentId = counterState.value.id,
            )
                .take(10).map { it.toTick() }
                .toList()
        )
        tickSortState = MutableStateFlow(TickSort.TimeForData)
        tickGraphState = MutableStateFlow(
            TickGraphState(
                currentDomain = tickStartTime .. tickEndTime,
                domainBounds = tickStartTime .. tickEndTime,
                currentRange = 1.00 .. 10.00,
                rangeBounds = 1.00 .. 10.00,
            ))

        composeRule.setContent {
            val ticks by ticksState.collectAsState()
            val tickSort by tickSortState.collectAsState()
            val graphState by tickGraphState.collectAsState()

            CounterHistoryScreen(
                ticks = ticks,
                tickSort = tickSort,
                graphState = graphState,
                onChangeSort = {},
                changeGraphDomain = {},
                onDeleteTick = {},
                onEditTick = {},
                onNavigateUp = {},
            )
        }
    }

    @Test
    fun `Click navigation button to Show graph shows graph`() {
        tickGraphNavButton.performClick()

        composeRule.onNodeWithTag(CounterHistoryTestTags.TickGraph).assertIsDisplayed()
    }

    @Test
    fun `Click navigation button to Show list shows list`() {
        tickLogNavButton.performClick()

        composeRule.onNodeWithTag(CounterHistoryTestTags.TickGraph).assertIsNotDisplayed()
    }

    @Test
    fun `Long Click Tick Log Entry shows Options`() {
        tickLogNavButton.performClick()

        composeRule.onAllNodesWithTag(CounterHistoryTestTags.TickLogEntry)
            .onFirst()
            .performSemanticsAction(SemanticsActions.OnLongClick)

        composeRule.onNodeWithTag(CounterHistoryTestTags.TickLogOptions).assertIsDisplayed()
        composeRule.onNodeWithTag(CounterHistoryTestTags.TickLogEntryEdit).assertIsDisplayed()
        composeRule.onNodeWithTag(CounterHistoryTestTags.TickLogEntryDelete).assertIsDisplayed()
    }

    @Test
    fun `Domain buttons bring up Domain picker`() {
        tickGraphNavButton.performClick()

        composeRule.onNodeWithTag(CounterHistoryTestTags.DomainDatePicker)
            .assertDoesNotExist()
        composeRule.onNodeWithTag(CounterHistoryTestTags.TickGraphDomainLower).performClick()
        composeRule.onNodeWithTag(CounterHistoryTestTags.DomainDatePicker)
            .assertExists()
            .performSemanticsAction(SemanticsActions.Dismiss)
            .assertDoesNotExist()
        composeRule.onNodeWithTag(CounterHistoryTestTags.TickGraphDomainUpper).performClick()
        composeRule.onNodeWithTag(CounterHistoryTestTags.DomainDatePicker)
            .assertExists()
            .performSemanticsAction(SemanticsActions.Dismiss)
            .assertDoesNotExist()
    }

    @Test
    fun `Domain Date Picker Fit to Data button also dismiss dialog`() {
        tickGraphNavButton.performClick()
        composeRule.onNodeWithTag(CounterHistoryTestTags.TickGraphDomainLower).performClick()
        composeRule.onNodeWithTag(CounterHistoryTestTags.DomainDatePicker).assertExists()

        composeRule.onNodeWithTag(CounterHistoryTestTags.DomainFitToData).performClick()
        composeRule.onNodeWithTag(CounterHistoryTestTags.DomainDatePicker).assertDoesNotExist()
    }
}
