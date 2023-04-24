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

@Dao
interface CounterDao {

    /**
     * ## IDs
     *
     */

    @Query("SELECT id FROM counters")
    suspend fun counterIds(): List<Long>

    @Query("SELECT id FROM counter_ticks")
    suspend fun tickIds(): List<Long>

    /**
     * # Get Objects
     * ## One-shot
     *
     */

    @Query("SELECT * FROM counters ORDER BY time_modified")
    suspend fun counters(): List<CounterEntity>

    @Query("SELECT * FROM counter_ticks WHERE parent_id = :parentId ORDER BY time_for_data")
    suspend fun ticksOf(parentId: Long): List<TickEntity>

    @MapInfo(keyColumn = "parent_id")
    @Query("SELECT * FROM counter_ticks GROUP BY parent_id")
    fun ticksByCounter(): Map<Long, List<TickEntity>>

    /**
     * ## Flow
     *
     */

    @Query("SELECT * FROM counters WHERE id = :id")
    fun counterFlow(id: Long): Flow<CounterEntity?>

    @Query("SELECT * FROM counters ORDER BY time_modified")
    fun countersFlow(): Flow<List<CounterEntity>>

    @Query("SELECT * FROM counter_ticks WHERE parent_id = :parentId ORDER BY time_for_data")
    fun ticksOfFlow(parentId: Long): Flow<List<TickEntity>>

    @MapInfo(keyColumn = "parent_id")
    @Query("SELECT * FROM counter_ticks GROUP BY parent_id")
    fun ticksByCounterFlow(): Flow<Map<Long, List<TickEntity>>>

    /**
     * # Insert, Update,
     * ## by Object
     *
     */

    @Insert
    suspend fun insertCounter(counter: CounterEntity)

    @Insert
    suspend fun insertCounterTick(tick: TickEntity)

    @Update
    suspend fun updateCounter(counter: CounterEntity)

    @Update
    suspend fun updateTick(tick: TickEntity)

    /**
     * # Delete
     * ## by ID
     */

    @Query("DELETE FROM counters WHERE id = :id")
    suspend fun deleteCounter(id: Long)

    @Query("DELETE FROM counter_ticks WHERE id = :id")
    suspend fun deleteTick(id: Long)

    @Query("DELETE FROM counters WHERE id in (:ids)")
    suspend fun deleteCounters(ids: List<Long>)

    @Query("DELETE FROM counter_ticks WHERE id in (:ids)")
    suspend fun deleteTicks(ids: List<Long>)


    /**
     * # Summary & Calculation
     * ## as One-shot
     */
    @MapInfo(
        keyColumn = "parent_id",
        valueColumn = "tick_sum",
    )
    @Query("SELECT SUM(amount) AS tick_sum, parent_id FROM counter_ticks GROUP BY parent_id")
    suspend fun ticksSumBy(): Map<Long, Double>

    @Query("SELECT SUM(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun ticksSumOf(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Double

    @Query("SELECT AVG(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    suspend fun ticksAverageOf(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Double

    /**
     * ## as Flow
     */

    @MapInfo(
        keyColumn = "parent_id",
        valueColumn = "tick_sum",
    )
    @Query("SELECT SUM(amount) AS tick_sum, parent_id FROM counter_ticks GROUP BY parent_id")
    fun ticksSumByFlow(): Flow<Map<Long, Double>>

    @Query("SELECT SUM(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun ticksSumOfFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Flow<Double>


    @Query("SELECT AVG(amount) FROM counter_ticks WHERE parent_id = :parentId AND time_for_data BETWEEN :start AND :end")
    fun ticksAverageOfFlow(
        parentId: Long,
        @TypeConverters(TimeTypeConverters::class) start: Instant,
        @TypeConverters(TimeTypeConverters::class) end: Instant,
    ): Flow<Double>
}
