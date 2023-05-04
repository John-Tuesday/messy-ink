package org.calamarfederal.messyink.feature_counter.presentation.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.key.Key.Companion.I
import kotlinx.datetime.Instant

/**
 * Constant value to represent an unset ID
 */
const val NOID: Long = 0L

/**
 * # Ui State holder for counters
 * @param[name] name to show ui
 * @param[timeCreated] time the counter was actually created
 * @param[timeModified] time the counter itself, or its ticks were last changed ??? might remove
 * @param[id] unique id for use as a key in @Composable or for the view model to map to data
 */
@Stable
data class UiCounter(
    val name: String = "",
    val timeCreated: Instant = Instant.DISTANT_FUTURE,
    val timeModified: Instant = Instant.DISTANT_FUTURE,
    val id: Long,
)

/**
 * # Ui State holder for Ticks
 *
 * ticks are modification events to a counter.
 *
 * @param[amount] amount to offset the counter's total
 * @param[timeModified] time the tick was last modified
 * @param[timeCreated] time the tick was created
 * @param[timeForData] time the tick is considered to be when graphing or doing timeline operations
 * @param[parentId] the id of the owning Counter
 * @param[id] unique id for use as a key in @Composable or for the view model to map to data
 */
@Stable
data class UiTick(
    val amount: Double = 0.0,
    val timeModified: Instant = Instant.DISTANT_FUTURE,
    val timeCreated: Instant = Instant.DISTANT_FUTURE,
    val timeForData: Instant = Instant.DISTANT_FUTURE,
    val parentId: Long,
    val id: Long,
)

/**
 * # Preview Generator
 *
 * used to generate self-labeled UiCounters for preview and testing purposes.
 */
val previewUiCounters: Sequence<UiCounter> get() = (1..Int.MAX_VALUE).asSequence().map {
    UiCounter(
        name = "name $it",
        id = it.toLong(),
    )
}

/**
 * # Preview Generator
 *
 * used to generate self-labeled UiTicks for preview and testing purposes.
 */
fun previewUiTicks(parentId: Long): Sequence<UiTick> = (1..Int.MAX_VALUE).asSequence().filterNot { it.toLong() == parentId }.map {
    UiTick(
        amount = it.toDouble(),
        parentId = parentId,
        id = it.toLong(),
    )
}
