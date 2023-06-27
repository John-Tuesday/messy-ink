package org.calamarfederal.messyink.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.entity.CounterColumn
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickColumn
import org.calamarfederal.messyink.data.entity.TickColumn.TimeType
import org.calamarfederal.messyink.data.entity.TickEntity


/**
 * # Dao for [CounterEntity]
 */
@Dao
@TypeConverters(CounterColumnConverter::class)
interface CounterDao {

    /**
     * [List] of every [CounterEntity.id]
     */
    @Query("SELECT id FROM counter")
    suspend fun counterIds(): List<Long>

    /**
     * All [CounterEntity] ordered by [CounterEntity.timeModified]
     */
    @Query("SELECT * FROM counter ORDER BY :sortColumn")
    suspend fun counters(
        @TypeConverters(CounterColumnConverter::class)
        sortColumn: CounterColumn.TimeType,
    ): List<CounterEntity>

    /**
     * Return [CounterEntity] with [id] if it exits, else null
     */
    @Query("SELECT * FROM counter WHERE id = :id")
    suspend fun counter(id: Long): CounterEntity?

    /**
     * @return emits [CounterEntity] with matching [id] or null when none exists
     */
    @Query("SELECT * FROM counter WHERE id = :id")
    fun counterFlow(id: Long): Flow<CounterEntity?>

    /**
     * @return emits [List] of all [CounterEntity] ordered by [CounterEntity.timeModified] or empty [List]
     */
    @Query("SELECT * FROM counter ORDER BY :sortColumn")
    fun countersFlow(
        @TypeConverters(CounterColumnConverter::class)
        sortColumn: CounterColumn.TimeType,
    ): Flow<List<CounterEntity>>

    /**
     * Add [counter] if it doesn't already exist
     */
    @Insert
    suspend fun insertCounter(counter: CounterEntity)

    /**
     * update [CounterEntity] to match [counter] if possible
     */
    @Update
    suspend fun updateCounter(counter: CounterEntity): RowsChanged

    /**
     * Delete any [CounterEntity] with matching [id]
     */
    @Query("DELETE FROM counter WHERE id = :id")
    suspend fun deleteCounter(id: Long)

    /**
     * Delete all [CounterEntity] whose [CounterEntity.id] is in [ids]
     */
    @Query("DELETE FROM counter WHERE id in (:ids)")
    suspend fun deleteCounters(ids: List<Long>)
}
