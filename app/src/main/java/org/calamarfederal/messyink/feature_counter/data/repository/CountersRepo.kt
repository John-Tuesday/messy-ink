package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.calamarfederal.messyink.feature_counter.domain.TickSort.TimeType

/**
 * # Wrapper for managing counters, ticks, and their common commands
 */
interface CountersRepo {
    /**
     * Watch changes to Counter with [id]
     *
     * @param[id] Counter.id
     * @return Flow emits null when Counter with [id] does not exist
     */
    fun getCounterFlow(id: Long): Flow<Counter?>

    /**
     * Watch all counters
     *
     * @return Flow emits empty list when no Counter exists
     */
    fun getCountersFlow(sort: CounterSort.TimeType): Flow<List<Counter>>

    /**
     * Watch all ticks which have parent_id = [parentId]
     *
     * @param[parentId] should be a valid Counter.id
     *
     * @return Flow emits empty list if no such tick can be found
     */
    fun getTicksFlow(parentId: Long, sort: TickSort.TimeType): Flow<List<Tick>>

    /**
     * Get and watch tick by [id]
     */
    fun getTickFlow(id: Long): Flow<Tick?>

    /**
     * Copy [counter]'s values, but save with a new id, and set its timeModified, and timeCreated values. returns the new [Counter]
     *
     * @param[counter] counter which will be used as the basis for a new counter
     */
    suspend fun createCounter(counter: Counter): Counter

    /**
     * Create new [Tick] using [Tick.amount], [Tick.parentId], and [Tick.timeForData] from [tick].
     * sets new [Tick.timeCreated] and [Tick.timeModified]
     */
    suspend fun createTick(tick: Tick): Tick

    /**
     * Update old [Counter] to [counter]
     *
     * if old [counter] cannot be found; then this does nothing
     */
    suspend fun updateCounter(counter: Counter): Boolean

    /**
     * Update old [Tick] to [tick]
     *
     * if old [tick] cannot be found; then this does nothing
     */
    suspend fun updateTick(tick: Tick): Boolean

    /**
     * Find and delete any [Counter] with matching [Counter.id]
     */
    suspend fun deleteCounter(id: Long)

    /**
     * Find and delete any [Tick] with matching [Tick.id]
     */
    suspend fun deleteTick(id: Long)

    /**
     * Find and delete all [Tick] with matching [Tick.id]
     */
    suspend fun deleteTicks(ids: List<Long>)

    /**
     * Delete all [Tick] with [parentId]
     */
    suspend fun deleteTicksOf(parentId: Long)

    /**
     * # Summary & Calculation
     */

    /**
     * One-shot, Get the sum of all ticks with [parentId] and in [ [start], [end] ]
     *
     * @param[parentId] parent_id of the ticks; should be a valid Counter.id
     * @param[start] inclusive, matched against tick.timeForData
     * @param[end] inclusive, matched against tick.timeForData
     */
    fun getTicksSumOfFlow(
        parentId: Long,
        timeType: TimeType,
        start: Instant = Instant.DISTANT_PAST,
        end: Instant = Instant.DISTANT_FUTURE,
    ): Flow<Double>

    /**
     * One-shot, Get the average of all ticks with [parentId] and in [ [start], [end] ]
     *
     * @param[parentId] parent_id of the ticks; should be a valid Counter.id
     * @param[start] inclusive, matched against tick.timeForData
     * @param[end] inclusive, matched against tick.timeForData
     */
    fun getTicksAverageOfFlow(
        parentId: Long,
        timeType: TimeType,
        start: Instant = Instant.DISTANT_PAST,
        end: Instant = Instant.DISTANT_FUTURE,
    ): Flow<Double>

    /**
     * Watch the sum of all ticks grouped by their parent_id
     *
     * @return Flow emits empty map when no ticks exist
     */
    fun getTicksSumByFlow(): Flow<Map<Long, Double>>
}
