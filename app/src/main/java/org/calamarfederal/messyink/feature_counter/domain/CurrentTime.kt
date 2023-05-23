package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

/**
 * Get Time
 */
fun interface GetTime {
    /**
     * return time
     */
    operator fun invoke(): Instant
}

/**
 * Get Time Zone
 */
fun interface GetTimeZone {
    /**
     * return current time zone
     */
    operator fun invoke(): TimeZone
}
