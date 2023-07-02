package org.calamarfederal.messyink.feature_counter.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantFuture
import kotlinx.datetime.isDistantPast
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.Counter
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.Tick
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.calamarfederal.messyink.feature_counter.domain.TickSort.TimeType
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import javax.inject.Inject
import kotlin.random.Random

/**
 * Generate new Id (randomly), not in Pool
 *
 * note: this is a temporary implementation so I don't rely on iterative ids
 */
private fun generateId(pool: Set<Long>, nextRand: () -> Long = { Random.nextLong() }): Long {
    var newId = nextRand()
    while (pool.contains(newId) || newId == NOID) newId = nextRand()
    return newId
}

/**
 * Implement CountersRepo using CounterDao
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CountersRepoImpl @Inject constructor(
    private val dao: CounterTickDao,
    @CurrentTime private val getCurrentTime: GetTime,
) : CountersRepo {
    private suspend fun getCounterIds(): List<Long> = dao.counterIds()
    private suspend fun getTickIds(): List<Long> = dao.tickIds()

    private suspend fun _createNewTick(
        amount: Double,
        parentId: Long,
        timeForData: Instant? = null,
    ): Tick {
        require(parentId != NOID)

        val time = getCurrentTime()
        return Tick(
            amount = amount,
            timeCreated = time,
            timeModified = time,
            timeForData = timeForData ?: time,
            parentId = parentId,
            id = generateId(getTickIds().toSet())
        )
    }

    override suspend fun getCounters(sort: CounterSort.TimeType): List<Counter> =
        dao.counters(sort.toCounterTimeColumn()).map { it.toCounter() }

    override suspend fun getCounterOrNull(id: Long): Counter? = dao.counter(id)?.toCounter()
    override suspend fun getTicks(parentId: Long, sort: TickSort.TimeType): List<Tick> =
        dao.ticksWithParentId(parentId, sort.toTickTimeType()).map { it.toTick() }

    override fun getCounterFlow(id: Long): Flow<Counter?> =
        dao.counterFlow(id).distinctUntilChanged().mapLatest { it?.toCounter() }

    override fun getCountersFlow(sort: CounterSort.TimeType): Flow<List<Counter>> =
        dao.countersFlow(sort.toCounterTimeColumn()).distinctUntilChanged()
            .map { data -> data.map { it.toCounter() } }

    override fun getTicksFlow(parentId: Long, sort: TickSort.TimeType): Flow<List<Tick>> =
        dao.ticksWithParentIdFlow(parentId, sort.toTickTimeType())
            .distinctUntilChanged()
            .map { data -> data.map { it.toTick() } }


    override suspend fun duplicateCounter(counter: Counter): Counter {
        val time = getCurrentTime()
        return counter.copy(
            timeCreated = time,
            timeModified = time,
            id = generateId(pool = getCounterIds().toSet()),
        ).also { dao.insertCounter(it.toEntity()) }
    }

    override suspend fun duplicateTick(tick: Tick): Tick {
        require(tick.parentId != NOID)

        val time = getCurrentTime()
        return tick.copy(
            timeCreated = time,
            timeModified = time,
            timeForData = tick.timeForData.run { if (isDistantFuture || isDistantPast) time else this },
            id = generateId(pool = getTickIds().toSet()),
        ).also { dao.insertTick(it.toEntity()) }
    }

    override suspend fun createTick(tick: Tick): Tick {
        require(tick.parentId != NOID) { "Cannot create Tick without a valid Parent ID" }

        val outTick = _createNewTick(tick.amount, parentId = tick.parentId)
        dao.insertTick(outTick.toEntity())
        return outTick
    }

    override suspend fun updateCounter(counter: Counter) =
        0 < dao.updateCounter(counter.copy(timeModified = getCurrentTime()).toEntity())

    override suspend fun updateTick(tick: Tick) =
        0 < dao.updateTick(tick.copy(timeModified = getCurrentTime()).toEntity())

    override suspend fun deleteCounter(id: Long) = dao.deleteCounter(id)
    override suspend fun deleteTick(id: Long) = dao.deleteTick(id)
    override suspend fun deleteTicks(ids: List<Long>) = dao.deleteTicks(ids)
    override suspend fun deleteTicksOf(parentId: Long) = dao.deleteTickWithParentId(parentId)

    override suspend fun deleteTicksBySelection(
        parentId: Long,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ) =
        dao.deleteTicksBySelector(
            parentId = parentId,
            limit = null,
            sortColumn = timeType.toTickTimeType(),
            start = start,
            end = end
        )

    override suspend fun deleteTicksBySelection(
        parentId: Long,
        limit: Int?,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ) {
        dao.deleteTicksBySelector(
            parentId = parentId,
            limit = limit,
            sortColumn = timeType.toTickTimeType(),
            start = start,
            end = end
        )
    }

    /**
     * # Summary & Calculation
     */
    override fun getTicksSumOfFlow(
        parentId: Long,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> =
        dao.tickSumWithParentIdBySelectorFlow(
            parentId = parentId,
            selector = timeType.toTickTimeType(),
            start = start,
            end = end
        )

    override fun getTicksAverageOfFlow(
        parentId: Long,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> =
        dao.tickAverageWithParentIdBySelectorFlow(
            parentId = parentId,
            selector = timeType.toTickTimeType(),
            start = start,
            end = end
        )

    override fun getTicksSumByFlow(): Flow<Map<Long, Double>> = dao.tickSumByParentIdFlow()
}
