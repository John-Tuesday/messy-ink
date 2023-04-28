@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFrom
import org.calamarfederal.messyink.feature_counter.domain.CreateTickFrom
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject

/**
 * Default Implementation
 */
class GetCounterFlowImpl @Inject constructor(private val repo: CountersRepo) : GetCounterFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(id: Long): Flow<UiCounter?> =
        repo.getCounterFlow(id).mapLatest { it?.toUI() }
}

/**
 *  Default Implementation
 */
class GetCountersFlowImpl @Inject constructor(private val repo: CountersRepo) : GetCountersFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<List<UiCounter>> =
        repo.getCountersFlow().mapLatest { it.map { item -> item.toUI() } }
}

/**
 * Default Implementation
 */
class GetTicksOfFlowImpl @Inject constructor (private val repo: CountersRepo) : GetTicksOfFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(parentId: Long): Flow<List<UiTick>> =
        repo.getTicksFlow(parentId = parentId).mapLatest { it.map { item -> item.toUi() } }
}

/**
 * Default Implementation
 */
class CreateCounterFromImpl @Inject constructor(private val repo: CountersRepo) : CreateCounterFrom {
    override suspend fun invoke(sample: UiCounter): UiCounter =
        repo.createCounterFrom(sample.copy(id = NOID).toCounter()).toUI()
}

/**
 * Default Implementation
 */
class CreateTickFromImpl @Inject constructor(private val repo: CountersRepo) : CreateTickFrom {
    override suspend fun invoke(sample: UiTick): UiTick {
        require(sample.parentId != NOID)
        println("use case: create tick from: tick { parentId  = ${sample.parentId}, id = ${sample.id} }")
        return repo.createTickFrom(sample.copy(id = NOID).toTick()).toUi()
    }
}

/**
 * Default Implementation
 */
class DeleteCounterImpl @Inject constructor(private val repo: CountersRepo): DeleteCounter {
    override suspend fun invoke(id: Long) = repo.deleteCounter(id)
}

/**
 * Default Implementation
 */
class DeleteTicksImpl @Inject constructor(private val repo: CountersRepo): DeleteTicks {
    override suspend fun invoke(ids: List<Long>) = repo.deleteTicks(ids)
    override suspend fun invoke(id: Long) = repo.deleteTick(id)
}

/**
 * Default Implementation
 */
class DeleteTicksFromImpl @Inject constructor(private val repo: CountersRepo): DeleteTicksFrom {
    override suspend fun invoke(parentId: Long, start: Instant, end: Instant) =
        repo.deleteTicksFrom(parentId = parentId, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksSumOfFlowImpl @Inject constructor(private val repo: CountersRepo) : GetTicksSumOfFlow {
    override fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        repo.getTicksSumOfFlow(parentId = parentId, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksAverageOfFlowImpl @Inject constructor(private val repo: CountersRepo) : GetTicksAverageOfFlow {
    override fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        repo.getTicksAverageOfFlow(parentId = parentId, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksSumByFlowImpl @Inject constructor(private val repo: CountersRepo) : GetTicksSumByFlow {
    override fun invoke(): Flow<Map<Long, Double>> = repo.getTicksSumByFlow()
}
