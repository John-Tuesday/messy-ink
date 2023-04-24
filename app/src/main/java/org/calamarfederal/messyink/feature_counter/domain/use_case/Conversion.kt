package org.calamarfederal.messyink.feature_counter.domain.use_case

import org.calamarfederal.messyink.feature_counter.domain.Counter
import org.calamarfederal.messyink.feature_counter.domain.Tick
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick


internal fun Counter.toUI() = UiCounter(
    name = name,
    timeCreated = timeCreated,
    timeModified = timeModified,
    id = id,
)

internal fun Tick.toUi() = UiTick(
    amount = amount,
    timeCreated = timeCreated,
    timeForData = timeForData,
    parentId = parentId,
    id = id,
)

internal fun UiCounter.toCounter() = Counter(
    name = name,
    timeModified = timeModified,
    timeCreated = timeCreated,
    id = id,
)

internal fun UiTick.toTick() = Tick(
    amount = amount,
    timeForData = timeForData,
    timeCreated = timeCreated,
    parentId = parentId,
    id = id,
)