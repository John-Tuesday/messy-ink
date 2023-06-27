package org.calamarfederal.messyink.feature_counter.data

import org.calamarfederal.messyink.data.entity.CounterColumn
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickColumn
import org.calamarfederal.messyink.data.entity.TickEntity
import org.calamarfederal.messyink.feature_counter.domain.Counter
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.domain.Tick
import org.calamarfederal.messyink.feature_counter.domain.TickSort

internal fun CounterEntity.toCounter() = Counter(
    name = name,
    timeCreated = timeCreated,
    timeModified = timeModified,
    id = id,
)

internal fun TickEntity.toTick() = Tick(
    amount = amount,
    timeModified = timeModified,
    timeCreated = timeCreated,
    timeForData = timeForData,
    parentId = parentId,
    id = id,
)

internal fun Counter.toEntity() = CounterEntity(
    name = name,
    timeCreated = timeCreated,
    timeModified = timeModified,
    id = id,
)

internal fun Tick.toEntity() = TickEntity(
    amount = amount,
    timeModified = timeModified,
    timeCreated = timeCreated,
    timeForData = timeForData,
    parentId = parentId,
    id = id,
)

internal fun TickColumn.TimeType.toTickTimeSort(): TickSort.TimeType = when (this) {
    TickColumn.TimeCreated  -> TickSort.TimeType.TimeCreated
    TickColumn.TimeForData  -> TickSort.TimeType.TimeForData
    TickColumn.TimeModified -> TickSort.TimeType.TimeModified
}

internal fun TickColumn.toTickSort(): TickSort = when (this) {
    is TickColumn.TimeType -> this.toTickTimeSort()
}

internal fun TickSort.TimeType.toTickTimeType(): TickColumn.TimeType = when (this) {
    TickSort.TimeType.TimeCreated  -> TickColumn.TimeCreated
    TickSort.TimeType.TimeForData  -> TickColumn.TimeForData
    TickSort.TimeType.TimeModified -> TickColumn.TimeModified
}

internal fun TickSort.toTickColumn(): TickColumn = when (this) {
    is TickSort.TimeType -> this.toTickTimeType()
}

internal fun CounterColumn.TimeType.toCounterTimeSort(): CounterSort.TimeType = when (this) {
    CounterColumn.TimeCreated  -> CounterSort.TimeType.TimeCreated
    CounterColumn.TimeModified -> CounterSort.TimeType.TimeModified
}

internal fun CounterColumn.toColumnSort(): CounterSort = when (this) {
    is CounterColumn.TimeType -> this.toCounterTimeSort()
}

internal fun CounterSort.TimeType.toCounterTimeColumn(): CounterColumn.TimeType = when (this) {
    CounterSort.TimeType.TimeCreated  -> CounterColumn.TimeCreated
    CounterSort.TimeType.TimeModified -> CounterColumn.TimeModified
}

internal fun CounterSort.toCounterColumn(): CounterColumn = when (this) {
    is CounterSort.TimeType -> this.toCounterTimeColumn()
}
