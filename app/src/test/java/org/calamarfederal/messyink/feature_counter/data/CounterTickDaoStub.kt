package org.calamarfederal.messyink.feature_counter.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.data.RowsChanged
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickEntity

open class CounterTickDaoStub : CounterTickDao {
    override suspend fun ticksWithParentId(parentId: Long): List<TickEntity> {
        TODO("Not yet implemented")
    }

    override fun ticksWithParentIdOrderDataFlow(parentId: Long): Flow<List<TickEntity>> {
        TODO("Not yet implemented")
    }

    override fun ticksWithParentIdOrderModifiedFlow(parentId: Long): Flow<List<TickEntity>> {
        TODO("Not yet implemented")
    }

    override fun ticksWithParentIdOrderCreatedFlow(parentId: Long): Flow<List<TickEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun tickIdsByTimeForData(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun tickIdsByTimeForData(
        parentId: Long,
        limit: Int,
        start: Instant,
        end: Instant,
    ): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTickWithParentId(parentId: Long) {
        TODO("Not yet implemented")
    }

    override fun tickSumByParentIdFlow(): Flow<Map<Long, Double>> {
        TODO("Not yet implemented")
    }

    override suspend fun tickSumWithParentIdByTimeForData(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Double {
        TODO("Not yet implemented")
    }

    override fun tickSumWithParentIdByDataFlow(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override fun tickSumWithParentIdByCreatedFlow(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override fun tickSumWithParentIdByModifiedFlow(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override suspend fun tickAverageWithParentIdByTimeForData(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Double {
        TODO("Not yet implemented")
    }

    override fun tickAverageWithParentIdByDataFlow(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override fun tickAverageWithParentIdByCreatedFlow(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override fun tickAverageWithParentIdByModifiedFlow(
        parentId: Long,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override suspend fun ticks(): List<TickEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun tickIds(): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun tick(id: Long): TickEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun insertTick(tick: TickEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTick(tick: TickEntity): RowsChanged {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTick(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTicks(ids: List<Long>) {
        TODO("Not yet implemented")
    }

    override suspend fun counterIds(): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun counters(): List<CounterEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun counter(id: Long): CounterEntity? {
        TODO("Not yet implemented")
    }

    override fun counterFlow(id: Long): Flow<CounterEntity?> {
        TODO("Not yet implemented")
    }

    override fun countersFlow(): Flow<List<CounterEntity>> {
        TODO("Not yet implemented")
    }

    override fun countersByCreatedFlow(): Flow<List<CounterEntity>> {
        TODO("Not yet implemented")
    }

    override fun countersByModifiedFlow(): Flow<List<CounterEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCounter(counter: CounterEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCounter(counter: CounterEntity): RowsChanged {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCounter(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCounters(ids: List<Long>) {
        TODO("Not yet implemented")
    }

}
