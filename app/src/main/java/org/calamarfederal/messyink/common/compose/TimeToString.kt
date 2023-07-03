package org.calamarfederal.messyink.common.compose

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.formatWithSkeleton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.compose.ClockFormat.AMPMLower
import org.calamarfederal.messyink.common.compose.ClockFormat.AMPMTitle
import org.calamarfederal.messyink.common.compose.ClockFormat.AMPMUpper
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeZoneGetter

/**
 * Formatting Options for Year
 *
 * used in [Instant.formatToLocalizedDate]
 */
enum class YearFormat {
    /**
     * Do not include
     */
    Omit,

    /**
     * Last two digits of the year
     */
    TwoDigit,

    /**
     * All 4 digits of the year
     */
    FullDigit,
}

/**
 * Formatting Options for Month
 *
 * used in [Instant.formatToLocalizedDate]
 */
enum class MonthFormat {
    /**
     * Do not include
     */
    Omit,

    /**
     * Minimum number of digits for Month Number
     */
    MinDigit,

    /**
     * 2 digit Month Number (padding as necessary)
     */
    TwoDigit,

    /**
     * Full name of the month
     *
     * e.g. September
     */
    FullName,

    /**
     * Shortened name of the month name
     *
     * e.g. Sep
     */
    AbbreviatedName,

    /**
     * First letter of the month name
     *
     * e.g. S
     */
    NarrowName,
}

/**
 * Formatting Options for Day
 *
 * used in [Instant.formatToLocalizedDate]
 */
enum class DayFormat {
    /**
     * Do not include
     */
    Omit,

    /**
     * Day of the month as minimum digit representation
     */
    MinDigit,

    /**
     * Day of the month with zero padding as necessary
     */
    TwoDigit
}

/**
 * Formating options of Hour
 */
enum class HourFormat {
    /**
     * Do not include
     */
    Omit,

    /**
     * Minimum digits
     */
    MinDigit,

    /**
     * Two digits zero padded as necessary
     */
    TwoDigit,
}

/**
 * Formatting options for Minute
 */
enum class MinuteFormat {
    /**
     * Do not include
     */
    Omit,

    /**
     * Minimum Digit expression
     */
    MinDigit,

    /**
     * Two digit, zero padded as necessary
     */
    TwoDigit,
}

/**
 * Formatting options for Second
 */
enum class SecondFormat {
    /**
     * Do not include
     */
    Omit,

    /**
     * Minimum digit expression
     */
    MinDigit,

    /**
     * Two digits with 0 padding as necessary
     */
    TwoDigit,
}

/**
 * Clock Style & display style
 */
enum class ClockFormat {
    /**
     * 24-hour clock
     */
    TwentyFourHour,

    /**
     * 12-hour clock with no time period label
     */
    TwelveHourHidden,

    /**
     * 12-hour clock with `Am` or `Pm`
     */
    AMPMTitle,

    /**
     * 12-hour clock with `AM` or `PM`
     */
    AMPMLower,

    /**
     * 12-hour clock with `am` or `pm`
     */
    AMPMUpper,
}

/**
 * Format to date relative to [formatLocale] time and style
 *
 * [month] is formatted as part of a whole, i.e using `M` not `L` regardless of if its alone
 * [formatWithSkeleton] only supports Dates not Time and order doesn't matter
 */
