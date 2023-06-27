package org.calamarfederal.messyink.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.Transaction
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
     * [List] of every [CounterEntity.id]
     */
    @Query("SELECT id FROM counter")
    suspend fun counterIds(): List<Long>

    /**
     * [List] of every [TickEntity.id]
     */
    @Query("SELECT id FROM counter_tick")
    suspend fun tickIds(): List<Long>

    /**
     * All [CounterEntity] ordered by [CounterEntity.timeModified]
     */
    @Query("SELECT * FROM counter ORDER BY time_modified")
    suspend fun counters(): List<CounterEntity>

    /**
     * Return [CounterEntity] with [id] if it exits, else null
     */
    @Query("SELECT * FROM counter WHERE id = :id")
    suspend fun counter(id: Long): CounterEntity?

    /**
     * All [TickEntity] ordered by [TickEntity.timeModified]
     */
    @Query("SELECT * FROM counter_tick ORDER BY time_modified")
    suspend fun ticks(): List<TickEntity>

    /**
     * Return [TickEntity] with [id] if it exists, else null
     */
    @Query("SELECT * FROM counter_tick WHERE id = :id")
    suspend fun tick(id: Long): TickEntity?

    /**
     * All [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData]
     */
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId ORDER BY time_for_data")
    suspend fun ticksOf(parentId: Long): List<TickEntity>

    /**
     * [Map] of [TickEntity.parentId] to a [List] of all matching [TickEntity]
     *
     * key is [TickEntity.parentId]
     * value is [List] of all [TickEntity] with matching [TickEntity.parentId]
     */
    @MapInfo(keyColumn = "parent_id")
    @Query("SELECT * FROM counter_tick GROUP BY parent_id")
    fun ticksByCounter(): Map<Long, List<TickEntity>>

    /**
     * @return emits [CounterEntity] with matching [id] or null when none exists
     */
    @Query("SELECT * FROM counter WHERE id = :id")
    fun counterFlow(id: Long): Flow<CounterEntity?>

    /**
     * @return emits [List] of all [CounterEntity] ordered by [CounterEntity.timeModified] or empty [List]
     */
    @Query("SELECT * FROM counter ORDER BY time_modified")
    fun countersFlow(): Flow<List<CounterEntity>>

    /**
     * @return emits [List] of every [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData] or empty [List]
     */
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId ORDER BY time_for_data")
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
    @Query("SELECT * FROM counter_tick GROUP BY parent_id")
    fun ticksByCounterFlow(): Flow<Map<Long, List<TickEntity>>>

    /**
     * [List] of [TickEntity.id] sorted by [TickEntity.timeModified] in bounds [[start], [end]] with [parentId]
     */
    @Query("SELECT id FROM counter_tick WHERE parent_id = :parentId AND time_modified BETWEEN :start AND :end ORDER BY time_modified LIMIT :limit")
    suspend fun tickIdsByTimeModifiedWithLimit(
        parentId: Long,
        limit: Int,

        @TypeConverters(TimeTypeConverters::class)
        start: Instant,

        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): List<Long>

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
    suspend fun updateCounter(counter: CounterEntity): RowsChanged

    /**
     * update [TickEntity] to match [tick] if possible
     */
    @Update
    suspend fun updateTick(tick: TickEntity): RowsChanged

    /**
     * Delete any [CounterEntity] with matching [id]
     */
    @Query("DELETE FROM counter WHERE id = :id")
    suspend fun deleteCounter(id: Long)

    /**
     * Delete any [TickEntity] with matching [id]
     */
    @Query("DELETE FROM counter_tick WHERE id = :id")
    suspend fun deleteTick(id: Long)

    /**
     * Delete all [CounterEntity] whose [CounterEntity.id] is in [ids]
     */
    @Query("DELETE FROM counter WHERE id in (:ids)")
    suspend fun deleteCounters(ids: List<Long>)

    /**
     * Delete all [TickEntity] whose [TickEntity.id] is in [ids]
     */
    @Query("DELETE FROM counter_tick WHERE id in (:ids)")
    suspend fun deleteTicks(ids: List<Long>)

    /**
     * Delete all [TickEntity] with [parentId]
     */
    @Query("DELETE FROM counter_tick WHERE parent_id = :parentId")
    suspend fun deleteTicksOf(parentId: Long)

    /**
     * delete all [TickEntity] with matching [parentId] and [TickEntity.timeForData] in bounds [[start], [end]]
     *
     * @param[start] inclusive lower bound
     * @param[end] inclusive upper bound
     */
    @Query("DELETE FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun deleteTicksByTimeForData(
        parentId: Long,

        @TypeConverters(TimeTypeConverters::class)
        start: Instant,

        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    )

    /**
     * delete all [TickEntity] with [parentId] and [TickEntity.timeModified] in bounds [[start], [end]]
     */
    @Query("DELETE FROM counter_tick WHERE parent_id = :parentId AND time_modified BETWEEN :start AND :end")
    suspend fun deleteTicksByTimeModified(
        parentId: Long,

        @TypeConverters(TimeTypeConverters::class)
        start: Instant,

        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    )

    /**
     * Delete up to [limit] [TickEntity] with [parentId] and [TickEntity.timeModified] in bounds [[start], [end]], ordered by [TickEntity.timeModified]
     */
    @Transaction
    suspend fun deleteTicksByTimeModifiedLimited(
        parentId: Long,
        limit: Int,

        @TypeConverters(TimeTypeConverters::class)
        start: Instant,

        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ) {
        val ids = tickIdsByTimeModifiedWithLimit(
            parentId = parentId,
            limit = limit,
            start = start,
            end = end
        )
        deleteTicks(ids)
    }

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
    @Query("SELECT SUM(amount) AS tick_sum, parent_id FROM counter_tick GROUP BY parent_id")
    suspend fun ticksSumBy(): Map<Long, Double>

    /**
     * Sum of all [TickEntity] with matching [parentId] within the bounds [[start], [end]]
     *
     * @param[start] inclusive lower bound
     * @param[end] inclusive upper bound
     */
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
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
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun ticksAverageOf(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Double

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
    @Query("SELECT SUM(amount) AS tick_sum, parent_id FROM counter_tick GROUP BY parent_id")
    fun ticksSumByFlow(): Flow<Map<Long, Double>>

    /**
     * @see [ticksSumOf]
     */
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun ticksSumOfFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Flow<Double>

    /**
     * @see [ticksAverageOf]
     */
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun ticksAverageOfFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Flow<Double>
}
