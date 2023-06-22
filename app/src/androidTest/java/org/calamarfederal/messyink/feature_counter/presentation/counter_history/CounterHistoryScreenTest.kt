package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CounterHistoryScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val testContent = CounterHistoryTestContent()

    @BindValue
    val contentSetter: OnCreateHookImpl = testContent

    @Before
    fun setUp() {
        hiltRule.inject()
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
