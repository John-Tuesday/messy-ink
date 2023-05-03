package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.datetime.Instant

/**
 * Get Time
 */
fun interface GetTime {
    /**
     * return time
     */
    operator fun invoke(): Instant
}
