package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.data.entity.TickColumn
import org.calamarfederal.messyink.feature_counter.data.TestTime
import org.calamarfederal.messyink.feature_counter.data.generateCounters
import org.calamarfederal.messyink.feature_counter.data.generateTicks
import org.calamarfederal.messyink.feature_counter.data.toCounter
import org.calamarfederal.messyink.feature_counter.domain.use_case.toUI
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class GameCounterScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val testCounter = generateCounters().first().toCounter().toUI()
    private val testSum = 1.23
    private val testContent = GameCounterTestContent(
        counter = testCounter,
        sum = testSum,
    )

    @BindValue
    val contentSetter: OnCreateHookImpl = testContent

    private val primaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryIncButton)
    private val primaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryDecButton)
    private val secondaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryIncButton)
    private val secondaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryDecButton)

    @Before
    fun setUp() {
        hiltRule.inject()
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
