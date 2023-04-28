package org.calamarfederal.messyink.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickEntity

/**
 * # DAO for Local Counter and Tick data
 */
@Dao
interface CounterDao {

    /**
     * ## IDs
     *
     */

    /**
     * [List] of every [CounterEntity.id]
     */
    @Query("SELECT id FROM counters")
    suspend fun counterIds(): List<Long>

    /**
     * [List] of every [TickEntity.id]
     */
    @Query("SELECT id FROM counter_ticks")
    suspend fun tickIds(): List<Long>

    /**
     * # Get Objects
     * ## One-shot
     *
     */

    /**
     * All [CounterEntity] ordered by [CounterEntity.timeModified]
     */
    @Query("SELECT * FROM counters ORDER BY time_modified")
    suspend fun counters(): List<CounterEntity>

    /**
     * All [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData]
     */
    @Query("SELECT * FROM counter_ticks WHERE parent_id = :parentId ORDER BY time_for_data")
    suspend fun ticksOf(parentId: Long): List<TickEntity>

    /**
     * [Map] of [TickEntity.parentId] to a [List] of all matching [TickEntity]
     *
     * key is [TickEntity.parentId]
     * value is [List] of all [TickEntity] with matching [TickEntity.parentId]
     */
    @MapInfo(keyColumn = "parent_id")
    @Query("SELECT * FROM counter_ticks GROUP BY parent_id")
    fun ticksByCounter(): Map<Long, List<TickEntity>>

    /**
     * ## Flow
     *
     */

    /**
     * @return emits [CounterEntity] with matching [id] or null when none exists
     */
    @Query("SELECT * FROM counters WHERE id = :id")
    fun counterFlow(id: Long): Flow<CounterEntity?>

    /**
     * @return emits [List] of all [CounterEntity] ordered by [CounterEntity.timeModified] or empty [List]
     */
    @Query("SELECT * FROM counters ORDER BY time_modified")
    fun countersFlow(): Flow<List<CounterEntity>>

    /**
     * @return emits [List] of every [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData] or empty [List]
     */
    @Query("SELECT * FROM counter_ticks WHERE parent_id = :parentId ORDER BY time_for_data")
    fun ticksOfFlow(parentId: Long): Flow<List<TickEntity>>

    /**
     * Groups all [TickEntity] by [TickEntity.parentId] and returns the resulting [Map]
     *
     * key is [TickEntity.parentId]
     * value is [List] of all [TickEntity] with matching [TickEntity.parentId]
     *
     * @return emits empty map if none exist
     */
    @MapInfo(keyColumn = "parent_id")
    @Query("SELECT * FROM counter_ticks GROUP BY parent_id")
    fun ticksByCounterFlow(): Flow<Map<Long, List<TickEntity>>>

    /**
     * # Insert, Update,
     * ## by Object
     *
     */

    /**
     * Add [counter] if it doesn't already exist
     */
    @Insert
    suspend fun insertCounter(counter: CounterEntity)

    /**
     * Add [tick] if it doesn't already exist
     */
    @Insert
    suspend fun insertCounterTick(tick: TickEntity)

    /**
     * update [CounterEntity] to match [counter] if possible
     */
    @Update
    suspend fun updateCounter(counter: CounterEntity)

    /**
     * update [TickEntity] to match [tick] if possible
     */
    @Update
    suspend fun updateTick(tick: TickEntity)

    /**
     * # Delete
     * ## by ID
     */

    /**
     * Delete any [CounterEntity] with matching [id]
     */
    @Query("DELETE FROM counters WHERE id = :id")
    suspend fun deleteCounter(id: Long)

    /**
     * Delete any [TickEntity] with matching [id]
     */
    @Query("DELETE FROM counter_ticks WHERE id = :id")
    suspend fun deleteTick(id: Long)

    /**
     * Delete all [CounterEntity] whose [CounterEntity.id] is in [ids]
     */
    @Query("DELETE FROM counters WHERE id in (:ids)")
    suspend fun deleteCounters(ids: List<Long>)

    /**
     * Delete all [TickEntity] whose [TickEntity.id] is in [ids]
     */
    @Query("DELETE FROM counter_ticks WHERE id in (:ids)")
    suspend fun deleteTicks(ids: List<Long>)

    /**
     * # Summary & Calculation
     * ## as One-shot
     */

    /**
     * Sum of all [TickEntity] when grouped by [TickEntity.parentId]
     *
     * key is [TickEntity.parentId]
     * value is the sum of all matching [TickEntity]
     */
    @MapInfo(
        keyColumn = "parent_id",
        valueColumn = "tick_sum",
    )
    @Query("SELECT SUM(amount) AS tick_sum, parent_id FROM counter_ticks GROUP BY parent_id")
    suspend fun ticksSumBy(): Map<Long, Double>

    /**
     * Sum of all [TickEntity] with matching [parentId] within the bounds [[start], [end]]
     * 
     * @param[start] inclusive lower bound
     * @param[end] inclusive upper bound
     */
    @Query("SELECT SUM(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun ticksSumOf(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Double

    /**
     * Average of all [TickEntity] with matching [parentId] within the bounds [[start], [end]]
     *
     * @param[start] inclusive lower bound
     * @param[end] inclusive upper bound
     */
    @Query("SELECT AVG(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun ticksAverageOf(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Double

    /**
     * ## as Flow
     */

    /**
     * key is [TickEntity.parentId]
     * value is the aggregate sum
     *
     * @see [ticksSumBy]
     * @return emits empty map when non exist
     */
    @MapInfo(
        keyColumn = "parent_id",
        valueColumn = "tick_sum",
    )
    @Query("SELECT SUM(amount) AS tick_sum, parent_id FROM counter_ticks GROUP BY parent_id")
    fun ticksSumByFlow(): Flow<Map<Long, Double>>

    /**
     * @see [ticksSumOf]
     */
    @Query("SELECT SUM(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun ticksSumOfFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Flow<Double>

    /**
     * @see [ticksAverageOf]
     */
    @Query("SELECT AVG(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun ticksAverageOfFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Flow<Double>
}
