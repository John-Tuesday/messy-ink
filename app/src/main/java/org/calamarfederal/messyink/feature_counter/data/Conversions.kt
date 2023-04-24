package org.calamarfederal.messyink.feature_counter.data

import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickEntity
import org.calamarfederal.messyink.feature_counter.domain.Counter
import org.calamarfederal.messyink.feature_counter.domain.Tick

internal fun CounterEntity.toCounter() = Counter(
    name = name,
    timeCreated = timeCreated,
    timeModified = timeModified,
    id = id,
)

internal fun TickEntity.toTick() = Tick(
    amount = amount,
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
    timeCreated = timeCreated,
    timeForData = timeForData,
    parentId = parentId,
    id = id,
)
