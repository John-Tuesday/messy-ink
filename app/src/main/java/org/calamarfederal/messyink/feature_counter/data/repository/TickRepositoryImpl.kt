package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.source.TickLocalSource
import org.calamarfederal.messyink.feature_counter.data.toEntity
import org.calamarfederal.messyink.feature_counter.data.toTick
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import javax.inject.Inject
import kotlin.random.Random

private fun generateId(pool: Set<Long>, nextRand: () -> Long = { Random.nextLong() }): Long {
    var newId = nextRand()
    while (pool.contains(newId) || newId == NOID) newId = nextRand()
    return newId
}

class TickRepositoryImpl @Inject constructor(
    private val dao: TickLocalSource,
    @CurrentTime
    private val getCurrentTime: GetTime,
) : TickRepository {
    private suspend fun getTickIds(): List<Long> = dao.tickIds()

    private suspend fun _createNewTick(
        amount: Double,
        parentId: Long,
        timeForData: Instant? = null,
    ): Tick {
        require(parentId != NOID)

        val time = getCurrentTime()
        return Tick(
            amount = amount,
            timeCreated = time,
            timeModified = time,
            timeForData = timeForData ?: time,
            parentId = parentId,
            id = generateId(getTickIds().toSet())
        )
    }

    override fun getTicksFlow(parentId: Long, sort: TickSort): Flow<List<Tick>> =
        when (sort) {
            TickSort.TimeCreated  -> dao.ticksWithParentIdOrderCreatedFlow(parentId)
            TickSort.TimeModified -> dao.ticksWithParentIdOrderModifiedFlow(parentId)
            TickSort.TimeForData  -> dao.ticksWithParentIdOrderDataFlow(parentId)
        }.distinctUntilChanged().map { data -> data.map { it.toTick() } }

    override fun getTickFlow(id: Long): Flow<Tick?> {
        return flow { emit(dao.tick(id)?.toTick()) }
    }

    override suspend fun createTick(tick: Tick): Tick {
        require(tick.parentId != NOID) { "Cannot create Tick without a valid Parent ID" }

        val outTick = _createNewTick(tick.amount, parentId = tick.parentId)
        dao.insertTick(outTick.toEntity())
        return outTick
    }

    override suspend fun updateTick(tick: Tick) =
        0 < dao.updateTick(tick.toEntity())
    override suspend fun deleteTick(id: Long) = dao.deleteTick(id)
    override suspend fun deleteTicks(ids: List<Long>) = dao.deleteTicks(ids)
    override suspend fun deleteTicksOf(parentId: Long) = dao.deleteTickWithParentId(parentId)

    /**
     * # Summary & Calculation
     */
    override fun getTicksSumOfFlow(
        parentId: Long,
        timeType: TickSort,
        start: Instant,
        end: Instant,
    ): Flow<Double> = when (timeType) {
        TickSort.TimeModified -> dao.tickSumWithParentIdByModifiedFlow(
            parentId = parentId,
            start = start,
            end = end
        )

        TickSort.TimeCreated  -> dao.tickSumWithParentIdByCreatedFlow(
            parentId = parentId,
            start = start,
            end = end
        )

        TickSort.TimeForData  -> dao.tickSumWithParentIdByDataFlow(
            parentId = parentId,
            start = start,
            end = end
        )
    }

    override fun getTicksAverageOfFlow(
        parentId: Long,
        timeType: TickSort,
        start: Instant,
        end: Instant,
    ): Flow<Double> =
        when (timeType) {
            TickSort.TimeCreated  -> dao.tickAverageWithParentIdByCreatedFlow(
                parentId = parentId,
                start = start,
                end = end
            )

            TickSort.TimeModified -> dao.tickAverageWithParentIdByModifiedFlow(
                parentId = parentId,
                start = start,
                end = end
            )

            TickSort.TimeForData  -> dao.tickAverageWithParentIdByDataFlow(
                parentId = parentId,
                start = start,
                end = end
            )
        }

    override fun getTicksSumByFlow(): Flow<Map<Long, Double>> = dao.tickSumByParentIdFlow()
}
