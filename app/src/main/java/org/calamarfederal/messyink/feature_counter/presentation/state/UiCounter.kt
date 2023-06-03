package org.calamarfederal.messyink.feature_counter.presentation.state

import androidx.compose.runtime.Stable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeGetter
import kotlin.time.Duration.Companion.days

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
    val timeModified: Instant = timeCreated,
    val id: Long,
)

/**
 * UI State holder to supply feedback when creating or editing a [UiCounter]
 *
 * @property[nameInput] current text the user has entered; NOT the saved copy in [UiCounter]
 * @property[nameHelp] support text to show to the user
 * @property[nameError] should this be shown as an error (i.e. disallow saving, show in red, etc.)
 */
@Stable
data class UiCounterSupport(
    val nameInput: String = "",
    val nameHelp: String? = null,
    val nameError: Boolean = nameHelp.isNullOrBlank(),
)

/**
 * Returns true if any error
 */
val UiCounterSupport.error: Boolean get() = nameError

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
    val timeCreated: Instant = Instant.DISTANT_FUTURE,
    val timeModified: Instant = timeCreated,
    val timeForData: Instant = timeCreated,
    val parentId: Long,
    val id: Long,
)

/**
 * # Preview Generator
 *
 * used to generate self-labeled UiCounters for preview and testing purposes.
 */
val previewUiCounters: Sequence<UiCounter>
    get() = (1 .. Int.MAX_VALUE).asSequence().map {
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
fun previewUiTicks(parentId: Long): Sequence<UiTick> =
    (1 .. Int.MAX_VALUE).asSequence().filterNot { it.toLong() == parentId }.map {
        val time = CurrentTimeGetter() + it.days
        UiTick(
            amount = it.toDouble(),
            timeModified = time,
            timeCreated = time,
            timeForData = time,
            parentId = parentId,
            id = it.toLong(),
        )
    }
