package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface CountersRepo {
    suspend fun getCounters(): List<Counter>
    suspend fun getTicks(parentId: Long): List<Tick>

    fun getCounterFlow(id: Long): Flow<Counter?>
    fun getCountersFlow(): Flow<List<Counter>>
    fun getTicksFlow(parentId: Long): Flow<List<Tick>>

    suspend fun createCounterFrom(counter: Counter): Counter
    suspend fun createTickFrom(tick: Tick): Tick

    suspend fun updateCounter(counter: Counter)
    suspend fun updateTick(tick: Tick)

    suspend fun deleteCounter(counter: Counter)
    suspend fun deleteTick(tick: Tick)

    /**
     * # Summary & Calculation
     */
    fun getTicksSumOfFlow(
        parentId: Long,
        start: Instant = Instant.DISTANT_PAST,
        end: Instant = Instant.DISTANT_FUTURE,
    ): Flow<Double>

    fun getTicksAverageOfFlow(
        parentId: Long,
        start: Instant = Instant.DISTANT_PAST,
        end: Instant = Instant.DISTANT_FUTURE,
    ): Flow<Double>

    fun getTicksSumByFlow(): Flow<Map<Long, Double>>
}
