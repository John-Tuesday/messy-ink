package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksFrom
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.TickSort.TimeType
import org.calamarfederal.messyink.feature_counter.domain.UpdateTick
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject

/**
 * Default Implementation
 */
class GetTicksOfFlowImpl @Inject constructor(private val repo: CountersRepo) : GetTicksOfFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(parentId: Long, sort: TimeType): Flow<List<UiTick>> =
        repo.getTicksFlow(parentId = parentId, sort = sort)
            .mapLatest { it.map { item -> item.toUi() } }
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
class DeleteTicksFromImpl @Inject constructor(private val repo: CountersRepo) : DeleteTicksFrom {
    override suspend fun invoke(parentId: Long, timeType: TimeType, start: Instant, end: Instant) =
        repo.deleteTicksBySelection(
            parentId = parentId,
            timeType = timeType,
            start = start,
            end = end
        )
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
