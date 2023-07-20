package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetTickSupport
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.TickSort.TimeType
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPoints
import org.calamarfederal.messyink.feature_counter.domain.UpdateTick
import org.calamarfederal.messyink.feature_counter.domain.UpdateTickFromSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.error
import org.calamarfederal.messyink.feature_counter.presentation.state.getTime
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * Default Implementation
 */
class GetTicksOfFlowImpl @Inject constructor(private val repo: CountersRepo) : GetTicksOfFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(parentId: Long, sort: TimeType): Flow<List<UiTick>> =
        repo.getTicksFlow(parentId = parentId, sort = sort)
            .mapLatest { it.map { item -> item.toUi() } }
}

class GetTickSupportImpl @Inject constructor(private val repo: CountersRepo) : GetTickSupport {
    override suspend fun invoke(id: Long): UiTickSupport? {
        return repo.getTickFlow(id).singleOrNull()?.let {
            UiTickSupport(
                amountInput = it.amount.toString(),
                amountHelp = null,
                amountError = false,
                timeForDataInput = it.timeForData,
                timeForDataHelp = null,
                timeForDataError = false,
                timeModifiedInput = it.timeModified,
                timeModifiedHelp = null,
                timeModifiedError = false,
                timeCreatedInput = it.timeModified,
                timeCreatedHelp = null,
                timeCreatedError = false,
                parentId = it.parentId,
                id = it.id
            )
        }
    }
}

/**
 * Default Implementation
 */
class CreateTickImpl @Inject constructor(private val repo: CountersRepo) : CreateTick {
    override suspend fun invoke(tick: UiTick): UiTick {
        require(tick.parentId != NOID) { "Cannot create a Tick without a valid Parent ID" }
        return repo.createTick(tick.toTick()).toUi()
    }
}

/**
 * Default Implementation
 */
class UpdateTickImpl @Inject constructor(private val repo: CountersRepo) : UpdateTick {
    override suspend fun invoke(changed: UiTick) = repo.updateTick(changed.toTick())
}

class UpdateTickFromSupportImpl @Inject constructor(private val repo: CountersRepo) :
    UpdateTickFromSupport {
    override suspend fun invoke(support: UiTickSupport): Boolean {
        if (support.error || support.id == null) return false

        val tick = repo.getTickFlow(support.id).singleOrNull() ?: return false
        return repo.updateTick(support.toTickOrNull() ?: return false)
    }
}

/**
 * Default Implementation
 */
class DeleteTicksImpl @Inject constructor(private val repo: CountersRepo) : DeleteTicks {
    override suspend fun invoke(ids: List<Long>) = repo.deleteTicks(ids)
    override suspend fun invoke(id: Long) = repo.deleteTick(id)
}

/**
 * Default Implementation
 */
class DeleteTicksOfImpl @Inject constructor(private val repo: CountersRepo) : DeleteTicksOf {
    override suspend fun invoke(parentId: Long) = repo.deleteTicksOf(parentId)
}

/**
 * Default Implementation
 */
class GetTicksSumOfFlowImpl @Inject constructor(private val repo: CountersRepo) :
    GetTicksSumOfFlow {
    override fun invoke(
        parentId: Long,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> =
        repo.getTicksSumOfFlow(parentId = parentId, timeType = timeType, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksAverageOfFlowImpl @Inject constructor(private val repo: CountersRepo) :
    GetTicksAverageOfFlow {

    override fun invoke(
        parentId: Long,
        timeType: TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> =
        repo.getTicksAverageOfFlow(
            parentId = parentId,
            timeType = timeType,
            start = start,
            end = end
        )
}

/**
 * Default Implementation
 */
class GetTicksSumByFlowImpl @Inject constructor(private val repo: CountersRepo) :
    GetTicksSumByFlow {
    override fun invoke(): Flow<Map<Long, Double>> = repo.getTicksSumByFlow()
}

class TicksToGraphPointsImpl @Inject constructor() : TicksToGraphPoints {
    override fun invoke(
        filteredTicks: List<UiTick>,
        sort: TimeType,
        domain: TimeDomain,
        range: ClosedRange<Double>
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
