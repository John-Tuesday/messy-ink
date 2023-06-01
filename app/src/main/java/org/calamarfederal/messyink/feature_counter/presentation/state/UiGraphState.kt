package org.calamarfederal.messyink.feature_counter.presentation.state

import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.domain.use_case.CurrentTimeGetter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/**
 * Used to describe and specify the domain of a Time-based Graph, i.e. UiTick
 */
data class TimeDomain(
    /**
     * Name for UI
     */
    val label: String,
    /**
     * Lower bound, inclusive
     */
    override val start: Instant,
    /**
     * Upper bound, inclusive
     */
    override val endInclusive: Instant,
) : ClosedRange<Instant> {
    constructor(label: String, domain: ClosedRange<Instant>) : this(
        label = label,
        start = domain.start,
        endInclusive = domain.endInclusive
    )

    companion object {}
}

/**
 * Domain spanning all possible representable time
 */
val TimeDomain.Companion.AbsoluteAllTime: TimeDomain
    get() = TimeDomain(
        start = Instant.DISTANT_PAST,
        endInclusive = Instant.DISTANT_FUTURE,
        label = "Absolute All Time",
    )

/**
 * Provide quick, adaptable options for user to set domain
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

    /**
     * auto use the label for the template for the domain
     */
    operator fun TimeDomain.Companion.invoke(domain: ClosedRange<Instant>) =
        TimeDomain(label = label, domain = domain)
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
 * Template which can build a [TimeDomain]
 */
class TimeDomainAgoTemplate(
    /**
     * Name for UI
     */
    override val label: String,
    /**
     * Duration from Current time i.e. min = Current - [duration]
     */
    private val duration: Duration,
) : TimeDomainTemplate {
    /**
     * Builds and returns the resulting [TimeDomain]
     */
    override fun domain() = CurrentTimeGetter().let {
        TimeDomain(
            endInclusive = it,
            start = it - duration,
            label = label
        )
    }

    companion object {
        /**
         * 365 days ago until now
         */
        val YearAgo get() = TimeDomainAgoTemplate("Year ago", 365.days)

        /**
         * 30 days ago until now
         */
        val MonthAgo get() = TimeDomainAgoTemplate("Month ago", 30.days)

        /**
         * 7 days ago until now
         */
        val WeekAgo get() = TimeDomainAgoTemplate("Week ago", 7.days)

        /**
         * 1 days ago until now
         */
        val DayAgo get() = TimeDomainAgoTemplate("Day ago", 1.days)

        /**
         * 1 hours ago until now
         */
        val HourAgo get() = TimeDomainAgoTemplate("Hour ago", 1.hours)

        /**
         * All of the predefined defaults
         */
        val Defaults = listOf(YearAgo, MonthAgo, DayAgo, HourAgo)
    }
}

