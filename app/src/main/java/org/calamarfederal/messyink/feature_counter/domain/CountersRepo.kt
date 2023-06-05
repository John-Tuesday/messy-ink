package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * # Wrapper for managing counters, ticks, and their common commands
 */
interface CountersRepo {
    /**
     * One-shot get all saved counters ordered by DAO logic
     */
    suspend fun getCounters(): List<Counter>

    /**
     * Gets the counter if it exists, null otherwise
     */
    suspend fun getCounterOrNull(id: Long): Counter?

    /**
     * One-shot get all ticks with parent_id
     *
     * @param[parentId] should be a valid Counter.id
     */
    suspend fun getTicks(parentId: Long): List<Tick>

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
    fun getCountersFlow(): Flow<List<Counter>>

    /**
     * Watch all ticks which have parent_id = [parentId]
     *
     * @param[parentId] should be a valid Counter.id
     *
     * @return Flow emits empty list if no such tick can be found
     */
    fun getTicksFlow(parentId: Long): Flow<List<Tick>>

    /**
     * Copy [counter]'s values, but save with a new id, and set its timeModified value
     *
     * @param[counter] counter which will be used as the basis for a new counter
     */
    suspend fun duplicateCounter(counter: Counter): Counter

    /**
     * Copy [tick]'s values, but save with a new id, and set its timeModified value
     *
     * @param[tick] tick which will be used as the basis for a new tick
     */
    suspend fun duplicateTick(tick: Tick): Tick

    /**
     * Update old [Counter] to [counter] and update [Counter.timeModified]
     *
     * if old [counter] cannot be found; then this does nothing
     */
    suspend fun updateCounter(counter: Counter): Boolean

    /**
     * Update old [Tick] to [tick] and set [Tick.timeModified]
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
     * Delete all [Tick] with matching [Tick.parentId] from [[start], [end]]
     */
    suspend fun deleteTicksByTimeForData(
        parentId: Long,
        start: Instant = Instant.DISTANT_PAST,
        end: Instant = Instant.DISTANT_FUTURE
    )

    /**
     * delete up to [limit], or all if [limit] is null, [Tick] with [Tick.timeModified] in bounds [[start], [end]]
     */
    suspend fun deleteTicksByTimeModified(
        parentId: Long,
        limit: Int? = null,
        start: Instant = Instant.DISTANT_PAST,
        end: Instant = Instant.DISTANT_FUTURE
    )

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
