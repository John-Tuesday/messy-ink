package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import org.calamarfederal.messyink.feature_counter.data.model.CounterSort
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport


/**
 * SAM Get Flow of corresponding Counter
 *
 * @see invoke
 */
fun interface GetCounterFlow {
    /**
     * Return flow watching for changes in Counter with [id] or emits null when does not exist
     */
    operator fun invoke(id: Long): Flow<UiCounter?>
}

/**
 * SAM Get All Counters
 *
 * @see invoke
 */
fun interface GetCountersFlow {
    /**
     * Get flow of all Counters
     *
     * @return flow emits empty list when none can be found
     */
    operator fun invoke(sort: CounterSort): Flow<List<UiCounter>>
}

/**
 * SAM Get Counter but return it as a [UiCounterSupport] instead of [UiCounter]
 */
fun interface GetCounterAsSupportOrNull {
    /**
     * gets the Counter with [id] and converts it to [UiCounterSupport] or `null` if it doesn't exist
     */
    suspend operator fun invoke(id: Long): UiCounterSupport?
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface CreateCounter {
    /**
     * Use [sample] as the basis for a new counter to be saved and returned
     */
    suspend operator fun invoke(sample: UiCounter): UiCounter
}

/**
 * Convert [UiCounterSupport] to a [Counter], save it, and return the saved version
 *
 * if [UiCounterSupport] has errors or otherwise cannot be saved as a valid [Counter] return null
 */
fun interface CreateCounterFromSupport {
    /**
     * Convert [UiCounterSupport] to a [Counter], save it, and return the saved version
     *
     * if [UiCounterSupport] has errors or otherwise cannot be saved as a valid [Counter] return null
     */
    suspend operator fun invoke(support: UiCounterSupport): UiCounter?
}

/**
 * SAM to Update Counter
 *
 * @see [invoke]
 */
fun interface UpdateCounter {
    /**
     * find and set [UiCounter] to [changed]; also update [UiCounter.timeModified]
     *
     * should it return the new value or `null` if not found
     */
    suspend operator fun invoke(changed: UiCounter): Boolean
}

/**
 * SAM to Update Counter
 */
fun interface UpdateCounterFromSupport {
    /**
     * Finds and updates Counter with matching [Counter.id]
     *
     * returns `false` if [support] has any errors or invalid id,
     * return `true` if the [Counter] was found and updated
     */
    suspend operator fun invoke(support: UiCounterSupport): Boolean
}

/**
 * Receive [UiCounterSupport], set its error & help appropriately
 */
fun interface UpdateCounterSupport {
    /**
     * Receive [changed], set its error & help appropriately
     */
    suspend operator fun invoke(changed: UiCounterSupport): UiCounterSupport
}

/**
 * @see [invoke]
 */
fun interface DeleteCounter {
    /**
     * Delete any [Counter] with matching [id]
     */
    suspend operator fun invoke(id: Long)
}
