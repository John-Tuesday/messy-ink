package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CounterSort.TimeType


open class CountersRepoStub : CountersRepo {
    override fun getCounterFlow(id: Long): Flow<Counter?> {
        TODO("Not yet implemented")
    }

    override fun getCountersFlow(sort: TimeType): Flow<List<Counter>> {
        TODO("Not yet implemented")
    }

    override fun getTicksFlow(parentId: Long, sort: TickSort.TimeType): Flow<List<Tick>> {
        TODO("Not yet implemented")
    }

    override fun getTickFlow(id: Long): Flow<Tick?> {
        TODO("Not yet implemented")
    }

    override suspend fun createCounter(counter: Counter): Counter {
        TODO("Not yet implemented")
    }

    override suspend fun createTick(tick: Tick): Tick {
        TODO("Not yet implemented")
    }

    override suspend fun updateCounter(counter: Counter): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateTick(tick: Tick): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCounter(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTick(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTicks(ids: List<Long>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTicksOf(parentId: Long) {
        TODO("Not yet implemented")
    }

    override fun getTicksSumOfFlow(
        parentId: Long,
        timeType: TickSort.TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override fun getTicksAverageOfFlow(
        parentId: Long,
        timeType: TickSort.TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override fun getTicksSumByFlow(): Flow<Map<Long, Double>> {
        TODO("Not yet implemented")
    }
}
