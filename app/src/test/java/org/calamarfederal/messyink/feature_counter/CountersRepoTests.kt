package org.calamarfederal.messyink.feature_counter

import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickEntity
import org.calamarfederal.messyink.feature_counter.data.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.data.toCounter
import org.calamarfederal.messyink.feature_counter.data.toTick
import org.calamarfederal.messyink.feature_counter.domain.Counter
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.Tick
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

private infix fun CounterEntity.alike(counter: Counter): Boolean =
    id == counter.id && name == counter.name && timeCreated == counter.timeCreated && timeModified == counter.timeModified

private infix fun Counter.alike(counter: CounterEntity): Boolean = counter.alike(this)

private infix fun TickEntity.alike(tick: Tick): Boolean =
    id == tick.id
            && parentId == tick.parentId
            && amount == tick.amount
            && timeCreated.epochSeconds == tick.timeCreated.epochSeconds
            && timeModified.epochSeconds == tick.timeModified.epochSeconds
            && timeForData.epochSeconds == tick.timeForData.epochSeconds

private infix fun Tick.alike(tick: TickEntity): Boolean = tick.alike(this)

//assert(id == counter.id && name == counter.name && timeCreated == counter.timeCreated && timeModified == counter.timeModified)

/**
 * # Test for [CountersRepoImpl] as [CountersRepo]
 */
class CountersRepoTests {
    private lateinit var repo: CountersRepo
    private lateinit var dao: CounterDao
    private lateinit var getTimeConst: GetTime

    private var invalidCounterID: Long = 0L
    private var invalidTickID: Long = 0L

    private var unusedCounterID: Long = 0L
    private var unusedTickID: Long = 0L

    private lateinit var recentPast: Instant

    @Before
    fun setUp() {
        dao = MockCounterDao()

        var time = LocalDateTime(
            year = 2023,
            monthNumber = 6,
            dayOfMonth = 15,
            hour = 7,
            minute = 7
        ).toInstant(TimeZone.UTC)

        invalidCounterID = -1L
        invalidTickID = -2L
        unusedCounterID = -3L
        unusedTickID = -4L

        var idToAdd = 0L

        for (counterCount in 0 until 10) {
            time += 1.days
            idToAdd += 1L

            val counterId = idToAdd

            runBlocking {
                dao.insertCounter(
                    CounterEntity(
                        id = counterId,
                        name = "name name $counterCount",
                        timeModified = time,
                        timeCreated = time,
                    )
                )
            }

            for (tickCount in 0 until 10) {
                idToAdd += 1
                val tickId = idToAdd
                runBlocking {
                    dao.insertCounterTick(
                        TickEntity(
                            id = tickId,
                            parentId = counterId,
                            amount = tickCount * 3.00 + 11.00,
                            timeCreated = time,
                            timeModified = time,
                            timeForData = time,
                        )
                    )
                }
            }
        }

        recentPast = LocalDateTime(
            year = 2023,
            monthNumber = 5,
            dayOfMonth = 7,
            hour = 8,
            minute = 13,
        ).toInstant(TimeZone.UTC)

        getTimeConst = GetTime { time }

        repo = CountersRepoImpl(dao, getTimeConst)
    }

    @Test
    fun `Get all counters all ways, with valid and invalid ids, with (non)zero data`(): Unit =
        runBlocking {
            val expectedCounters = dao.counters()
            val actualCounters = repo.getCounters()

            assert(actualCounters == repo.getCountersFlow().single())
            assert(expectedCounters.size == actualCounters.size)

            actualCounters.onEach { actual ->
                val expect = dao.counter(actual.id)!!
                assert(expect alike actual)
                assert(expect alike repo.getCounterFlow(actual.id).single()!!)
                assert(expect alike repo.getCounterOrNull(actual.id)!!)
            }
        }

    @Test
    fun `Get all ticks all ways, with (in)valid ids`() = runBlocking {
        for (parentId in dao.counterIds()) {
            val actualTicks = repo.getTicks(parentId)

            assert(dao.ticksOf(parentId).size == actualTicks.size)
            assert(actualTicks == repo.getTicksFlow(parentId).single())
            actualTicks.forEach {
                val expect = dao.tick(it.id)!!
                assert(expect alike it)
            }
        }

        assert(repo.getTicks(invalidTickID).isEmpty())
        assert(repo.getTicksFlow(invalidTickID).single().isEmpty())
    }

    @Test
    fun `Duplicate counter creates a new time with a new id and updated timeModified`(): Unit =
        runBlocking {
            val daoCountersBefore = dao.counters()
            val counterBase = daoCountersBefore.first()

            val returnedCounter = repo.duplicateCounter(counterBase.toCounter())
            assert(returnedCounter.id != counterBase.id)
            val counterActual = dao.counters().single { !daoCountersBefore.contains(it) }
            assert(counterActual alike returnedCounter)
            assert(counterBase.id != counterActual.id)
            assert(dao.counter(returnedCounter.id) == counterActual)
            assert(
                counterBase.copy(
                    id = counterActual.id,
                    timeModified = getTimeConst(),
                    timeCreated = getTimeConst(),
                ) == counterActual
            )
        }

