package org.calamarfederal.messyink.feature_counter.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEmpty
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.feature_counter.domain.Counter
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.Tick
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import kotlin.random.Random

private fun generateId(pool: Set<Long>, nextRand: () -> Long = { Random.nextLong() }): Long {
    var newId = nextRand()
    while (pool.contains(newId) || newId == NOID) newId = nextRand()
    return newId
}

@OptIn(ExperimentalCoroutinesApi::class)
class CountersRepoImpl(private val dao: CounterDao) : CountersRepo {
    private suspend fun getCounterIds(): List<Long> = dao.counterIds()
    private suspend fun getTickIds(): List<Long> = dao.tickIds()

    override suspend fun getCounters(): List<Counter> = dao.counters().map { it.toCounter() }
    override suspend fun getTicks(parentId: Long): List<Tick> =
        dao.ticksOf(parentId).map { it.toTick() }

    override fun getCounterFlow(id: Long): Flow<Counter?> =
        dao.counterFlow(id).distinctUntilChanged().mapLatest { it?.toCounter() }

    override fun getCountersFlow(): Flow<List<Counter>> =
        dao.countersFlow().distinctUntilChanged().map { data -> data.map { it.toCounter() } }

    override fun getTicksFlow(parentId: Long): Flow<List<Tick>> =
        dao.ticksOfFlow(parentId).distinctUntilChanged().map { data -> data.map { it.toTick() } }


    override suspend fun createCounterFrom(counter: Counter): Counter =
        counter.copy(id = generateId(pool = getCounterIds().toSet()))
            .also { dao.insertCounter(it.toEntity()) }

    override suspend fun createTickFrom(tick: Tick): Tick {
        require(tick.parentId != NOID)
        return tick.copy(id = generateId(pool = getTickIds().toSet()))
            .also { dao.insertCounterTick(it.toEntity()) }
    }

    override suspend fun updateCounter(counter: Counter) = dao.updateCounter(counter.toEntity())

    override suspend fun updateTick(tick: Tick) = dao.updateTick(tick.toEntity())

    override suspend fun deleteCounter(counter: Counter) = dao.deleteCounter(counter.id)
    override suspend fun deleteTick(tick: Tick) = dao.deleteTick(tick.id)

    /**
     * # Summary & Calculation
     */
    override fun getTicksSumOfFlow(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        dao.ticksSumOfFlow(parentId = parentId, start = start, end = end)

    override fun getTicksAverageOfFlow(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        dao.ticksAverageOfFlow(parentId = parentId, start = start, end = end)

    override fun getTicksSumByFlow(): Flow<Map<Long, Double>> = dao.ticksSumByFlow()
}
