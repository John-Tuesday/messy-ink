package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Tick

/**
 * Sort Ticks by element
 */
//sealed interface TickSort {
//    /**
//     * The time stamps recorded in each Tick
//     */
//    enum class TimeType : TickSort {
//        /**
//         * Time the tick was first created
//         */
//        TimeCreated,
//
//        /**
//         * Time the tick was last modified
//         */
//        TimeModified,
//
//        /**
//         * Default time to use when identifying when the tick 'happened'
//         */
//        TimeForData,
//    }
//}
//
///**
// * Get corresponding time based on [timeType]
// */
//fun Tick.getTime(timeType: TickSort.TimeType): Instant = when (timeType) {
//    TimeType.TimeModified -> timeModified
//    TimeType.TimeCreated  -> timeCreated
//    TimeType.TimeForData  -> timeForData
//}

/**
 * Sort counters by this
 */
sealed interface CounterSort {
    /**
     * Time stamps recorded in Counter
     */
    enum class TimeType : CounterSort {
        /**
         * Time the Counter was created
         */
        TimeCreated,

        /**
         * Time the Counter itself was last modified
         */
        TimeModified
    }
}