    @Test
    fun `Duplicate tick create new tick with new id and updated time modified`(): Unit =
        runBlocking {
            val daoTicksBefore = dao.ticks()
            val daoBeforeTick = daoTicksBefore.first()

            val returnTick = repo.duplicateTick(daoBeforeTick.toTick())
            val daoActualTick = dao.ticks().single { !daoTicksBefore.contains(it) }
            assert(daoActualTick alike returnTick)
            assert(
                daoBeforeTick.copy(
                    timeModified = getTimeConst(),
                    timeCreated = getTimeConst(),
                    id = daoActualTick.id
                ) == daoActualTick
            )
        }

    @Test
    fun `Update counter`(): Unit = runBlocking {
        val daoCountersBefore = dao.counters()
        val counterBase = daoCountersBefore.first()
        val counterDNE = counterBase.copy(
            name = "DNE: ${counterBase.name}",
            timeModified = counterBase.timeModified + 1.days,
            timeCreated = counterBase.timeCreated + 1.days,
            id = invalidCounterID,
        )
        val counterUpdated = counterDNE.copy(id = counterBase.id)

        assert(!repo.updateCounter(counterDNE.toCounter()) && daoCountersBefore == dao.counters())
        assert(
            repo.updateCounter(counterUpdated.toCounter()) && counterUpdated.copy(timeModified = getTimeConst()) == dao.counter(
                counterBase.id
            )
        )
    }

    @Test
    fun `Update tick updates with the correct info and updates timeModified or does nothing if it cannot update`(): Unit =
        runBlocking {
            val daoTicksBefore = dao.ticks()
            val tickBase = daoTicksBefore.first()
            val tickDNE = tickBase.copy(
                amount = tickBase.amount + 1,
                timeModified = tickBase.timeModified + 1.days,
                timeCreated = tickBase.timeCreated + 1.days,
                timeForData = tickBase.timeForData + 1.days,
                parentId = invalidCounterID,
                id = invalidTickID,
            )
            val tickUpdate = tickDNE.copy(id = tickBase.id, parentId = tickBase.parentId)

            assert(repo.updateTick(tickUpdate.toTick()))
            assert(dao.tick(tickBase.id) == tickUpdate.copy(timeModified = getTimeConst()))
        }

    @Test
    fun `Delete counter`(): Unit = runBlocking {
        val daoCountersBefore = dao.counters()
        val deletedCounter = daoCountersBefore.first()
        val daoCounterExpect = daoCountersBefore.filterNot { it == deletedCounter }

        repo.deleteCounter(deletedCounter.id)
        assert(dao.counters() == daoCounterExpect)
    }

    @Test
    fun `Delete tick by id, correct`(): Unit = runBlocking {
        val daoTicksBefore = dao.ticks()
        val deleteId = daoTicksBefore.first().id
        val daoTicksExpect = daoTicksBefore.filterNot { it.id == deleteId }

        repo.deleteTick(invalidTickID)
        assert(daoTicksBefore == dao.ticks())

        repo.deleteTick(deleteId)

        assert(daoTicksExpect == dao.ticks())
    }

    @Test
    fun `Delete ticks by id list, correct`(): Unit = runBlocking {
        val daoTicksBefore = dao.ticks()
        val deleteIds = daoTicksBefore.take(5).map { it.id }
        val daoTicksExpect = daoTicksBefore.filterNot { it.id in deleteIds }

        repo.deleteTicks(listOf(invalidTickID))
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicks(listOf())
        assert(daoTicksBefore == dao.ticks())

        repo.deleteTicks(deleteIds)

        assert(daoTicksExpect == dao.ticks())
    }

    @Test
    fun `Delete ticks by parent id, correct`(): Unit = runBlocking {
        val daoTicksBefore = dao.ticks()
        val deleteId = daoTicksBefore.first().parentId
        val daoTicksExpect = daoTicksBefore.filterNot { it.parentId == deleteId }

        repo.deleteTicksOf(invalidCounterID)
        assert(daoTicksBefore == dao.ticks())

        repo.deleteTicksOf(deleteId)

        assert(daoTicksExpect == dao.ticks())
    }

