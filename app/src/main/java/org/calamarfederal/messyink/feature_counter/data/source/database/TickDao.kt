package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * # Dao focusing on [TickEntity]
 */
@Dao
interface TickDao {
    /**
     * All [TickEntity] ordered by [TickEntity.timeModified]
     */
    @Transaction
    @Query("SELECT * FROM counter_tick")
    suspend fun ticks(): List<TickEntity>

    /**
     * [List] of every [TickEntity.id]
     */
    @Transaction
    @Query("SELECT id FROM counter_tick")
    suspend fun tickIds(): List<Long>

    /**
     * Return [TickEntity] with [id] if it exists, else null
     */
    @Transaction
    @Query("SELECT * FROM counter_tick WHERE id = :id")
    suspend fun tick(id: Long): TickEntity?

    /**
     * Add [tick] if it doesn't already exist
     */
    @Insert
    suspend fun insertTick(tick: TickEntity)

    /**
     * update [TickEntity] to match [tick] if possible
     */
    @Update
    suspend fun updateTick(tick: TickEntity): RowsChanged

    /**
     * Delete any [TickEntity] with matching [id]
     */
    @Transaction
    @Query("DELETE FROM counter_tick WHERE id = :id")
    suspend fun deleteTick(id: Long)

    /**
     * Delete all [TickEntity] whose [TickEntity.id] is in [ids]
     */
    @Transaction
    @Query("DELETE FROM counter_tick WHERE id in (:ids)")
    suspend fun deleteTicks(ids: List<Long>)

    /**
     * All [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData]
     */
    @Transaction
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId")
    suspend fun ticksWithParentId(parentId: Long): List<TickEntity>

    /**
     * @return emits [List] of every [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData] or empty [List]
     */
    @Transaction
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId ORDER BY time_for_data")
    fun ticksWithParentIdOrderDataFlow(parentId: Long): Flow<List<TickEntity>>

    /**
     * @return emits [List] of every [TickEntity] with matching [parentId] ordered by [TickEntity.timeModified] or empty [List]
     */
    @Transaction
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId ORDER BY time_modified")
    fun ticksWithParentIdOrderModifiedFlow(parentId: Long): Flow<List<TickEntity>>

    /**
     * @return emits [List] of every [TickEntity] with matching [parentId] ordered by [TickEntity.timeCreated] or empty [List]
     */
    @Transaction
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId ORDER BY time_created")
    fun ticksWithParentIdOrderCreatedFlow(parentId: Long): Flow<List<TickEntity>>

    /**
     * [List] of [TickEntity.id] sorted by [TickEntity.timeModified] in bounds [[start], [end]] with [parentId]
     */
    @Transaction
    @Query("SELECT id FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end ORDER BY time_for_data")
    suspend fun tickIdsByTimeForData(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): List<Long>

    /**
     * [List] of [TickEntity.id] sorted by [TickEntity.timeModified] in bounds [[start], [end]] with [parentId]
     */
    @Transaction
    @Query("SELECT id FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end ORDER BY time_for_data LIMIT :limit")
    suspend fun tickIdsByTimeForData(
        parentId: Long,
        limit: Int,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): List<Long>

    /**
     * Delete all [TickEntity] with [parentId]
     */
    @Transaction
    @Query("DELETE FROM counter_tick WHERE parent_id = :parentId")
    suspend fun deleteTickWithParentId(parentId: Long)

    /**
     * Sum of all [TickEntity] when grouped by [TickEntity.parentId]
     *
     * key is [TickEntity.parentId]
     * value is the sum of all matching [TickEntity]
     */
    @Transaction
    @MapInfo(
        keyColumn = "parent_id",
        valueColumn = "tick_sum",
    )
    @Query("SELECT SUM(amount) AS tick_sum, parent_id FROM counter_tick GROUP BY parent_id")
    fun tickSumByParentIdFlow(): Flow<Map<Long, Double>>

    /**
     * Sum of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun tickSumWithParentIdByTimeForData(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Double

    /**
     * Sum of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun tickSumWithParentIdByDataFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

    /**
     * Sum of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND time_created BETWEEN :start AND :end")
    fun tickSumWithParentIdByCreatedFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

    /**
     * Sum of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND time_modified BETWEEN :start AND :end")
    fun tickSumWithParentIdByModifiedFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

    /**
     * Average of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun tickAverageWithParentIdByTimeForData(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Double

    /**
     * Average of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun tickAverageWithParentIdByDataFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

    /**
     * Average of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND time_created BETWEEN :start AND :end")
    fun tickAverageWithParentIdByCreatedFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

    /**
     * Average of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Transaction
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND time_modified BETWEEN :start AND :end")
    fun tickAverageWithParentIdByModifiedFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

}
