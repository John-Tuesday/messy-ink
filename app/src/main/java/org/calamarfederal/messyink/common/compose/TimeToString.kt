package org.calamarfederal.messyink.common.compose

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.di.CurrentTimeZone
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Localize Time to string, but
 * - [seconds] if less than 1 [minutes] from [now]
 * - [minutes] if less than 1 [hours] from [now]
 * - [hours] if less than 1 [days] from [now]
 * - otherwise use implementation provided by [toLocalDateTime]
 */
fun Instant.toDbgString(
    @CurrentTimeZone tz: TimeZone,
    @CurrentTime now: GetTime,
): String {
    val local = toLocalDateTime(tz)
    var diff = now().minus(this)
    val suffix = if (diff.isNegative()) "ago" else "ahead"
    diff = diff.absoluteValue
    if (1.minutes > diff) return "${diff.inWholeSeconds} seconds $suffix"
    if (1.hours > diff) return "${diff.inWholeMinutes} minutes $suffix"
    if (1.days > diff) return "${diff.inWholeHours} days $suffix"
    return local.toString()
}