    @Test
    fun `Delete ticks by parent id and time for data, correct`(): Unit = runBlocking {
        val daoTicksBefore = dao.ticks()
        val deleteParentId = daoTicksBefore.first().parentId
        val deleteRange = daoTicksBefore
            .filter { it.parentId == deleteParentId }
            .take(3)
            .let { it.first().timeForData .. it.last().timeForData }
        val daoTicksExpect = daoTicksBefore.filterNot {
            it.parentId == deleteParentId && it.timeForData in deleteRange
        }

        repo.deleteTicksByTimeForData(invalidCounterID, deleteRange.start, deleteRange.endInclusive)
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeForData(invalidCounterID, deleteRange.endInclusive, deleteRange.start)
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeForData(deleteParentId, Instant.DISTANT_PAST, Instant.DISTANT_PAST)
        assert(daoTicksBefore == dao.ticks())
        val epoch = Instant.fromEpochSeconds(0L)
        repo.deleteTicksByTimeForData(deleteParentId, epoch, epoch)
        assert(daoTicksBefore == dao.ticks())

        repo.deleteTicksByTimeForData(deleteParentId, deleteRange.start, deleteRange.endInclusive)
        assert(daoTicksExpect == dao.ticks())
    }

    @Test
    fun `Delete ticks by parent id and time modified, no limit correct`(): Unit = runBlocking {
        val daoTicksBefore = dao.ticks()
        val deleteParentId = daoTicksBefore.first().parentId
        val deleteRange = daoTicksBefore
            .filter { it.parentId == deleteParentId }
            .take(3)
            .let { it.first().timeModified .. it.last().timeModified }
        val daoTicksExpect = daoTicksBefore.filterNot {
            it.parentId == deleteParentId && it.timeModified in deleteRange
        }

        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = null,
            start = deleteRange.start,
            end = deleteRange.endInclusive
        )
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = null,
            start = deleteRange.endInclusive,
            end = deleteRange.start
        )
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeModified(
            parentId = deleteParentId,
            limit = null,
            start = Instant.DISTANT_PAST,
            end = Instant.DISTANT_PAST,
        )
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = null,
            start = Instant.DISTANT_PAST,
            end = Instant.DISTANT_FUTURE,
        )
        assert(daoTicksBefore == dao.ticks())
        val epoch = Instant.fromEpochSeconds(0L)
        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = null,
            start = epoch,
            end = epoch,
        )
        assert(daoTicksBefore == dao.ticks())

        repo.deleteTicksByTimeModified(
            parentId = deleteParentId,
            limit = null,
            start = deleteRange.start,
            end = deleteRange.endInclusive,
        )
        assert(daoTicksExpect == dao.ticks())
    }

    @Test
    fun `Delete ticks by parent id and time modified, with limit correct`(): Unit = runBlocking {
        val daoTicksBefore = dao.ticks()
        val limit = 4
        val deleteParentId = daoTicksBefore.first().parentId
        val deleteRange = daoTicksBefore
            .filter { it.parentId == deleteParentId }
            .take(3)
            .let { it.first().timeModified .. it.last().timeModified }
        val daoTicksExpect = daoTicksBefore.partition {
            it.parentId == deleteParentId && it.timeModified in deleteRange
        }.run { first.drop(limit) + second }

        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = limit,
            start = deleteRange.start,
            end = deleteRange.endInclusive
        )
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = limit,
            start = deleteRange.endInclusive,
            end = deleteRange.start
        )
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = limit,
            start = Instant.DISTANT_PAST,
            end = Instant.DISTANT_PAST,
        )
        assert(daoTicksBefore == dao.ticks())
        repo.deleteTicksByTimeModified(
            parentId = invalidCounterID,
            limit = limit,
            start = Instant.DISTANT_PAST,
            end = Instant.DISTANT_FUTURE,
        )
        assert(daoTicksBefore == dao.ticks())
        val epoch = Instant.fromEpochSeconds(0L)
        repo.deleteTicksByTimeModified(
            parentId = deleteParentId,
            limit = limit,
            start = epoch,
            end = epoch,
        )
        assert(daoTicksBefore == dao.ticks())

        repo.deleteTicksByTimeModified(
            parentId = deleteParentId,
            limit = limit,
            start = deleteRange.start,
            end = deleteRange.endInclusive,
        )
        assert(daoTicksExpect == dao.ticks())
    }

    @Test
    fun `Get tick sum`() = runBlocking {
        val daoTicks = dao.ticks()
        val parentId = daoTicks.first().parentId
        val range = daoTicks.take(4).run { first().timeForData .. last().timeForData }
        val expectSum = daoTicks.filter { it.parentId == parentId && it.timeForData in range }
            .sumOf { it.amount }

        assert(
//            repo.getTicksSumOfFlow(parentId, range.start, range.endInclusive).single() == expectSum
            repo.getTicksSumOfFlow(
                parentId, range.start, range.endInclusive
            ).single() == dao.ticksSumOf(parentId, range.start, range.endInclusive)
        )

        assert(repo.getTicksSumByFlow().single() == dao.ticksSumBy())
    }
}
