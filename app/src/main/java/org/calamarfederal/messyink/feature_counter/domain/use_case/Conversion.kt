package org.calamarfederal.messyink.feature_counter.domain.use_case

import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter


internal fun Counter.toUI() = UiCounter(
    name = name,
    timeCreated = timeCreated,
    timeModified = timeModified,
    id = id,
)
