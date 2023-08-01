package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.model.TickSort.TimeCreated
import org.calamarfederal.messyink.feature_counter.data.model.TickSort.TimeForData
import org.calamarfederal.messyink.feature_counter.data.model.TickSort.TimeModified
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class TickGraphRepositoryUnitTest {
    private lateinit var repo: TickGraphRepository

    @Before
    fun setUp() {
        val dep = DummyTickRepository()
        repo = TickGraphRepositoryImpl(dep)
    }

    @Test
    fun `getMaxBounds gets the correct bounds from the correct category`() {
        val baseTimeModified = Instant.fromEpochMilliseconds(15.days.inWholeMilliseconds)
        val minTimeModified = Instant.fromEpochMilliseconds(10.days.inWholeMilliseconds)
        val maxTimeModified = Instant.fromEpochMilliseconds(19.days.inWholeMilliseconds)
        val baseTimeCreated = Instant.fromEpochMilliseconds(25.days.inWholeMilliseconds)
        val minTimeCreated = Instant.fromEpochMilliseconds(20.days.inWholeMilliseconds)
        val maxTimeCreated = Instant.fromEpochMilliseconds(29.days.inWholeMilliseconds)
        val baseTimeForData = Instant.fromEpochMilliseconds(35.days.inWholeMilliseconds)
        val minTimeForData = Instant.fromEpochMilliseconds(30.days.inWholeMilliseconds)
        val maxTimeForData = Instant.fromEpochMilliseconds(39.days.inWholeMilliseconds)
        val baseAmount = 1.00
        val minAmount = 0.50
        val maxAmount = 1.50
        val baseTick = Tick(
            amount = baseAmount,
            timeModified = baseTimeModified,
            timeCreated = baseTimeCreated,
            timeForData = baseTimeForData,
            parentId = 1L,
            id = 0L,
        )
        val testTicks = (1L .. 10L).map { baseTick.copy(id = it) }.shuffled()

        runBlocking {
            val result = repo.getMaxBounds(testTicks, TickSort.TimeForData)
            assert(result.second == baseAmount .. baseAmount) { "range = ${result.first}" }
            assert(result.first == baseTimeForData .. baseTimeForData)
        }
        runBlocking {
            val result = repo.getMaxBounds(testTicks, TickSort.TimeModified)
            assert(result.second == baseAmount .. baseAmount)
            assert(result.first == baseTimeModified .. baseTimeModified)
        }
        runBlocking {
            val result = repo.getMaxBounds(testTicks, TickSort.TimeCreated)
            assert(result.second == baseAmount .. baseAmount)
            assert(result.first == baseTimeCreated .. baseTimeCreated)
        }

        val testTicksAlt = (testTicks + baseTick.copy(
            amount = minAmount,
            timeModified = minTimeModified,
            timeCreated = minTimeCreated,
            timeForData = minTimeForData,
            id = 11L,
        ) + baseTick.copy(
            amount = maxAmount,
            timeModified = maxTimeModified,
            timeCreated = maxTimeCreated,
            timeForData = maxTimeForData,
            id = 12L,
        )).shuffled()

        runBlocking {
            val result = repo.getMaxBounds(testTicksAlt, TickSort.TimeForData)
            assert(result.second == minAmount .. maxAmount)
            assert(result.first == minTimeForData .. maxTimeForData)
        }
        runBlocking {
            val result = repo.getMaxBounds(testTicksAlt, TickSort.TimeModified)
            assert(result.second == minAmount .. maxAmount)
            assert(result.first == minTimeModified .. maxTimeModified)
        }
        runBlocking {
            val result = repo.getMaxBounds(testTicksAlt, TickSort.TimeCreated)
            assert(result.second == minAmount .. maxAmount)
            assert(result.first == minTimeCreated .. maxTimeCreated)
        }
    }

    @Test
    fun `convertToGraphPoints throws when domain or range has a width of 0`() {
        runBlocking {
            val testDomain = Instant.fromEpochMilliseconds(1.days.inWholeMilliseconds).let {
                it .. it
            }
            val safeDomain = Instant.fromEpochMilliseconds(1.days.inWholeMilliseconds).let {
                it .. (it + 1.days)
            }
            try {
                repo.convertToGraphPoints(
                    ticks = listOf(),
                    sort = TimeForData,
                    domain = testDomain,
                    range = 0.00 .. 1.00,
                )
                assert(false) { "Failed to throw when domain is single" }
            } catch (_: IllegalArgumentException) {
            }
            try {
                repo.convertToGraphPoints(
                    ticks = listOf(),
                    sort = TimeForData,
                    domain = safeDomain,
                    range = 1.00 .. 1.00,
                )
                assert(false) { "Failed to throw when range is single" }
            } catch (_: IllegalArgumentException) {
            }
            try {
                repo.convertToGraphPoints(
                    ticks = listOf(),
                    sort = TimeForData,
                    domain = testDomain,
                    range = 1.00 .. 1.00,
                )
                assert(false) { "Failed to throw when domain and range are single" }
            } catch (_: IllegalArgumentException) {
            }
        }
    }

    @Test
    fun `getGraphPointsFlow Range is expanded when max == min`() {
        val minAmount = -5.00
        val baseAmount = 2.00
        val maxAmount = 5.00
        val minTimeModified = Instant.fromEpochMilliseconds(1.days.inWholeMilliseconds)
        val baseTimeModified = Instant.fromEpochMilliseconds(2.days.inWholeMilliseconds)
        val maxTimeModified = Instant.fromEpochMilliseconds(3.days.inWholeMilliseconds)
        val minTimeCreated = Instant.fromEpochMilliseconds(4.days.inWholeMilliseconds)
        val baseTimeCreated = Instant.fromEpochMilliseconds(5.days.inWholeMilliseconds)
        val maxTimeCreated = Instant.fromEpochMilliseconds(6.days.inWholeMilliseconds)
        val minTimeForData = Instant.fromEpochMilliseconds(7.days.inWholeMilliseconds)
        val baseTimeForData = Instant.fromEpochMilliseconds(8.days.inWholeMilliseconds)
        val maxTimeForData = Instant.fromEpochMilliseconds(9.days.inWholeMilliseconds)

        val baseTick = Tick(
            amount = baseAmount,
            timeModified = baseTimeModified,
            timeCreated = baseTimeCreated,
            timeForData = baseTimeForData,
            parentId = 1L,
            id = 0L,
        )
        val baseTicks = (1L .. 10L).map {
            baseTick.copy(id = it)
        } + baseTick.copy(
            amount = minAmount,
            timeModified = minTimeModified,
            timeCreated = minTimeCreated,
            timeForData = minTimeForData,
            id = 11L,
        ) + baseTick.copy(
            amount = maxAmount,
            timeModified = maxTimeModified,
            timeCreated = maxTimeCreated,
            timeForData = maxTimeForData,
            id = 12L,
        )
        runBlocking {
            val result = repo.convertToGraphState(
                ticks = baseTicks,
                sort = TimeForData,
                domain = null,
                range = null,
            )

            assert(result.rangeBounds == result.currentRange) {
                "range bounds and current bounds should be equal! found:\nbounds: ${result.rangeBounds}\ncurrent: ${result.currentRange}"
            }
            assert(result.currentRange == minAmount .. maxAmount)
            assert(result.domainBounds == result.currentDomain)
            assert(result.currentDomain == minTimeForData .. maxTimeForData)
        }
        runBlocking {
            val result = repo.convertToGraphState(
                ticks = baseTicks.map { it.copy(amount = -1.00) },
                sort = TimeCreated,
                domain = null,
                range = null,
            )

            assert(result.rangeBounds == -1.00 .. -1.00)
            assert(result.currentRange == -1.00 .. 1.00)
            assert(result.domainBounds == result.currentDomain)
            assert(result.currentDomain == minTimeCreated .. maxTimeCreated)
        }
        runBlocking {
            val result = repo.convertToGraphState(
                ticks = baseTicks.map { it.copy(amount = 2.00) },
                sort = TimeModified,
                domain = null,
                range = null,
            )

            assert(result.rangeBounds == 2.00 .. 2.00)
            assert(result.currentRange == 0.00 .. 2.00)
            assert(result.domainBounds == result.currentDomain)
            assert(result.currentDomain == minTimeModified .. maxTimeModified)
        }
    }

    @Test
    fun `Convert to graph state always uses provided domain and range`() {
        runBlocking {
            val domain = Instant.fromEpochMilliseconds(3.days.inWholeMilliseconds).let {
                it .. (it + 1.days)
            }
            val baseTime = Instant.fromEpochMilliseconds(5.days.inWholeMilliseconds)
            val range = 3.00 .. 4.00
            val result = repo.convertToGraphState(
                ticks = listOf(
                    Tick(
                        amount = 5.00,
                        timeModified = baseTime,
                        timeCreated = baseTime,
                        timeForData = baseTime,
                        parentId = 1L,
                        id = 1L,
                    )
                ),
                sort = TimeModified,
                domain = domain,
                range = range,
            )

            assert(result.currentDomain == domain)
            assert(result.currentRange == range)
            assert(result.rangeBounds == 5.00 .. 5.00) {
                "range bounds does not match data:\nexpected: 5.00 .. 5.00\nfound: ${result.rangeBounds}"
            }
            assert(result.domainBounds == baseTime .. baseTime)
            assert(result.graphPoints == listOf(PointByPercent(x = 2.00, y = 2.00)))
        }
    }
}
