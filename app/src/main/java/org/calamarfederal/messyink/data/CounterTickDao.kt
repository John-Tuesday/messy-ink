package org.calamarfederal.messyink.data

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.entity.TickColumn
import org.calamarfederal.messyink.data.entity.TickColumn.TimeType
import org.calamarfederal.messyink.data.entity.TickEntity

/**
 * Dao for combined operations of [TickEntity] and [CounterEntity]
 */
@Dao
interface CounterTickDao : TickDao, CounterDao {

    /**
     * All [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData]
     */
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId ORDER BY :sortColumn")
    suspend fun ticksWithParentId(
        parentId: Long,
        @TypeConverters(TickColumnConverters::class)
        sortColumn: TimeType,
    ): List<TickEntity>

    /**
     * @return emits [List] of every [TickEntity] with matching [parentId] ordered by [TickEntity.timeForData] or empty [List]
     */
    @Query("SELECT * FROM counter_tick WHERE parent_id = :parentId ORDER BY :sortColumn")
    fun ticksWithParentIdFlow(
        parentId: Long,
        @TypeConverters(TickColumnConverters::class)
        sortColumn: TimeType,
    ): Flow<List<TickEntity>>

    /**
     * [List] of [TickEntity.id] sorted by [TickEntity.timeModified] in bounds [[start], [end]] with [parentId]
     */
    @Query("SELECT id FROM counter_tick WHERE parent_id = :parentId AND :sortColumn BETWEEN :start AND :end ORDER BY :sortColumn")
    suspend fun tickIdsBySelector(
        parentId: Long,
        @TypeConverters(TickColumnConverters::class)
        sortColumn: TimeType,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): List<Long>

    /**
     * [List] of [TickEntity.id] sorted by [TickEntity.timeModified] in bounds [[start], [end]] with [parentId]
     */
    @Query("SELECT id FROM counter_tick WHERE parent_id = :parentId AND :sortColumn BETWEEN :start AND :end ORDER BY :sortColumn LIMIT :limit")
    suspend fun tickIdsBySelectorWithLimit(
        parentId: Long,
        limit: Int,
        @TypeConverters(TickColumnConverters::class)
        sortColumn: TickColumn.TimeType,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): List<Long>

    /**
     * Delete all [TickEntity] with [parentId]
     */
    @Query("DELETE FROM counter_tick WHERE parent_id = :parentId")
    suspend fun deleteTickWithParentId(parentId: Long)

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
    fun tickSumByParentIdFlow(): Flow<Map<Long, Double>>

    /**
     * Sum of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND :selector BETWEEN :start AND :end")
    suspend fun tickSumWithParentIdBySelector(
        parentId: Long,
        @TypeConverters(TickColumnConverters::class)
        selector: TimeType,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Double

    /**
     * Sum of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Query("SELECT SUM(amount) FROM counter_tick WHERE parent_id = :parentId AND :selector BETWEEN :start AND :end")
    fun tickSumWithParentIdBySelectorFlow(
        parentId: Long,
        @TypeConverters(TickColumnConverters::class)
        selector: TimeType,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

    /**
     * Average of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND :selector BETWEEN :start AND :end")
    suspend fun tickAverageWithParentIdBySelector(
        parentId: Long,
        @TypeConverters(TickColumnConverters::class)
        selector: TimeType,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Double

    /**
     * Average of all [TickEntity.amount] with [parentId] and [selector] between [start] and [end]
     */
    @Query("SELECT AVG(amount) FROM counter_tick WHERE parent_id = :parentId AND :selector BETWEEN :start AND :end")
    fun tickAverageWithParentIdBySelectorFlow(
        parentId: Long,
        @TypeConverters(TickColumnConverters::class)
        selector: TimeType,
        @TypeConverters(TimeTypeConverters::class)
        start: Instant,
        @TypeConverters(TimeTypeConverters::class)
        end: Instant,
    ): Flow<Double>

    /**
     * Delete up to [limit] [TickEntity] with [parentId] and [TickEntity.timeModified] in bounds [[start], [end]], ordered by [TickEntity.timeModified]
     */
    @Transaction
    suspend fun deleteTicksBySelector(
        parentId: Long,
        limit: Int?,
        sortColumn: TimeType,
        start: Instant,
        end: Instant,
    ) {
        val ids = if (limit != null) tickIdsBySelectorWithLimit(
            parentId = parentId,
            limit = limit,
            sortColumn = sortColumn,
            start = start,
            end = end
        ) else tickIdsBySelector(
            parentId = parentId,
            sortColumn = sortColumn,
            start = start,
            end = end
        )
        deleteTicks(ids)
    }

}
