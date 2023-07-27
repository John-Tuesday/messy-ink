package org.calamarfederal.messyink.feature_counter.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.source.database.TickEntity
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class CountersRepoImplTest {
    private lateinit var repo: CountersRepoImpl
    private lateinit var dao: CounterTickDao
    private lateinit var getTime: GetTime
    private lateinit var tickTable: List<TickEntity>
    private lateinit var counterTable: List<CounterEntity>
    private val parentId = 1L

    fun assertSameOrder(expected: List<TickEntity>, actual: List<Tick>) {
        assert(expected.size == actual.size) {
            "Difference found: Expected size = ${expected.size} Found size = ${actual.size}"
        }

        expected.forEachIndexed { index, tickEntity ->
            assert(actual[index].id == tickEntity.id) {
                "Difference found: (index = $index)\nExpected: ${tickEntity.id} Found: ${actual[index].id}"
            }
        }
    }

    @Before
    fun setUp() {
        getTime = GetTime { Instant.fromEpochMilliseconds(0L) }
        val seedTime: Instant = Instant.fromEpochMilliseconds(1688435708911L)
        tickTable = (1 .. 50).map {
            val time = seedTime + (13 * it).days
            TickEntity(
                amount = 1.00,
                timeCreated = time + (it % 9).days,
                timeModified = time + (it % 7).days,
                timeForData = time + (it % 11).days,
                parentId = parentId,
                id = it.toLong(),
            ).also {
                println("$it")
            }
        }
        counterTable = listOf()

        dao = MockCounterTickDao(tickTable = tickTable.shuffled(), counterTable = counterTable)
        repo = CountersRepoImpl(
            dao = dao,
            getCurrentTime = getTime,
        )
    }

    @Test
    fun `GetTicksFlow returns correct order TimeModified`() {
        val flow = repo.getTicksFlow(
            parentId = parentId,
            sort = TickSort.TimeType.TimeModified,
        )
        val actualTicks = runBlocking { flow.first() }

        assertSameOrder(expected = tickTable, actual = actualTicks)
    }

    @Test
    fun `GetTicksFlow returns correct order TimeForData`() {
        val flow = repo.getTicksFlow(
            parentId = parentId,
            sort = TickSort.TimeType.TimeForData,
        )
        val actualTicks = runBlocking { flow.first() }

        assertSameOrder(expected = tickTable, actual = actualTicks)
    }

    @Test
    fun `GetTicksFlow returns correct order TimeCreated`() {
        val flow = repo.getTicksFlow(
            parentId = parentId,
            sort = TickSort.TimeType.TimeCreated,
        )
        val actualTicks = runBlocking { flow.first() }

        assertSameOrder(expected = tickTable, actual = actualTicks)
    }
}
