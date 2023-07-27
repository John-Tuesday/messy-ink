package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Stable
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * Convenience function to convert [millis] to a date using [timeZone]
 */
fun epochMillisToDate(millis: Long, timeZone: TimeZone): LocalDate =
    Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date

/**
 * Domain of time for a graph
 */
@Stable
class TimeDomain(first: Instant, second: Instant, inclusive: Boolean) {
    /**
     * Lowest / earliest point
     */
    val start: Instant = minOf(first, second)

    /**
     * Highest / latest point, inclusion depends on [inclusiveEnd]
     */
    val end: Instant = maxOf(first, second)

    /**
     * Determines if [end] should be included
     */
    val inclusiveEnd: Boolean = inclusive

    constructor(closedRange: ClosedRange<Instant>) : this(
        first = closedRange.start,
        second = closedRange.endInclusive,
        inclusive = true
    )

    @OptIn(ExperimentalStdlibApi::class)
    constructor(openEndRange: OpenEndRange<Instant>) : this(
        first = openEndRange.start,
        second = openEndRange.endExclusive,
        inclusive = false
    )

    /**
     * True if [other] is within bounds
     *
     * when [inclusiveEnd] `[start] <= [other] <= [end]` otherwise `[start] <= [other] < [end]`
     */
    operator fun contains(other: Instant): Boolean =
        other >= start && if (inclusiveEnd) other <= end else other < end

    /**
     * Create a [SelectableDates] that tests true on dates within bounds
     */
    @OptIn(ExperimentalMaterial3Api::class)
    fun toSelectableDates(timeZone: TimeZone): SelectableDates {
        val startDate = start.toLocalDateTime(timeZone).date
        val endDate = end.toLocalDateTime(timeZone).date.let {
            if (inclusiveEnd) it
            else it.minus(DateTimeUnit.DAY)
        }
        return SelectableTimeDomain(
            containsDate = { it in startDate .. endDate },
            containsYear = { it in startDate.year .. endDate.year }
        )
    }

    override fun toString(): String {
        return "TimeDomain { start = $start, end = $end, endInclusive = $inclusiveEnd }"
    }

    override fun equals(other: Any?): Boolean =
        if (other is TimeDomain) other.start == start && other.end == end && other.inclusiveEnd == inclusiveEnd else false

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + inclusiveEnd.hashCode()
        return result
    }

    companion object
}

/**
 * Domain spanning all time possible (inclusive)
 */
val TimeDomain.Companion.AllTime: TimeDomain get() = TimeDomain(Instant.DISTANT_PAST .. Instant.DISTANT_FUTURE)

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
