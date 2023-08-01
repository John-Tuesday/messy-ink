package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.model.getTime
import org.calamarfederal.messyink.feature_counter.di.DefaultDispatcher
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * State for TickGraph
 */
data class TickGraphState(
    /**
     * sorted, culled, and mapped Ticks
     */
    val graphPoints: List<PointByPercent> = listOf(),
    /**
     * Min and max of the whole unfiltered data set
     * or `[Instant.DISTANT_PAST]..[Instant.DISTANT_FUTURE]` when no data
     */
    val domainBounds: ClosedRange<Instant> = Instant.DISTANT_PAST .. Instant.DISTANT_FUTURE,
    /**
     * Current domain displayed
     */
    val currentDomain: ClosedRange<Instant> = domainBounds,
    /**
     * Min and max of the whole unfiltered data or `0.00..1.00` when its empty
     */
    val rangeBounds: ClosedRange<Double> = 0.00 .. 1.00,
    /**
     * Current range displayed
     */
    val currentRange: ClosedRange<Double> = rangeBounds,
)

/**
 * Sorting then prepping to graph Ticks with shared parentId
 */
interface TickGraphRepository {
    /**
     * Get the max and min values of amount and sort time
     */
    suspend fun getMaxBounds(
        ticks: List<Tick>,
        sort: TickSort,
    ): Pair<ClosedRange<Instant>, ClosedRange<Double>>

    /**
     * with [domain] and [range] as the viewport, map [ticks] to [PointByPercent]
     *
     * requires [domain] and [range] starts and ends not to be equal
     */
    suspend fun convertToGraphPoints(
        ticks: List<Tick>,
        sort: TickSort,
        domain: ClosedRange<Instant>,
        range: ClosedRange<Double>,
    ): List<PointByPercent>

    /**
     * map [ticks] to [PointByPercent] with graph determined by [domain] and [range]
     * using the time value specified by [sort]
     *
     * if [domain] or [range] are `null` then use the maxBounds
     */
    suspend fun convertToGraphState(
        ticks: List<Tick>,
        sort: TickSort,
        domain: ClosedRange<Instant>? = null,
        range: ClosedRange<Double>? = null,
    ): TickGraphState
}

/**
 * Default Implementation
 */
class TickGraphRepositoryImpl @Inject constructor(
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
) : TickGraphRepository {
    override suspend fun getMaxBounds(
        ticks: List<Tick>,
        sort: TickSort,
    ): Pair<ClosedRange<Instant>, ClosedRange<Double>> = withContext(defaultDispatcher) {
        var domain: ClosedRange<Instant>? = null
        var range: ClosedRange<Double>? = null

        for (tick in ticks) {
            range = range?.let {
                if (tick.amount in it) it else minOf(
                    it.start,
                    tick.amount
                ) .. maxOf(it.endInclusive, tick.amount)
            } ?: tick.amount .. tick.amount
            val time = tick.getTime(sort)
            domain = domain?.let {
                if (time in it) it else minOf(it.start, time) .. maxOf(it.endInclusive, time)
            } ?: time .. time
        }

        (domain ?: (Instant.DISTANT_PAST .. Instant.DISTANT_FUTURE)) to (range
            ?: (0.00 .. 1.00))
    }

    override suspend fun convertToGraphPoints(
        ticks: List<Tick>,
        sort: TickSort,
        domain: ClosedRange<Instant>,
        range: ClosedRange<Double>,
    ): List<PointByPercent> = withContext(defaultDispatcher) {
        require(domain.start != domain.endInclusive) { "Domain start cannot equal end" }
        require(range.start != range.endInclusive) { "Range start cannot equal end" }
        ticks.map {
            PointByPercent(
                x = (it.getTime(sort) - domain.start) / (domain.endInclusive - domain.start).absoluteValue,
                y = (it.amount - range.start) / (range.endInclusive - range.start).absoluteValue,
            )
        }
    }

    override suspend fun convertToGraphState(
        ticks: List<Tick>,
        sort: TickSort,
        domain: ClosedRange<Instant>?,
        range: ClosedRange<Double>?,
    ): TickGraphState {
        val bounds = getMaxBounds(ticks = ticks, sort = sort)
        val currentDomain = domain ?: bounds.first
        val currentRange = (range ?: bounds.second).let {
            if (it.start == it.endInclusive)
                minOf(0.00, it.start) .. maxOf(1.00, it.start)
            else
                it
        }
        val graphPoints = convertToGraphPoints(
            ticks = ticks,
            sort = sort,
            domain = currentDomain,
            range = currentRange,
        )

        return TickGraphState(
            currentDomain = currentDomain,
            domainBounds = bounds.first,
            currentRange = currentRange,
            rangeBounds = bounds.second,
            graphPoints = graphPoints,
        )
    }
}
