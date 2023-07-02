package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.generateCounters
import org.calamarfederal.messyink.feature_counter.data.generateTestData
import org.calamarfederal.messyink.feature_counter.data.toCounter
import org.calamarfederal.messyink.feature_counter.domain.use_case.toUI
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * # Counter Overview Screen
 * ## Unit Tests
 */
@RunWith(AndroidJUnit4::class)
class CounterOverviewScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    lateinit var countersState: MutableStateFlow<List<UiCounter>>
    lateinit var tickSumState: MutableStateFlow<Map<Long, Double>>

    @Before
    fun setUp() {
        countersState =
            MutableStateFlow(generateCounters().take(5).map { it.toCounter().toUI() }.toList())
        tickSumState = MutableStateFlow(countersState.value.associate { it.id to 1.00 })

        composeRule.setContent {
            val counters by countersState.collectAsState()
            val tickSum by tickSumState.collectAsState()

            CounterOverviewScreen(
                counters = counters,
                tickSums = tickSum,
                onCounterIncrement = {},
                onCounterDecrement = {},
                onDeleteCounter = {},
                onClearCounterTicks = {},
                onCreateCounter = {},
                onNavigateToCounterDetails = {},
                onNavigateToCounterGameMode = {},
                onNavigateToCounterEdit = {},
            )
        }
    }

    @Test
    fun `Counter Item long click shows more options`() {
        composeRule.onNodeWithTag(CounterOverviewTestTags.CounterOptions).assertDoesNotExist()
        composeRule.onAllNodesWithTag(CounterOverviewTestTags.CounterItem).onFirst()
            .performSemanticsAction(SemanticsActions.OnLongClick)
        composeRule.onNodeWithTag(CounterOverviewTestTags.CounterOptions).assertIsDisplayed()
    }

    @Test
    fun `Counter options disappear on dismissal`() {
        composeRule.onAllNodesWithTag(CounterOverviewTestTags.CounterItem).onFirst()
            .performSemanticsAction(SemanticsActions.OnLongClick)
        composeRule.onNodeWithTag(CounterOverviewTestTags.CounterOptions).assertIsDisplayed()
        composeRule.onNodeWithTag(CounterOverviewTestTags.CounterOptions)
            .performSemanticsAction(SemanticsActions.Dismiss)
        composeRule.onNodeWithTag(CounterOverviewTestTags.CounterOptions).assertDoesNotExist()
    }
}
