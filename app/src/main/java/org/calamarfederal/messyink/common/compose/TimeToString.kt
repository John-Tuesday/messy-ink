package org.calamarfederal.messyink.common.compose

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.formatWithSkeleton
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.compose.TimeUnit.DAYS
import org.calamarfederal.messyink.common.compose.TimeUnit.EMPTY
import org.calamarfederal.messyink.common.compose.TimeUnit.ERR
import org.calamarfederal.messyink.common.compose.TimeUnit.HOURS
import org.calamarfederal.messyink.common.compose.TimeUnit.MINUTES
import org.calamarfederal.messyink.common.compose.TimeUnit.MONTHS
import org.calamarfederal.messyink.common.compose.TimeUnit.SECONDS
import org.calamarfederal.messyink.common.compose.TimeUnit.SUBSECOND
import org.calamarfederal.messyink.common.compose.TimeUnit.WEEKS
import org.calamarfederal.messyink.common.compose.TimeUnit.YEARS
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.di.CurrentTimeZone
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeGetter
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeZoneGetter
import kotlin.time.Duration
import kotlin.time.Duration.Companion
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
    tz: TimeZone = CurrentTimeZoneGetter(),
    now: Instant = CurrentTimeGetter(),
): String {
    val local = toLocalDateTime(tz)
    val diff = now.minus(this).absoluteValue
    if (1.minutes > diff) return "${diff.inWholeSeconds} seconds"
    if (1.hours > diff) return "${diff.inWholeMinutes} minutes"
    if (1.days > diff) return "${diff.inWholeHours} hours"
    return local.toString()
}

enum class TimeUnit {
    ERR, EMPTY, SUBSECOND, SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS,
}

data class TimeUnitValue(
    val value: Long?,
    val unit: TimeUnit,
) {
    override fun toString(): String {
        when {
            unit == EMPTY                -> return "Empty"
            unit == SUBSECOND            -> return "right now"
            unit == ERR || value == null -> return "ERROR"
        }
        return "$value ${
            when (unit) {
                SECONDS -> "s"
                MINUTES -> "m"
                HOURS   -> "h"
                DAYS    -> "d"
                WEEKS   -> "w"
                MONTHS  -> "mo"
                YEARS   -> "y"
                else    -> return "ERROR"
            }
        }"
    }
}

fun Duration.biggestTimeValue(): TimeUnitValue = when {
    !isFinite()          -> TimeUnitValue(null, ERR)
    inWholeDays != 0L    -> TimeUnitValue(inWholeDays, DAYS)
    inWholeMinutes != 0L -> TimeUnitValue(inWholeMinutes, MINUTES)
    inWholeSeconds != 0L -> TimeUnitValue(inWholeSeconds, SECONDS)
    else                 -> TimeUnitValue(null, SUBSECOND)
}

fun Instant.relativeTime(origin: Instant = CurrentTimeGetter()) = minus(origin).biggestTimeValue()
