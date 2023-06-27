package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
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

/**
 * # Counter Overview Screen
 * ## Unit Tests
 */
@HiltAndroidTest
class CounterOverviewScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val testContent = CounterOverviewContent()

    @BindValue
    val contentSetter: OnCreateHookImpl = testContent

    @Inject
    lateinit var dao: CounterTickDao

    @Before
    fun setUp() {
        hiltRule.inject()

        runBlocking {
            dao.generateTestData()
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
