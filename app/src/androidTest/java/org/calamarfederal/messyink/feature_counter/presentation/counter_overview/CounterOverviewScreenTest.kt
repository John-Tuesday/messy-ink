package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.text
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.generateTestData
import org.junit.Assert.*

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
    lateinit var dao: CounterDao

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