@OptIn(ExperimentalMaterial3Api::class)
fun Instant.formatToLocalizedDate(
    day: DayFormat = DayFormat.MinDigit,
    month: MonthFormat = MonthFormat.FullName,
    year: YearFormat = YearFormat.FullDigit,
    formatLocale: CalendarLocale = java.util.Locale.getDefault(),//LocalConfiguration.current.locales[0],
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String = formatWithSkeleton(
    utcTimeMillis = toLocalDateTime(timeZone).date
        .atStartOfDayIn(TimeZone.UTC)
        .toEpochMilliseconds(),
    locale = formatLocale,
    skeleton = buildString {
        when (year) {
            YearFormat.FullDigit -> append("yyyy")
            YearFormat.TwoDigit  -> append("yy")
            YearFormat.Omit      -> Unit
        }

        when (month) {
            MonthFormat.MinDigit        -> append("M")
            MonthFormat.TwoDigit        -> append("MM")
            MonthFormat.FullName        -> append("MMMM")
            MonthFormat.AbbreviatedName -> append("MMM")
            MonthFormat.NarrowName      -> append("MMMMM")
            MonthFormat.Omit            -> Unit
        }

        when (day) {
            DayFormat.MinDigit -> append("d")
            DayFormat.TwoDigit -> append("dd")
            DayFormat.Omit     -> Unit
        }

    },
)

/**
 * same as [formatToLocalizedDate] except the timeZone is set by [CurrentTimeZoneGetter]
 * and the format [CalendarLocale] is the first in [LocalConfiguration]
 */
@Composable
fun Instant.formatToLocalDate(
    day: DayFormat = DayFormat.MinDigit,
    month: MonthFormat = MonthFormat.FullName,
    year: YearFormat = YearFormat.FullDigit,
): String = formatToLocalizedDate(
    day = day,
    month = month,
    year = year,
    formatLocale = LocalConfiguration.current.locales[0],
    timeZone = CurrentTimeZoneGetter(),
)

/**
 * convert to string using a 24 hr clock, localized with [CurrentTimeZoneGetter]
 *
 * am I using [androidx.compose.material3.formatWithSkeleton] wrong? use this until I figure it out
 */
fun Instant.localTimeToString(
    hour: HourFormat = HourFormat.TwoDigit,
    minute: MinuteFormat = MinuteFormat.TwoDigit,
    second: SecondFormat = SecondFormat.TwoDigit,
    clockFormat: ClockFormat = ClockFormat.TwentyFourHour,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val dt = toLocalDateTime(timeZone).time
    val hourStr = when (hour) {
        HourFormat.TwoDigit -> if (dt.hour < 10) "0${dt.hour}" else "${dt.hour}"
        HourFormat.MinDigit -> "${dt.hour}"
        HourFormat.Omit     -> ""
    }

    val minuteStr = when (minute) {
        MinuteFormat.TwoDigit -> if (dt.minute < 10) "0${dt.minute}" else "${dt.minute}"
        MinuteFormat.MinDigit -> "${dt.minute}"
        MinuteFormat.Omit     -> ""
    }

    val secondStr = when (second) {
        SecondFormat.TwoDigit -> if (dt.second < 10) "0${dt.second}" else "${dt.minute}"
        SecondFormat.MinDigit -> "${dt.second}"
        SecondFormat.Omit     -> ""
    }

    val periodStr = when (clockFormat) {
        ClockFormat.TwentyFourHour, ClockFormat.TwelveHourHidden -> ""
        AMPMLower, AMPMTitle, AMPMUpper                          -> {
            val amPm = (if (dt.hour >= 12) "pm" else "am")
            when (clockFormat) {
                AMPMUpper -> amPm.uppercase()
                AMPMLower -> amPm.lowercase()
                AMPMTitle -> amPm.replaceFirstChar { it.uppercase() }
                else      -> ""
            }
        }
    }

    return listOf(hourStr, minuteStr, secondStr)
        .filterNot { it.isEmpty() }
        .joinToString(separator = ":") { it } + " $periodStr"

}

/**
 * convenience function equivalent to [localToString] [divider] [localTimeToString]
 *
 * am I using [androidx.compose.material3.formatWithSkeleton] wrong? use this until I figure it out
 */
fun Instant.localToString(
    year: YearFormat = YearFormat.FullDigit,
    month: MonthFormat = MonthFormat.FullName,
    day: DayFormat = DayFormat.TwoDigit,
    hour: HourFormat = HourFormat.TwoDigit,
    minute: MinuteFormat = MinuteFormat.TwoDigit,
    second: SecondFormat = SecondFormat.TwoDigit,
    clockFormat: ClockFormat = ClockFormat.TwentyFourHour,
    divider: String = " ",
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    formatLocale: CalendarLocale = java.util.Locale.getDefault(),
): String = "${
    formatToLocalizedDate(
        year = year,
        month = month,
        day = day,
        formatLocale = formatLocale,
        timeZone = timeZone
    )
}$divider${
    localTimeToString(
        hour = hour,
        minute = minute,
        second = second,
        clockFormat = clockFormat,
        timeZone = timeZone
    )
}"
