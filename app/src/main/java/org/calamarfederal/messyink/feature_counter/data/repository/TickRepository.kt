package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort

interface TickRepository {
    /**
     * Watch all ticks which have parent_id = [parentId]
     *
     * @param[parentId] should be a valid Counter.id
     *
     * @return Flow emits empty list if no such tick can be found
     */
    fun getTicksFlow(parentId: Long, sort: TickSort): Flow<List<Tick>>

    /**
     * Get and watch tick by [id]
     */
    fun getTickFlow(id: Long): Flow<Tick?>

    /**
     * Create new [Tick] using [Tick.amount], [Tick.parentId], and [Tick.timeForData] from [tick].
     * sets new [Tick.timeCreated] and [Tick.timeModified]
     */
    suspend fun createTick(tick: Tick): Tick

    /**
     * Update old [Tick] to [tick]
     *
     * if old [tick] cannot be found; then this does nothing
     */
    suspend fun updateTick(tick: Tick): Boolean

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
        timeType: TickSort,
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
        timeType: TickSort,
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
