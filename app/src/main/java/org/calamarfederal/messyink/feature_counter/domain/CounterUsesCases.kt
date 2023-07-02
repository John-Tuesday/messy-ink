package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.domain.TickSort.TimeType
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport
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
    operator fun invoke(sort: CounterSort.TimeType): Flow<List<UiCounter>>
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
fun interface GetTicksOfFlow {
    /**
     * list of all UiTick sharing [parentId]
     */
    operator fun invoke(parentId: Long, sort: TimeType): Flow<List<UiTick>>
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface DuplicateCounter {
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

fun interface CreateTick {
    suspend operator fun invoke(tick: UiTick): UiTick
}

/**
 * SAM
 *
 * @see invoke
 */
fun interface DuplicateTick {
    /**
     * Use [sample] as the basis for a new tick to be saved and returned
     */
    suspend operator fun invoke(sample: UiTick): UiTick
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
 * SAM to Update Tick
 *
 * @see [invoke]
 */
fun interface UpdateTick {
    /**
     * find and set [UiTick] to [changed]; also update [UiCounter.timeModified]
     *
     * should it return the new value or `null` if not found
     */
    suspend operator fun invoke(changed: UiTick): Boolean
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
 * SAM delete all ticks of a counter
 *
 * @see [invoke]
 */
fun interface DeleteTicksOf {
    /**
     * delete all ticks with [parentId]
     */
    suspend operator fun invoke(parentId: Long)
}

/**
 * @see [invoke]
 */
fun interface DeleteTicksFrom {
    /**
     * Delete all [Tick] with [Tick.parentId] and [Tick.timeForData] within [[start], [end]]
     */
    suspend operator fun invoke(parentId: Long, timeType: TimeType, start: Instant, end: Instant)

    /**
     * Overload of [invoke] with bounds set to max
     */
    suspend operator fun invoke(parentId: Long, timeType: TimeType) =
        invoke(parentId, timeType, start = Instant.DISTANT_PAST, end = Instant.DISTANT_FUTURE)
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
    operator fun invoke(
        parentId: Long,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double>

    /**
     * overload of [invoke] for when time has no bounds
     */
    operator fun invoke(parentId: Long, timeType: TimeType): Flow<Double> =
        invoke(
            parentId = parentId,
            timeType = timeType,
            start = Instant.DISTANT_PAST,
            end = Instant.DISTANT_FUTURE
        )
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
    operator fun invoke(
        parentId: Long,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double>

    /**
     * Overload for [invoke]. Useful when there are no time bounds
     */
    operator fun invoke(parentId: Long, timeType: TimeType): Flow<Double> =
        invoke(
            parentId = parentId,
            timeType = timeType,
            start = Instant.DISTANT_PAST,
            end = Instant.DISTANT_FUTURE
        )
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
