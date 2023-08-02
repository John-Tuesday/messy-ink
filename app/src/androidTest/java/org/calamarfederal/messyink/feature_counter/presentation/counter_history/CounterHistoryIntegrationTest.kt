package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performSemanticsAction
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.R
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.common.presentation.format.omitWhen
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.TickEntity
import org.calamarfederal.messyink.feature_counter.di.TestTz
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.time.Duration.Companion.days


@HiltAndroidTest
class CounterHistoryIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val counterId = 1L
    private val counterName = "counter name"

    @BindValue
    val contentSetter: OnCreateHookImpl = object : OnCreateHookImpl() {
        override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
            with(activity) {
                setContent {
                    NavHost(
                        navController = rememberNavController(),
                        startDestination = "test/${CounterHistoryNode.COUNTER_ID}",
                    ) {
                        composable(
                            route = "test/${CounterHistoryNode.COUNTER_ID}",
                            arguments = listOf(
                                navArgument(CounterHistoryNode.COUNTER_ID) {
                                    defaultValue = counterId
                                }
                            )
                        ) {
                            CounterHistoryNode.CounterHistoryScreenBuilder(
                                onNavigateUp = {},
                                onNavigateToEditTick = {},
                            )
                        }
                    }
                }
            }
        }
    }

    @Inject
    lateinit var dao: CounterTickDao

    lateinit var testTicks: List<TickEntity>

    lateinit var minModified: String
    lateinit var maxModified: String
    lateinit var minCreated: String
    lateinit var maxCreated: String
    lateinit var minData: String
    lateinit var maxData: String
    lateinit var lowerDataDomain: String

    private val tickLogNavButton
        get() = composeRule
            .onNode(hasText(CounterHistoryTab.TickLogs.displayName) and hasClickAction())
    private val tickGraphNavButton
        get() = composeRule
            .onNode(hasText(CounterHistoryTab.TickGraphs.displayName) and hasClickAction())
    private val sortIconButton
        get() = composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.sort_icon_button_content_description)
        )
    private val sortByModifiedButton
        get() = composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.time_modified)
        )
    private val sortByCreatedButton
        get() = composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.time_created)
        )

    private val sortByDataButton
        get() = composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.time_for_data)
        )

    private val tickLogContainer get() = composeRule.onNodeWithTag(CounterHistoryTestTags.TickLogScrollable)

    @Before
    fun setUp() {
        hiltRule.inject()

        runBlocking {
            dao.insertCounter(
                CounterEntity(
                    name = counterName,
                    timeModified = Instant.fromEpochMilliseconds(0L),
                    timeCreated = Instant.fromEpochMilliseconds(0L),
                    id = counterId,
                )
            )

            val baseTime = Instant.fromEpochMilliseconds(30.days.inWholeMilliseconds)
            testTicks = (1L .. 10L).map { id ->
                TickEntity(
                    amount = id.toDouble(),
                    timeModified = baseTime - id.days,
                    timeCreated = baseTime - (2 * id).days,
                    timeForData = baseTime + id.days,
                    parentId = counterId,
                    id = id,
                )
            }
            for (t in testTicks)
                dao.insertTick(t)

            val dataDomainStrings = with(TestTz) {
                val start = baseTime.toLocalDateTime()
                val end = (baseTime + 10.days).toLocalDateTime()
                val formatter = DateTimeFormat().omitWhen(start, end)
                start.formatToString(formatter) to end.formatToString(formatter)
            }
            lowerDataDomain = dataDomainStrings.first
            minCreated = "10"
            maxCreated = "1"
            minModified = "10"
            maxModified = "1"
            minData = "1"
            maxData = "10"

        }
    }

    @Test
    fun `Delete tick actually deletes`() {
        runBlocking {
            assert(dao.tick(2L) != null)
        }
        composeRule
            .onNode(hasText(CounterHistoryTab.TickLogs.displayName) and hasClickAction())
            .performClick()

        composeRule
            .onNode(hasText("2") and hasTestTag(CounterHistoryTestTags.TickLogEntry))
            .performSemanticsAction(SemanticsActions.OnLongClick)

        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickLogEntryDelete)
            .performClick()

        composeRule.waitForIdle()
        runBlocking {
            assert(dao.tick(2L) == null)
            composeRule.awaitIdle()
        }

        composeRule
            .onNode(hasText("2") and hasTestTag(CounterHistoryTestTags.TickLogEntry))
            .assertIsNotDisplayed()
    }

    @Test
    fun `Changing sort order is respected`() {
        tickLogNavButton.performClick()

        sortIconButton.performClick()
        sortByModifiedButton.performClick()
        tickLogContainer.performScrollToIndex(0).onChildAt(0).assertTextContains(minModified)

        sortIconButton.performClick()
        sortByCreatedButton.performClick()
        tickLogContainer.performScrollToIndex(0).onChildAt(0).assertTextContains(minCreated)

        sortIconButton.performClick()
        sortByDataButton.performClick()
        tickLogContainer.performScrollToIndex(0).onChildAt(0).assertTextContains(minData)
    }

    @Test
    fun `Change domain to a single day`() {
        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickGraphDomainUpper)
            .performClick()

        val startDay = testTicks[3].timeForData.toLocalDateTime(TestTz)
        val endDay = startDay.date.atStartOfDayIn(TestTz).plus(1.days).toLocalDateTime(TestTz)
        val (lowerDomain, upperDomain) = DateTimeFormat()
            .omitWhen(
                startDay,
                endDay
            ).let {
                startDay.formatToString(it) to endDay.formatToString(it)
            }


        composeRule
            .onNodeWithText(
                "${startDay.month.name} ${startDay.dayOfMonth}",
                substring = true,
                ignoreCase = true
            )
            .performClick()

        composeRule
            .onNodeWithText(
                "${startDay.month.name} ${startDay.dayOfMonth}",
                substring = true,
                ignoreCase = true
            )
            .performClick()

        composeRule
            .onNodeWithTag(CounterHistoryTestTags.DomainPickerSave)
            .performClick()

        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickGraphDomainLower)
            .assertTextContains(lowerDomain)
        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickGraphDomainUpper)
            .assertTextContains(upperDomain)
    }

    @Test
    fun `Change domain changes domain`() {
        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickGraphDomainUpper)
            .performClick()

        val startDay = testTicks[3].timeForData.toLocalDateTime(TestTz).date.atTime(0, 0)
        val endDay = testTicks[8].timeForData.toLocalDateTime(TestTz)
        val upperDomainDay = endDay.date.atStartOfDayIn(TestTz).plus(1.days).toLocalDateTime(TestTz)
        val (lowerDomain, upperDomain) = DateTimeFormat().omitWhen(
            startDay,
            upperDomainDay,
        ).let {
            startDay.formatToString(it) to upperDomainDay.formatToString(it)
        }


        composeRule
            .onNodeWithText(
                "${startDay.month.name} ${startDay.dayOfMonth}",
                substring = true,
                ignoreCase = true
            )
            .performClick()

        composeRule
            .onNodeWithText(
                "${endDay.month.name} ${endDay.dayOfMonth}",
                substring = true,
                ignoreCase = true
            )
            .performClick()

        composeRule
            .onNodeWithTag(CounterHistoryTestTags.DomainPickerSave)
            .performClick()

        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickGraphDomainLower)
            .assertTextContains(lowerDomain)
        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickGraphDomainUpper)
            .assertTextContains(upperDomain)
    }

    @Test
    fun `Changing domain to fit to data persists after tick changes`() {
        composeRule.onAllNodesWithTag(CounterHistoryTestTags.TickGraphDomainLower)
            .filterToOne(hasText(lowerDataDomain))
            .assertExists()
        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickGraphDomainUpper)
            .performClick()

        composeRule
            .onNodeWithTag(CounterHistoryTestTags.DomainFitToData)
            .performClick()

        tickLogNavButton.performClick()
        composeRule
            .onNode(hasTestTag(CounterHistoryTestTags.TickLogEntry) and hasText(minData))
            .performSemanticsAction(SemanticsActions.OnLongClick)

        composeRule
            .onNodeWithTag(CounterHistoryTestTags.TickLogEntryDelete)
            .performClick()

        tickGraphNavButton.performClick()
        composeRule.onAllNodesWithTag(CounterHistoryTestTags.TickGraphDomainLower)
            .filterToOne(hasText(lowerDataDomain))
            .assertDoesNotExist()

    }
}
