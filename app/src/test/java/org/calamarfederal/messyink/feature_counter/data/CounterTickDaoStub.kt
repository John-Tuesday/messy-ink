package org.calamarfederal.messyink.feature_counter.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.data.RowsChanged
import org.calamarfederal.messyink.data.entity.CounterColumn.TimeType
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickColumn
import org.calamarfederal.messyink.data.entity.TickEntity

open class CounterTickDaoStub : CounterTickDao {
    override suspend fun ticksWithParentId(
        parentId: Long,
        sortColumn: TickColumn.TimeType,
    ): List<TickEntity> {
        TODO("Not yet implemented")
    }

    override fun ticksWithParentIdFlow(
        parentId: Long,
        sortColumn: TickColumn.TimeType,
    ): Flow<List<TickEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun tickIdsBySelector(
        parentId: Long,
        sortColumn: TickColumn.TimeType,
        start: Instant,
        end: Instant,
    ): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun tickIdsBySelectorWithLimit(
        parentId: Long,
        limit: Int,
        sortColumn: TickColumn.TimeType,
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

    override suspend fun tickSumWithParentIdBySelector(
        parentId: Long,
        selector: TickColumn.TimeType,
        start: Instant,
        end: Instant,
    ): Double {
        TODO("Not yet implemented")
    }

    override fun tickSumWithParentIdBySelectorFlow(
        parentId: Long,
        selector: TickColumn.TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override suspend fun tickAverageWithParentIdBySelector(
        parentId: Long,
        selector: TickColumn.TimeType,
        start: Instant,
        end: Instant,
    ): Double {
        TODO("Not yet implemented")
    }

    override fun tickAverageWithParentIdBySelectorFlow(
        parentId: Long,
        selector: TickColumn.TimeType,
        start: Instant,
        end: Instant,
    ): Flow<Double> {
        TODO("Not yet implemented")
    }

    override suspend fun ticks(sortColumn: TickColumn.TimeType): List<TickEntity> {
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

    override suspend fun counters(sortColumn: TimeType): List<CounterEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun counter(id: Long): CounterEntity? {
        TODO("Not yet implemented")
    }

    override fun counterFlow(id: Long): Flow<CounterEntity?> {
        TODO("Not yet implemented")
    }

    override fun countersFlow(sortColumn: TimeType): Flow<List<CounterEntity>> {
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
