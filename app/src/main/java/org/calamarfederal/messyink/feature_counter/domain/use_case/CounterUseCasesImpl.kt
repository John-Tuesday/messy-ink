@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEmpty
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.domain.Counter
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFrom
import org.calamarfederal.messyink.feature_counter.domain.CreateTickFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick

class GetCounterFlowImpl(private val repo: CountersRepo) : GetCounterFlow {
    override fun invoke(id: Long): Flow<UiCounter?> =
        repo.getCounterFlow(id).mapLatest { it?.toUI() }
}

class GetCountersFlowImpl(private val repo: CountersRepo) : GetCountersFlow {
    override fun invoke(): Flow<List<UiCounter>> =
        repo.getCountersFlow().mapLatest { it.map { item -> item.toUI() } }
}

class GetTicksOfFlowImpl(private val repo: CountersRepo) : GetTicksOfFlow {
    override fun invoke(parentId: Long): Flow<List<UiTick>> =
        repo.getTicksFlow(parentId = parentId).mapLatest { it.map { item -> item.toUi() } }
}

class CreateCounterFromImpl(private val repo: CountersRepo) : CreateCounterFrom {
    override suspend fun invoke(sample: UiCounter): UiCounter =
        repo.createCounterFrom(sample.copy(id = NOID).toCounter()).toUI()
}

class CreateTickFromImpl(private val repo: CountersRepo) : CreateTickFrom {
    override suspend fun invoke(sample: UiTick): UiTick {
        require(sample.parentId != NOID)
        println("use case: create tick from: tick { parentId  = ${sample.parentId}, id = ${sample.id} }")
        return repo.createTickFrom(sample.copy(id = NOID).toTick()).toUi()
    }
}

class GetTicksSumOfFlowImpl(private val repo: CountersRepo) : GetTicksSumOfFlow {
    override fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        repo.getTicksSumOfFlow(parentId = parentId, start = start, end = end)
}

class GetTicksAverageOfFlowImpl(private val repo: CountersRepo) : GetTicksAverageFlow {
    override fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        repo.getTicksAverageOfFlow(parentId = parentId, start = start, end = end)
}

class GetTicksSumByFlowImpl(private val repo: CountersRepo) : GetTicksSumByFlow {
    override fun invoke(): Flow<Map<Long, Double>> = repo.getTicksSumByFlow()
}
