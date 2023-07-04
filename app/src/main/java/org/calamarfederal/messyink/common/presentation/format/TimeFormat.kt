package org.calamarfederal.messyink.common.presentation.format

import kotlinx.datetime.LocalTime


/**
 * Time format options
 */
data class TimeFormat(
    /**
     * Hour Format option
     */
    val hour: HourFormat = HourFormat.TwoDigit,
    /**
     * Minute Format option
     */
    val minute: MinuteFormat = MinuteFormat.TwoDigit,
    /**
     * Second Format option
     */
    val second: SecondFormat = SecondFormat.TwoDigit,
    /**
     * Clock Format option
     */
    val clockFormat: ClockFormat = ClockFormat.TwentyFourHour,
) {
    companion object
}

/**
 * [TimeFormat] with all options omitted
 *
 * [ClockFormat] is set to [ClockFormat.TwentyFourHour] but [ClockFormat.TwelveHourHidden] is available
 */
val TimeFormat.Companion.Empty: TimeFormat
    get() = TimeFormat(
        hour = HourFormat.Omit,
        minute = MinuteFormat.Omit,
        second = SecondFormat.Omit,
        clockFormat = ClockFormat.TwentyFourHour
    )

/**
 * Formatting options of Hour
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
 * Format to time according to [timeFormat]
 */
fun LocalTime.formatToString(
    timeFormat: TimeFormat,
): String {
    val hourStr = when (timeFormat.hour) {
        HourFormat.TwoDigit -> if (hour < 10) "0$hour" else "$hour"
        HourFormat.MinDigit -> "$hour"
        HourFormat.Omit     -> ""
    }

    val minuteStr = when (timeFormat.minute) {
        MinuteFormat.TwoDigit -> if (minute < 10) "0$minute" else "$minute"
        MinuteFormat.MinDigit -> "$minute"
        MinuteFormat.Omit     -> ""
    }

    val secondStr = when (timeFormat.second) {
        SecondFormat.TwoDigit -> if (second < 10) "0$second" else "$minute"
        SecondFormat.MinDigit -> "$second"
        SecondFormat.Omit     -> ""
    }

    val periodStr = when (timeFormat.clockFormat) {
        ClockFormat.TwentyFourHour, ClockFormat.TwelveHourHidden            -> ""
        ClockFormat.AMPMLower, ClockFormat.AMPMTitle, ClockFormat.AMPMUpper -> {
            val amPm = (if (hour >= 12) "pm" else "am")
            when (timeFormat.clockFormat) {
                ClockFormat.AMPMUpper -> amPm.uppercase()
                ClockFormat.AMPMLower -> amPm.lowercase()
                ClockFormat.AMPMTitle -> amPm.replaceFirstChar { it.uppercase() }
                else                  -> ""
            }
        }
    }

    return listOf(hourStr, minuteStr, secondStr)
        .filterNot { it.isEmpty() }
        .joinToString(separator = ":") { it } + " $periodStr"
}
