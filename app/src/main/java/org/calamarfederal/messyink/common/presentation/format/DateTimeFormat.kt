package org.calamarfederal.messyink.common.presentation.format

import androidx.compose.material3.CalendarLocale
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/**
 * Date and time format specification
 *
 * combination of [org.calamarfederal.messyink.common.presentation.format.TimeFormat] and [org.calamarfederal.messyink.common.presentation.format.DateFormat]
 */
data class DateTimeFormat(
    /**
     * Year Format option
     */
    val year: YearFormat = YearFormat.FullDigit,
    /**
     * Month Format option
     */
    val month: MonthFormat = MonthFormat.FullName,
    /**
     * Day Format option
     */
    val day: DayFormat = DayFormat.TwoDigit,
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
    constructor(timeFormat: TimeFormat, dateFormat: DateFormat) : this(
        year = dateFormat.year, month = dateFormat.month, day = dateFormat.day,
        hour = timeFormat.hour, minute = timeFormat.minute, second = timeFormat.second,
        clockFormat = timeFormat.clockFormat
    )

    /**
     * Lazy Construct and get just the [DateFormat] portion
     */
    val dateFormat by lazy { DateFormat(year = year, month = month, day = day) }

    /**
     * Lazy Construct and get just the [TimeFormat] portion
     */
    val timeFormat by lazy {
        TimeFormat(hour = hour, minute = minute, second = second, clockFormat = clockFormat)
    }

    companion object
}

/**
 * [DateTimeFormat] with all options omitted
 */
val DateTimeFormat.Companion.Empty: DateTimeFormat
    get() = DateTimeFormat(
        timeFormat = TimeFormat.Empty,
        dateFormat = DateFormat.Empty
    )

/**
 * create copy but omit fields in which [momentA] and [momentB] are equal or as specified by threshold
 *
 * time units will be omitted when the respective threshold is `true` when fed the difference
 * between [momentA] and [momentB] (absolute value)
 */
fun DateTimeFormat.omitWhen(
    momentA: Instant,
    momentB: Instant,
    timeZone: TimeZone,
    hourThreshold: (Duration) -> Boolean = { it > 1.days },
    minuteThreshold: (Duration) -> Boolean = { it > 1.hours },
    secondThreshold: (Duration) -> Boolean = { it > 1.seconds },
): DateTimeFormat {
    val startDateTime = momentA.toLocalDateTime(timeZone)
    val endDateTime = momentB.toLocalDateTime(timeZone)
    val diff = (momentA - momentB).absoluteValue
    return this.copy(
        year = if (startDateTime.year == endDateTime.year) YearFormat.Omit else year,
        month = if (startDateTime.date == endDateTime.date) MonthFormat.Omit else month,
        day = if (startDateTime.date == endDateTime.date) DayFormat.Omit else day,
        hour = if (hourThreshold(diff)) HourFormat.Omit else hour,
        minute = if (minuteThreshold(diff)) MinuteFormat.Omit else minute,
        second = if (secondThreshold(diff)) SecondFormat.Omit else second,
    )
}

/**
 * Format a date and time with [dateTimeFormat] and [formatLocale]
 */
fun LocalDateTime.formatToString(
    dateTimeFormat: DateTimeFormat,
    formatLocale: CalendarLocale = java.util.Locale.getDefault(),
): String = "${
    date.formatToString(
        dateFormat = dateTimeFormat.dateFormat,
        formatLocale = formatLocale
    )
} ${time.formatToString(timeFormat = dateTimeFormat.timeFormat)}"
