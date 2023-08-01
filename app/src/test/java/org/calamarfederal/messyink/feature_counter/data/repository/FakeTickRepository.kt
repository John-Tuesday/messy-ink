package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.model.getTime

class DummyTickRepository : TickRepository {
    override fun getTicksFlow(parentId: Long, sort: TickSort): Flow<List<Tick>> =
        TODO("Not yet implemented")

    override fun getTickFlow(id: Long): Flow<Tick?> = TODO("Not yet implemented")

    override suspend fun createTick(tick: Tick): Tick = TODO("Not yet implemented")

    override suspend fun updateTick(tick: Tick): Boolean = TODO("Not yet implemented")

    override suspend fun deleteTick(id: Long):Unit = TODO("Not yet implemented")

    override suspend fun deleteTicks(ids: List<Long>):Unit = TODO("Not yet implemented")

    override suspend fun deleteTicksOf(parentId: Long):Unit = TODO("Not yet implemented")

    override fun getTicksSumOfFlow(parentId: Long, timeType: TickSort, start: Instant, end: Instant): Flow<Double> =
        TODO("Not yet implemented")

    override fun getTicksAverageOfFlow(parentId: Long, timeType: TickSort, start: Instant, end: Instant): Flow<Double> =
        TODO("Not yet implemented")

    override fun getTicksSumByFlow(): Flow<Map<Long, Double>> = TODO("Not yet implemented")

}

@OptIn(ExperimentalCoroutinesApi::class)
class FakeTickRepository(
    private val tickDataState: MutableStateFlow<List<Tick>> = MutableStateFlow(listOf()),
) : TickRepository {
    override fun getTicksFlow(parentId: Long, sort: TickSort): Flow<List<Tick>> {
        return tickDataState.mapLatest {
            it.filter { it.parentId == parentId }.sortedBy { it.getTime(sort) }
        }
    }

    override fun getTickFlow(id: Long): Flow<Tick?> {
        return tickDataState.mapLatest { it.single { it.id == id } }
    }

    override suspend fun createTick(tick: Tick): Tick {
        lateinit var newTick: Tick
        tickDataState.update {
            newTick = tick.copy(id = it.lastOrNull()?.id?.plus(1) ?: 1)
            it + newTick
        }
        return newTick
    }

    override suspend fun updateTick(tick: Tick): Boolean {
        tickDataState.update {
            it.toMutableList().apply {
                set(
                    indexOfFirst { it.id == tick.id }.also { if (it == -1) return false },
                    tick
                )
            }
        }
        return true
    }

    override suspend fun deleteTick(id: Long) {
        return deleteTicks(listOf(id))
    }

    override suspend fun deleteTicks(ids: List<Long>) {
        tickDataState.update {
            it.toMutableList().apply { removeAll { it.id in ids } }
        }
    }

    override suspend fun deleteTicksOf(parentId: Long) {
        tickDataState.update {
            it.toMutableList().apply { removeAll { it.parentId == parentId } }
        }
    }

    override fun getTicksSumOfFlow(
        parentId: Long,
        timeType: TickSort,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        return getTicksFlow(
            parentId = parentId,
            sort = timeType
        ).mapLatest { it.filter { it.getTime(timeType) in start .. end }.sumOf { it.amount } }
    }

    override fun getTicksAverageOfFlow(
        parentId: Long,
        timeType: TickSort,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        return getTicksFlow(
            parentId = parentId,
            sort = timeType
        ).mapLatest {
            it.filter { it.getTime(timeType) in start .. end }.run {
                sumOf { it.amount } / this.size
            }
        }
    }

    override fun getTicksSumByFlow(): Flow<Map<Long, Double>> {
        return tickDataState.mapLatest {
            it.groupBy({ it.parentId }, { it.amount }).mapValues { (_, v) -> v.sum() }
        }
    }

}
