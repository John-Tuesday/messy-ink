package org.calamarfederal.messyink.feature_counter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.RowsChanged
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickEntity

class MockCounterDao : CounterDao {
    private val counters = mutableListOf<CounterEntity>()
    private val ticks = mutableMapOf<Long, MutableList<TickEntity>>()

    override suspend fun counterIds(): List<Long> = counters.map { it.id }

    override suspend fun tickIds(): List<Long> = ticks.values.flatMap { it.map { it.id } }

    override suspend fun counters(): List<CounterEntity> = counters.toList()

    override suspend fun counter(id: Long): CounterEntity? = counters.find { it.id == id }

    override suspend fun ticks(): List<TickEntity> = ticks.values.flatten()

    override suspend fun tick(id: Long): TickEntity? = ticks().find { it.id == id }

    override suspend fun ticksOf(parentId: Long): List<TickEntity> = ticks[parentId]?.toList() ?: listOf()

    override fun ticksByCounter(): Map<Long, List<TickEntity>> = ticks.mapValues { it.value.toList() }

    override fun counterFlow(id: Long): Flow<CounterEntity?> =
        flowOf(counters.find { it.id == id })

    override fun countersFlow(): Flow<List<CounterEntity>> = flowOf(counters)

    override fun ticksOfFlow(parentId: Long): Flow<List<TickEntity>> =
        flowOf(ticks[parentId] ?: listOf())

    override fun ticksByCounterFlow(): Flow<Map<Long, List<TickEntity>>> = flowOf(ticks)

    override suspend fun tickIdsByTimeModifiedWithLimit(
        parentId: Long,
        limit: Int,
        start: Instant,
        end: Instant,
    ): List<Long> = ticks[parentId]?.asSequence()?.run {
        take(limit).filter { it.timeModified in start .. end }.map { it.id }.toList()
    } ?: listOf()

    override suspend fun insertCounter(counter: CounterEntity) {
        counters.add(counter)
    }

    override suspend fun insertCounterTick(tick: TickEntity) {
        ticks.getOrPut(tick.parentId) { mutableListOf() }.add(tick)
    }

    override suspend fun updateCounter(counter: CounterEntity): RowsChanged {
        if (counters.removeAll { it.id == counter.id }) {
            counters.add(counter)
            return 1
        }
        return 0
    }

    override suspend fun updateTick(tick: TickEntity): RowsChanged {
        if (ticks[tick.parentId]?.removeAll { it.id == tick.id } ?: return 0) {
            ticks[tick.parentId]!!.add(tick)
            return 1
        }
        return 0
    }

    override suspend fun deleteCounter(id: Long) {
        counters.removeAll { it.id == id }
    }

    override suspend fun deleteTick(id: Long) {
        ticks.values.onEach { it.removeAll { it.id == id } }
    }

    override suspend fun deleteCounters(ids: List<Long>) {
        counters.removeAll { it.id in ids }
    }

    override suspend fun deleteTicks(ids: List<Long>) {
        ticks.values.onEach { it.removeAll { it.id in ids } }
    }

    override suspend fun deleteTicksOf(parentId: Long) {
        ticks[parentId]?.clear()
    }

    override suspend fun deleteTicksByTimeForData(parentId: Long, start: Instant, end: Instant) {
        ticks[parentId]?.removeAll { it.timeForData in start .. end }
    }

    override suspend fun deleteTicksByTimeModified(parentId: Long, start: Instant, end: Instant) {
        ticks[parentId]?.removeAll { it.timeModified in start .. end }
    }

    override suspend fun ticksSumBy(): Map<Long, Double> =
        ticks.mapValues { it.value.sumOf { it.amount } }

    override suspend fun ticksSumOf(parentId: Long, start: Instant, end: Instant): Double =
        ticks[parentId]?.asSequence()
            ?.run { filter { it.timeForData in start .. end }.sumOf { it.amount } } ?: 0.00

    override suspend fun ticksAverageOf(parentId: Long, start: Instant, end: Instant): Double =
        ticks[parentId]?.asSequence()
            ?.filter { it.timeForData in start .. end }?.run {
                sumOf { it.amount } / count()
            } ?: 0.00

    override fun ticksSumByFlow(): Flow<Map<Long, Double>> = flowOf(runBlocking { ticksSumBy() })

    override fun ticksSumOfFlow(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        flowOf(runBlocking { ticksSumOf(parentId, start, end) })

    override fun ticksAverageOfFlow(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        flowOf(runBlocking { ticksAverageOf(parentId, start, end) })
}
