package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport

/**
 * SAM
 *
 * @see invoke
 */
fun interface GetTicksOfFlow {
    /**
     * list of all UiTick sharing [parentId]
     */
    operator fun invoke(parentId: Long, sort: TickSort): Flow<List<UiTick>>
}

fun interface SimpleCreateTickUseCase {
    suspend operator fun invoke(amount: Double, parentId: Long): Tick
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
        timeType: TickSort,
        start: Instant,
        end: Instant,
    ): Flow<Double>

    /**
     * overload of [invoke] for when time has no bounds
     */
    operator fun invoke(parentId: Long, timeType: TickSort): Flow<Double> =
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

/**
 * Converter for Ticks -> Graph points
 */
fun interface TicksToGraphPoints {
    /**
     * Sort ticks by [sort] and cull any out of [domain] or out of [range]
     */
    operator fun invoke(
        filteredTicks: List<UiTick>,
        sort: TickSort,
        domain: TimeDomain,
        range: ClosedRange<Double>,
    ): List<PointByPercent>
}
