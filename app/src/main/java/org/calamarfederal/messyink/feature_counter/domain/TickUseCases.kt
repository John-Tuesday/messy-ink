package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.domain.TickSort.TimeType
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick

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
 * Create a new [Tick] and set [Tick.timeModified] and [Tick.timeCreated] accordingly
 */
fun interface CreateTick {
    /**
     * Create a new [Tick] and set [Tick.timeModified] and [Tick.timeCreated] accordingly
     */
    suspend operator fun invoke(tick: UiTick): UiTick
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
