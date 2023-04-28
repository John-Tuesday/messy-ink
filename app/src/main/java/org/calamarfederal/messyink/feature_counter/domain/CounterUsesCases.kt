package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick


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
    operator fun invoke(): Flow<List<UiCounter>>
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface GetTicksOfFlow {
    /**
     * list of all UiTick sharing [parentId]
     */
    operator fun invoke(parentId: Long): Flow<List<UiTick>>
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface CreateCounterFrom {
    /**
     * Use [sample] as the basis for a new counter to be saved and returned
     */
    suspend operator fun invoke(sample: UiCounter): UiCounter
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface CreateTickFrom {
    /**
     * Use [sample] as the basis for a new tick to be saved and returned
     */
    suspend operator fun invoke(sample: UiTick): UiTick
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

/**
 * @see [invoke]
 */
fun interface DeleteTicks {
    /**
     * Delete each [Tick] specified by [ids]
     */
    suspend operator fun invoke(ids: List<Long>)
    /**
     * Convenience for single tick deletion
     * @see [invoke]
     */
    suspend operator fun invoke(id: Long) = invoke(listOf(id))
}

/**
 * @see [invoke]
 */
fun interface DeleteTicksFrom {
    /**
     * Delete all [Tick] with [Tick.parentId] and [Tick.timeForData] within [[start], [end]]
     */
    suspend operator fun invoke(parentId: Long, start: Instant, end: Instant)

    /**
     * Overload of [invoke] with bounds set to max
     */
    suspend operator fun invoke(parentId: Long) =
        invoke(parentId, start = Instant.DISTANT_PAST, end = Instant.DISTANT_FUTURE)
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface GetTicksSumOfFlow {
    /**
     * Sum all ticks from [[start], [end]] with [parentId]
     *
     * @return Flow should emit 0.00 when no ticks exist (if it crashes make it nullable)
     */
    operator fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double>

    /**
     * overload of [invoke] for when time has no bounds
     */
    operator fun invoke(parentId: Long): Flow<Double> =
        invoke(parentId = parentId, start = Instant.DISTANT_PAST, end = Instant.DISTANT_FUTURE)
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface GetTicksAverageOfFlow {
    /**
     * Average all ticks from [[start], [end]] with [parentId]
     *
     * @return Flow should emit 0.00 when no ticks exist (if it crashes make it nullable)
     */
    operator fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double>

    /**
     * Overload for [invoke]. Useful when there are no time bounds
     */
    operator fun invoke(parentId: Long): Flow<Double> =
        invoke(parentId = parentId, start = Instant.DISTANT_PAST, end = Instant.DISTANT_FUTURE)
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface GetTicksSumByFlow {
    /**
     * groups ticks by parent_id and their respective collective sums
     *
     * @return Flow emits empty map if non exists
     */
    operator fun invoke(): Flow<Map<Long, Double>>
}
