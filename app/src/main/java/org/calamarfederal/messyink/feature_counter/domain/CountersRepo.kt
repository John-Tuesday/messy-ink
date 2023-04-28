package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion

/**
 * # Wrapper for managing counters, ticks, and their common commands
 */
interface CountersRepo {
    /**
     * One-shot get all saved counters ordered by DAO logic
     */
    suspend fun getCounters(): List<Counter>

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
    suspend fun createCounterFrom(counter: Counter): Counter

    /**
     * Copy [tick]'s values, but save with a new id, and set its timeModified value
     *
     * @param[tick] tick which will be used as the basis for a new tick
     */
    suspend fun createTickFrom(tick: Tick): Tick

    /**
     * Find saved Counter with matching id; if found, update saved version to match [counter]
     *
     * this method should preserve timeCreated, i think
     *
     * @param[counter] counter whose content and id will be used to update the saved version
     */
    suspend fun updateCounter(counter: Counter)

    /**
     * Find saved Tick with matching id; if found, update saved version to match [tick]
     *
     * this method should preserve timeCreated, i think
     *
     * @param[tick] tick whose content and id will be used to update the saved version
     */
    suspend fun updateTick(tick: Tick)

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
     * Delete all [Tick] with matching [Tick.parentId] from [[start], [end]]
     */
    suspend fun deleteTicksFrom(parentId: Long, start: Instant = Instant.DISTANT_PAST, end: Instant = Instant.DISTANT_FUTURE)

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
