package org.calamarfederal.messyink.common.presentation.format

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.formatWithSkeleton
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn


/**
 * Date format options
 */
data class DateFormat(
    /**
     * Year format option
     */
    val year: YearFormat = YearFormat.FullDigit,
    /**
     * Month format option
     */
    val month: MonthFormat = MonthFormat.FullName,
    /**
     * Day format option
     */
    val day: DayFormat = DayFormat.TwoDigit,
) {
    companion object
}

/**
 * [DateFormat] with all option omitted
 */
val DateFormat.Companion.Empty: DateFormat
    get() = DateFormat(
        year = YearFormat.Omit,
        month = MonthFormat.Omit,
        day = DayFormat.Omit
    )

/**
 * Formatting Options for Year
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
 * Format to date relative to [formatLocale] time and style
 *
 * Month is formatted as part of a whole, i.e using `M` not `L` regardless of if its alone
 * [formatWithSkeleton] only supports Dates not Time and order doesn't matter
 */
@OptIn(ExperimentalMaterial3Api::class)
fun LocalDate.formatToString(
    dateFormat: DateFormat,
    formatLocale: CalendarLocale = java.util.Locale.getDefault(),
): String = formatWithSkeleton(
    utcTimeMillis = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
    locale = formatLocale,
    skeleton = buildString {
        when (dateFormat.year) {
            YearFormat.FullDigit -> append("yyyy")
            YearFormat.TwoDigit  -> append("yy")
            YearFormat.Omit      -> Unit
        }

        when (dateFormat.month) {
            MonthFormat.MinDigit        -> append("M")
            MonthFormat.TwoDigit        -> append("MM")
            MonthFormat.FullName        -> append("MMMM")
            MonthFormat.AbbreviatedName -> append("MMM")
            MonthFormat.NarrowName      -> append("MMMMM")
            MonthFormat.Omit            -> Unit
        }

        when (dateFormat.day) {
            DayFormat.MinDigit -> append("d")
            DayFormat.TwoDigit -> append("dd")
            DayFormat.Omit     -> Unit
        }
    },
)
