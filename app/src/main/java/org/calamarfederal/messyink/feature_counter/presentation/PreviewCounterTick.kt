package org.calamarfederal.messyink.feature_counter.presentation

import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import kotlin.time.Duration.Companion.days

/**
 * # Preview Generator
 *
 * used to generate self-labeled UiCounters for preview and testing purposes.
 */
val previewUiCounters: Sequence<Counter>
    get() = (1 .. Int.MAX_VALUE).asSequence().map {
        Counter(
            name = "name $it",
            timeCreated = Instant.fromEpochMilliseconds(it.days.inWholeMilliseconds),
            timeModified = Instant.fromEpochMilliseconds(it.days.inWholeMilliseconds),
            id = it.toLong(),
        )
    }

/**
 * # Preview Generator
 *
 * used to generate self-labeled UiTicks for preview and testing purposes.
 */
fun previewUiTicks(
    parentId: Long,
    timeGetter: GetTime = GetTime { Instant.fromEpochMilliseconds(0L) },
): Sequence<Tick> =
    (1 .. Int.MAX_VALUE).asSequence().filterNot { it.toLong() == parentId }.map {
        val time = timeGetter() + it.days
        Tick(
            amount = it.toDouble(),
            timeModified = time,
            timeCreated = time,
            timeForData = time,
            parentId = parentId,
            id = it.toLong(),
        )
    }
