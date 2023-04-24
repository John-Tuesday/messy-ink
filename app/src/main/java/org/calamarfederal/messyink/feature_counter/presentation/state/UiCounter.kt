package org.calamarfederal.messyink.feature_counter.presentation.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.key.Key.Companion.I
import kotlinx.datetime.Instant

const val NOID: Long = 0L

@Stable
data class UiCounter(
    val name: String = "",
    val timeCreated: Instant = Instant.DISTANT_FUTURE,
    val timeModified: Instant = Instant.DISTANT_FUTURE,
    val id: Long,
)

@Stable
data class UiTick(
    val amount: Double = 0.0,
    val timeCreated: Instant = Instant.DISTANT_FUTURE,
    val timeForData: Instant = Instant.DISTANT_FUTURE,
    val parentId: Long,
    val id: Long,
)

val previewUiCounters: Sequence<UiCounter> get() = (1..Int.MAX_VALUE).asSequence().map {
    UiCounter(
        name = "name $it",
        id = it.toLong(),
    )
}

fun previewUiTicks(parentId: Long): Sequence<UiTick> = (1..Int.MAX_VALUE).asSequence().map {
    UiTick(
        amount = it.toDouble(),
        parentId = parentId,
        id = it.toLong(),
    )
}
