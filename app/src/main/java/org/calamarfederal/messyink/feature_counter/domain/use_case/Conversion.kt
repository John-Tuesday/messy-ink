package org.calamarfederal.messyink.feature_counter.domain.use_case

import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.Tick
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
    timeModified = timeModified,
    timeCreated = timeCreated,
    timeForData = timeForData,
    parentId = parentId,
    id = id,
)
