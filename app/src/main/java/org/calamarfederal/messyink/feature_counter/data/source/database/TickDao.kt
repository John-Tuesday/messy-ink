package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

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
}
