package org.calamarfederal.messyink.feature_counter.presentation.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewTestTags
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterTestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CounterFeatureNavigationTests {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun `First screen is Overview`() {
        composeRule.onNodeWithTag(CounterOverviewTestTags.CreateCounterFab).assertIsDisplayed()
    }

    @Test
    fun `Overview to Create Screen flow`() {
        composeRule.onNodeWithTag(CounterOverviewTestTags.CreateCounterFab).performClick()
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField).assertIsDisplayed()
    }

    @Test
    fun `Overview to Create Screen and Close or Submit Flow`() {
        composeRule.onNodeWithTag(CounterOverviewTestTags.CreateCounterFab).performClick()
        composeRule.onNodeWithTag(CreateCounterTestTags.CloseButton).assertIsDisplayed()
        composeRule.onNodeWithTag(CreateCounterTestTags.CloseButton).performClick()
        composeRule.onNodeWithTag(CounterOverviewTestTags.CreateCounterFab).assertIsDisplayed()

        val testTitle: String = "test"
        composeRule.onNodeWithTag(CounterOverviewTestTags.CreateCounterFab).performClick()
        composeRule.onNodeWithTag(CreateCounterTestTags.SubmitButton).assertIsNotEnabled()
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField).performTextInput(testTitle)
        composeRule.onNodeWithTag(CreateCounterTestTags.SubmitButton).performClick()

        composeRule.onNodeWithTag(CounterOverviewTestTags.CounterItem).assertTextContains(testTitle)
    }
}
