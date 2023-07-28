package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.calamarfederal.messyink.feature_counter.data.generateCounters
import org.calamarfederal.messyink.feature_counter.data.repository.toCounter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameCounterScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val testCounter = generateCounters().first().toCounter()
    private val testSum = 1.23

    private val primaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryIncButton)
    private val primaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryDecButton)
    private val secondaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryIncButton)
    private val secondaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryDecButton)

    @Before
    fun setUp() {
        composeRule.setContent {
            val counter = testCounter
            val sum = testSum
            val primaryInc = 5.00
            val secondaryInc = 5.00

            GameCounterScreen(
                counter = counter,
                tickSum = sum,
                primaryIncrement = primaryInc,
                onChangePrimaryIncrement = {},
                secondaryIncrement = secondaryInc,
                onChangeSecondaryIncrement = {},
                onAddTick = {},
                onUndo = {},
                onRedo = {},
                onReset = {},
                onEditCounter = {},
            )
        }
    }

    @Test
    fun `All Buttons are displayed`() {
        primaryIncButton.assertIsDisplayed()
        secondaryIncButton.assertIsDisplayed()
        secondaryDecButton.assertIsDisplayed()
        primaryDecButton.assertIsDisplayed()
    }

    @Test
    fun `Counter name is displayed`() {
        composeRule.onNodeWithTag(GameCounterTestTags.SummaryBox)
            .assertTextContains(testCounter.name)
    }

    @Test
    fun `Sum is displayed`() {
        composeRule.onNodeWithTag(GameCounterTestTags.SummaryBox)
            .assertTextContains("$testSum", substring = true)
    }
}
