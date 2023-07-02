package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.generateTestData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class GameCounterScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dao: CounterTickDao

    private val testContent = GameCounterTestContent(1L)

    @BindValue
    val contentSetter: OnCreateHookImpl = testContent

    private val primaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryIncButton)
    private val primaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryDecButton)
    private val secondaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryIncButton)
    private val secondaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryDecButton)

    @Before
    fun setUp() {
        hiltRule.inject()

        runBlocking { dao.generateTestData(counters = 1) }
    }

    @Test
    fun `All Buttons are displayed`() {
        primaryIncButton.assertIsDisplayed()
        secondaryIncButton.assertIsDisplayed()
        secondaryDecButton.assertIsDisplayed()
        primaryDecButton.assertIsDisplayed()
    }
}
