package org.calamarfederal.messyink.feature_counter.domain

import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick

/**
 * Create a [Tick] with a given amount and set the time values to the current time
 */
fun interface SimpleCreateTickUseCase {
    /**
     * Create [Tick] with [amount] and [parentId] and inject current time into the time values
     */
    suspend operator fun invoke(amount: Double, parentId: Long): Tick
}

/**
 * Converter for Ticks -> Graph points
 */
fun interface TicksToGraphPoints {
    /**
     * Sort ticks by [sort] and cull any out of [domain] or out of [range]
     */
    suspend operator fun invoke(
        filteredTicks: List<Tick>,
        sort: TickSort,
        domain: TimeDomain,
        range: ClosedRange<Double>,
    ): List<PointByPercent>
}
