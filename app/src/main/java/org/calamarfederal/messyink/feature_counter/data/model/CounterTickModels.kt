package org.calamarfederal.messyink.feature_counter.data.model

import kotlinx.datetime.Instant

/**
 * Holds the metadata for a collection of ticks, i.e. the parent / the owner
 *
 * @property[name] name given by the user; not unique
 * @property[timeModified] time of last modification of this Counter its Tick
 * @property[timeCreated] time the Counter was originally made
 * @property[id] unique identifier; also used for [Tick.parentId]
 */
data class Counter(
    val name: String,
    val timeModified: Instant,
    val timeCreated: Instant,
    val id: Long,
)

/**
 * Sorting options for [Counter]
 */
enum class CounterSort {
    /**
     * Sort by [Counter.timeCreated]
     */
    TimeCreated,

    /**
     * Sort by [Counter.timeModified]
     */
    TimeModified,
}

/**
 * Get the corresponding [Instant]
 */
fun Counter.getTime(sort: CounterSort): Instant = when (sort) {
    CounterSort.TimeCreated  -> timeCreated
    CounterSort.TimeModified -> timeModified
}

/**
 * Single event to be counted
 *
 * @property[amount] value to measured
 * @property[timeModified] time of last modification
 * @property[timeCreated] time the [Tick] was created; for use with history and logs
 * @property[timeForData] time to be used when analyzing; e.g. graphing
 * @property[parentId] id of owning [Counter]
 * @property[id] unique identity; (unique across all ticks)
 */
data class Tick(
    val amount: Double,
    val timeModified: Instant,
    val timeCreated: Instant,
    val timeForData: Instant,
    val parentId: Long,
    val id: Long,
)

/**
 * Sort Options for [Tick]
 */
enum class TickSort {
    /**
     * Time the tick was first created
     */
    TimeCreated,

    /**
     * Time the tick was last modified
     */
    TimeModified,

    /**
     * Default time to use when identifying when the tick 'happened'
     */
    TimeForData,
}

/**
 * Get the corresponding [Instant]
 */
fun Tick.getTime(sort: TickSort): Instant = when (sort) {
    TickSort.TimeForData  -> timeForData
    TickSort.TimeModified -> timeModified
    TickSort.TimeCreated  -> timeCreated
}
