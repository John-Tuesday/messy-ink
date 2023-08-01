package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.TickEntity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CounterOverviewIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dao: CounterTickDao

    private val time = Instant.fromEpochMilliseconds(0L)

    private val startingCounters = listOf(
        CounterEntity(
            name = "first test",
            timeModified = time,
            timeCreated = time,
            id = 1L,
        ),
        CounterEntity(
            name = "SEconD Test",
            timeModified = time,
            timeCreated = time,
            id = 2L,
        ),
    )

    private val startingTicks = listOf(
        TickEntity(
            amount = 1.00,
            timeModified = time,
            timeCreated = time,
            timeForData = time,
            parentId = 2L,
            id = 10L,
        ),
        TickEntity(
            amount = -10.00,
            timeModified = time,
            timeCreated = time,
            timeForData = time,
            parentId = 2L,
            id = 11L,
        ),
    )

    @Before
    fun setUp() {
        hiltRule.inject()

        runBlocking {
            for (c in startingCounters)
                dao.insertCounter(c)

            for (t in startingTicks)
                dao.insertTick(t)
        }
    }

    @Test
    fun `All counters are present`() {
        composeRule
            .onAllNodesWithTag(CounterOverviewTestTags.CounterItem)
            .filterToOne(hasText(startingCounters[0].name))
            .assertExists()
            .assertTextContains("null")
        composeRule
            .onAllNodesWithTag(CounterOverviewTestTags.CounterItem)
            .filterToOne(hasText(startingCounters[1].name))
            .assertExists()
            .assertTextContains("${-9.00}")
    }
}
