package org.calamarfederal.messyink.feature_counter.presentation.state

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Stable
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeGetter
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeZoneGetter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun epochMillisToDate(millis: Long, timeZone: TimeZone = CurrentTimeZoneGetter()): LocalDate =
    Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date

/**
 * Domain of time for a graph
 */
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
    @OptIn(ExperimentalStdlibApi::class, ExperimentalMaterial3Api::class)
    fun toSelectableDates(timeZone: TimeZone = CurrentTimeZoneGetter()): SelectableDates {
        val localStart = start.toLocalDateTime(timeZone)
        val localEnd = end.toLocalDateTime(timeZone)
        return SelectableTimeDomain(
            containsDate = if (inclusiveEnd) { it -> it in localStart.date .. localEnd.date }
            else { it -> it in localStart.date ..< localEnd.date },
            containsYear = if (!inclusiveEnd && localEnd == LocalDateTime(
                    year = localEnd.year,
                    0,
                    0,
                    0,
                    0
                )
            ) { it ->
                it in localStart.year ..< localEnd.year
            } else { it -> it in localStart.year .. localEnd.year }
        )
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

/**
 * Provide quick, adaptable options to set domain
 */
interface TimeDomainTemplate {
    /**
     * name for the UI
     */
    val label: String

    /**
     * build/get the domain
     */
    fun domain(): TimeDomain
}

/**
 * Generic class that just calls [domainBuilder] as [domain]
 */
data class TimeDomainLambdaTemplate(
    override val label: String,
    private val domainBuilder: TimeDomainTemplate.() -> TimeDomain,
) : TimeDomainTemplate {
    override fun domain() = domainBuilder()
}

/**
 * Template for building [TimeDomain] based on "time ago," i.e. [duration] form now
 */
class TimeDomainAgoTemplate(
    override val label: String,
    private val duration: Duration,
) : TimeDomainTemplate {
    override fun domain() = CurrentTimeGetter().let { TimeDomain((it - duration) .. it) }
}

