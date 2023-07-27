package org.calamarfederal.messyink.feature_counter.data

import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.source.database.TickEntity
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.Tick

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
