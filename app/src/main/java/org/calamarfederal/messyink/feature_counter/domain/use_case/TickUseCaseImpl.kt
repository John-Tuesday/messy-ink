package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPoints
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * Default Implementation
 */
class GetTicksOfFlowImpl @Inject constructor(private val repo: TickRepository) : GetTicksOfFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(parentId: Long, sort: TickSort): Flow<List<UiTick>> =
        repo.getTicksFlow(parentId = parentId, sort = sort)
            .mapLatest { it.map { item -> item.toUi() } }
}

/**
 * Default Implementation
 */
class CreateTickImpl @Inject constructor(private val repo: TickRepository) : CreateTick {
    override suspend fun invoke(tick: UiTick): UiTick {
        require(tick.parentId != NOID) { "Cannot create a Tick without a valid Parent ID" }
        return repo.createTick(tick.toTick()).toUi()
    }
}

/**
 * Default Implementation
 */
class DeleteTicksImpl @Inject constructor(private val repo: TickRepository) : DeleteTicks {
    override suspend fun invoke(ids: List<Long>) = repo.deleteTicks(ids)
    override suspend fun invoke(id: Long) = repo.deleteTick(id)
}

/**
 * Default Implementation
 */
class DeleteTicksOfImpl @Inject constructor(private val repo: TickRepository) : DeleteTicksOf {
    override suspend fun invoke(parentId: Long) = repo.deleteTicksOf(parentId)
}

/**
 * Default Implementation
 */
class GetTicksSumOfFlowImpl @Inject constructor(private val repo: TickRepository) :
    GetTicksSumOfFlow {
    override fun invoke(
        parentId: Long,
        timeType: TickSort,
        start: Instant,
        end: Instant,
    ): Flow<Double> =
        repo.getTicksSumOfFlow(parentId = parentId, timeType = timeType, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksSumByFlowImpl @Inject constructor(private val repo: TickRepository) :
    GetTicksSumByFlow {
    override fun invoke(): Flow<Map<Long, Double>> = repo.getTicksSumByFlow()
}

fun UiTick.getTime(sort: TickSort): Instant = when (sort) {
    TickSort.TimeForData  -> timeForData
    TickSort.TimeModified -> timeModified
    TickSort.TimeCreated  -> timeCreated
}

class TicksToGraphPointsImpl @Inject constructor() : TicksToGraphPoints {
    override fun invoke(
        filteredTicks: List<UiTick>,
        sort: TickSort,
        domain: TimeDomain,
        range: ClosedRange<Double>,
    ): List<PointByPercent> {
        val width = (domain.start - domain.end).absoluteValue
        val height = (range.start - range.endInclusive).absoluteValue
        return filteredTicks.map { tick ->
            PointByPercent(
                x = (tick.getTime(sort) - domain.start) / width,
                y = (tick.amount - range.start) / (height)
            )
        }
    }

}
