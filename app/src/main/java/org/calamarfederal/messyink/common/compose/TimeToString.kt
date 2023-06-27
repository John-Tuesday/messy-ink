package org.calamarfederal.messyink.common.compose

import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeZoneGetter

/**
 * to string with the order Day Month Year
 *
 * am I using [androidx.compose.material3.formatWithSkeleton] wrong? use this until I figure it out
 */
fun Instant.localDateToString(
    year: Boolean = false,
    monthName: Boolean = true,
    separator: String = if (monthName) " " else "-",
): String {
    val dt = toLocalDateTime(CurrentTimeZoneGetter())
    return buildString {
        append(dt.dayOfMonth)
        append(separator)

        if (monthName)
            append(dt.month.name.run { take(1).uppercase() + drop(1).lowercase() })
        else
            append(dt.monthNumber)

        if (year) append(separator).append(dt.year)
    }
}

/**
 * convert to string using a 24 hr clock, localized with [CurrentTimeZoneGetter]
 *
 * am I using [androidx.compose.material3.formatWithSkeleton] wrong? use this until I figure it out
 */
fun Instant.localTimeToString(second: Boolean = false, separator: String = ":"): String {
    val dt = toLocalDateTime(CurrentTimeZoneGetter())
    return buildString {
        if (dt.hour < 10) append("0")
        append(dt.hour)
        append(separator)
        if (dt.minute < 10) append("0")
        append(dt.minute)
        if (second) {
            append(separator)
            if (dt.second < 10) append("0")
            append(dt.second)
        }
    }
}

/**
 * convenience function equivalent to [localToString] [divider] [localTimeToString]
 *
 * am I using [androidx.compose.material3.formatWithSkeleton] wrong? use this until I figure it out
 */
fun Instant.localToString(
    divider: String = " ",
    year: Boolean = false,
    second: Boolean = false,
): String = "${localDateToString(year)}$divider${localTimeToString(second)}"

//@OptIn(ExperimentalMaterial3Api::class)
//fun Instant.formatToString(
//    second: Boolean = false,
//    minute: Boolean = true,
//    hour: Boolean = true,
//    day: Boolean = true,
//    month: Boolean = false,
//    year: Boolean = false,
//    preferNames: Boolean = false,
//): String = formatWithSkeleton(
//    epochSeconds,
//    buildString {
//        if (year) append("yy")
//
//        if (month && !preferNames) append("M")
//        else if (month && preferNames) append("MMMM")
//
//        if (day) append("d")
//
//        if (hour && !preferNames) append("h")
//        else if (hour && preferNames) append("j")
//
//        if (minute) append("m")
//
//        if (second) append("s")
//    },
//)
