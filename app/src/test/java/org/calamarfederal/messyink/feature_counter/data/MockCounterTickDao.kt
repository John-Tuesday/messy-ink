package org.calamarfederal.messyink.feature_counter.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickColumn.TimeType
import org.calamarfederal.messyink.data.entity.TickEntity
import org.calamarfederal.messyink.data.entity.getTimeColumn

class MockCounterTickDao(
    private val tickTable: List<TickEntity>,
    private val counterTable: List<CounterEntity>,
) : CounterTickDaoStub() {
    override fun ticksWithParentIdFlow(
        parentId: Long,
        sortColumn: TimeType,
    ): Flow<List<TickEntity>> = flowOf(
        tickTable
            .filter { it.parentId == parentId }
            .sortedBy { it.getTimeColumn(sortColumn) }
    )
}
