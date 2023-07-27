package org.calamarfederal.messyink.feature_counter.domain.use_case

import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.error


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

internal fun UiCounter.toCounter() = Counter(
    name = name,
    timeModified = timeModified,
    timeCreated = timeCreated,
    id = id,
)

internal fun UiTick.toTick() = Tick(
    amount = amount,
    timeModified = timeModified,
    timeForData = timeForData,
    timeCreated = timeCreated,
    parentId = parentId,
    id = id,
)

internal fun Counter.toSupport(): UiCounterSupport = UiCounterSupport(
    nameInput = name,
    nameError = false,
    nameHelp = null,
    id = id,
)

internal fun UiTickSupport.toTickOrNull(): Tick? {
    if (this.error) return null

    return Tick(
        amount = amountInput.toDoubleOrNull() ?: return null,
        timeModified = timeModifiedInput,
        timeCreated = timeCreatedInput,
        timeForData = timeForDataInput,
        parentId = parentId,
        id = id ?: NOID,
    )
}
