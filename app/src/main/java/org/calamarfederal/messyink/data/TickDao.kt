package org.calamarfederal.messyink.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import org.calamarfederal.messyink.data.entity.TickColumn
import org.calamarfederal.messyink.data.entity.TickEntity

/**
 * # Dao focusing on [TickEntity]
 */
@Dao
interface TickDao {
    /**
     * All [TickEntity] ordered by [TickEntity.timeModified]
     */
    @Query("SELECT * FROM counter_tick ORDER BY :sortColumn")
    suspend fun ticks(
        @TypeConverters(TickColumnConverters::class)
        sortColumn: TickColumn.TimeType,
    ): List<TickEntity>

    /**
     * [List] of every [TickEntity.id]
     */
    @Query("SELECT id FROM counter_tick")
    suspend fun tickIds(): List<Long>

    /**
     * Return [TickEntity] with [id] if it exists, else null
     */
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
    @Query("DELETE FROM counter_tick WHERE id = :id")
    suspend fun deleteTick(id: Long)

    /**
     * Delete all [TickEntity] whose [TickEntity.id] is in [ids]
     */
    @Query("DELETE FROM counter_tick WHERE id in (:ids)")
    suspend fun deleteTicks(ids: List<Long>)
}
