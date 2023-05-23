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
     * Lower bound, inclusive
     */
    val min: Instant,
    /**
     * Upper bound, inclusive
     */
    val max: Instant,
    /**
     * Name for UI
     */
    val label: String,
) {
    companion object {}
}

/**
 * Domain spanning all possible representable time
 */
val TimeDomain.Companion.AbsoluteAllTime: TimeDomain
    get() = TimeDomain(
        min = Instant.DISTANT_PAST,
        max = Instant.DISTANT_FUTURE,
        label = "Absolute All Time",
    )

/**
 * Template which can build a [TimeDomain]
 */
class TimeDomainTemplate(
    /**
     * Name for UI
     */
    val label: String,
    /**
     * Duration from Current time i.e. min = Current - [duration]
     */
    private val duration: Duration,
) {
    /**
     * Builds and returns the resulting [TimeDomain]
     */
    val domain: TimeDomain
        get() = CurrentTimeGetter().let {
            TimeDomain(
                max = it,
                min = it - duration,
                label = label
            )
        }

    companion object {
        /**
         * 365 days ago until now
         */
        val YearAgo get() = TimeDomainTemplate("Year ago", 365.days)

        /**
         * 30 days ago until now
         */
        val MonthAgo get() = TimeDomainTemplate("Month ago", 30.days)

        /**
         * 7 days ago until now
         */
        val WeekAgo get() = TimeDomainTemplate("Week ago", 7.days)

        /**
         * 1 days ago until now
         */
        val DayAgo get() = TimeDomainTemplate("Day ago", 1.days)

        /**
         * 1 hours ago until now
         */
        val HourAgo get() = TimeDomainTemplate("Hour ago", 1.hours)

        /**
         * All of the predefined defaults
         */
        val Defaults = listOf(YearAgo, MonthAgo, DayAgo, HourAgo)
    }
}

