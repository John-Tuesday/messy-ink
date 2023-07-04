package org.calamarfederal.messyink.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.calamarfederal.messyink.data.entity.CounterEntity


/**
 * # Dao for [CounterEntity]
 */
@Dao
interface CounterDao {

    /**
     * [List] of every [CounterEntity.id]
     */
    @Transaction
    @Query("SELECT id FROM counter")
    suspend fun counterIds(): List<Long>

    /**
     * All [CounterEntity] ordered by [CounterEntity.timeModified]
     */
    @Transaction
    @Query("SELECT * FROM counter")
    suspend fun counters(): List<CounterEntity>

    /**
     * Return [CounterEntity] with [id] if it exits, else null
     */
    @Transaction
    @Query("SELECT * FROM counter WHERE id = :id")
    suspend fun counter(id: Long): CounterEntity?

    /**
     * @return emits [CounterEntity] with matching [id] or null when none exists
     */
    @Transaction
    @Query("SELECT * FROM counter WHERE id = :id")
    fun counterFlow(id: Long): Flow<CounterEntity?>

    /**
     * @return emits [List] of all [CounterEntity] ordered by [CounterEntity.timeModified] or empty [List]
     */
    @Transaction
    @Query("SELECT * FROM counter")
    fun countersFlow(): Flow<List<CounterEntity>>

    /**
     * All counters sorted by [CounterEntity.timeCreated]
     */
    @Transaction
    @Query("SELECT * FROM counter ORDER BY time_created")
    fun countersByCreatedFlow(): Flow<List<CounterEntity>>

    /**
     * All counters sorted by [CounterEntity.timeModified]
     */
    @Transaction
    @Query("SELECT * FROM counter ORDER BY time_modified")
    fun countersByModifiedFlow(): Flow<List<CounterEntity>>

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
    @Transaction
    @Query("DELETE FROM counter WHERE id = :id")
    suspend fun deleteCounter(id: Long)

    /**
     * Delete all [CounterEntity] whose [CounterEntity.id] is in [ids]
     */
    @Transaction
    @Query("DELETE FROM counter WHERE id in (:ids)")
    suspend fun deleteCounters(ids: List<Long>)
}
