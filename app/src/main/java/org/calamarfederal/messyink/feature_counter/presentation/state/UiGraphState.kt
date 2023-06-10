package org.calamarfederal.messyink.feature_counter.presentation.state

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Stable
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeGetter
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeZoneGetter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

//fun Instant.floor(dateTimeUnit: DateTimeUnit, timeZone: TimeZone = CurrentTimeZoneGetter()): Instant {
//    with(toLocalDateTime(timeZone)) {
//        when (dateTimeUnit.) {
//
//        }
//    }
//}

/**
 * Domain of Time but it adheres to Kotlin spec that [start] < [endInclusive]
 */
@Stable
@OptIn(ExperimentalMaterial3Api::class)
class TimeDomain(first: Instant, second: Instant) : ClosedRange<Instant>, SelectableDates {
    constructor(other: ClosedRange<Instant>) : this(other.start, other.endInclusive)

    override fun equals(other: Any?): Boolean =
        other is TimeDomain && start == other.start && endInclusive == other.endInclusive

    override val start: Instant = minOf(first, second)
    override val endInclusive: Instant = maxOf(first, second)

    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis.milliseconds.inWholeDays in (start.toEpochMilliseconds().milliseconds.inWholeDays .. endInclusive.toEpochMilliseconds().milliseconds.inWholeDays)

    override fun isSelectableYear(year: Int): Boolean = with(CurrentTimeZoneGetter()) {
        year in start.toLocalDateTime().year .. endInclusive.toLocalDateTime().year
    }

    fun toLocalTimeRange(timeZone: TimeZone = CurrentTimeZoneGetter()) = with(timeZone) {
        start.toLocalDateTime() .. endInclusive.toLocalDateTime()
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + endInclusive.hashCode()
        return result
    }

    companion object
}

/**
 * Widest Possible range of time
 */
val TimeDomain.Companion.AllTime: TimeDomain
    get() = TimeDomain(Instant.DISTANT_PAST, Companion.DISTANT_FUTURE)

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

