package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.flow.Flow
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.CounterSort

/**
 * # Wrapper for managing counters, ticks, and their common commands
 */
interface CountersRepo {
    /**
     * Watch changes to Counter with [id]
     *
     * @param[id] Counter.id
     * @return Flow emits null when Counter with [id] does not exist
     */
    fun getCounterFlow(id: Long): Flow<Counter?>

    /**
     * Watch all counters
     *
     * @return Flow emits empty list when no Counter exists
     */
    fun getCountersFlow(sort: CounterSort): Flow<List<Counter>>

    /**
     * Copy [counter]'s values, but save with a new id, and set its timeModified, and timeCreated values. returns the new [Counter]
     *
     * @param[counter] counter which will be used as the basis for a new counter
     */
    suspend fun createCounter(counter: Counter): Counter

    /**
     * Update old [Counter] to [counter]
     *
     * if old [counter] cannot be found; then this does nothing
     */
    suspend fun updateCounter(counter: Counter): Boolean

    /**
     * Find and delete any [Counter] with matching [Counter.id]
     */
    suspend fun deleteCounter(id: Long)
}
