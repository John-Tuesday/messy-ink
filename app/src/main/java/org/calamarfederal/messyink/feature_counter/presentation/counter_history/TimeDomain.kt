package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Convenience function to convert [millis] to a date using [timeZone]
 */
fun epochMillisToDate(millis: Long, timeZone: TimeZone): LocalDate =
    Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date

/**
 * More easily define [SelectableDates] implementations
 */
@OptIn(ExperimentalMaterial3Api::class)
class SelectableTimeDomain(
    private val containsDate: (LocalDate) -> Boolean,
    private val containsYear: (Int) -> Boolean,
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        containsDate(
            Instant.fromEpochMilliseconds(utcTimeMillis).toLocalDateTime(TimeZone.UTC).date
        )

    override fun isSelectableYear(year: Int): Boolean = containsYear(year)
}

/**
 * Return a [SelectableDates] object that allows only dates in [[start], [end]]
 */
@OptIn(ExperimentalMaterial3Api::class)
fun selectableDatesInRange(
    start: LocalDate,
    end: LocalDate,
): SelectableDates = SelectableTimeDomain(
    { it in start .. end },
    { it in start.year .. end.year }
)
