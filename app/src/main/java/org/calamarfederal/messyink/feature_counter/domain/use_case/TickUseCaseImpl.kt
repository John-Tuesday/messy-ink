package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.SimpleCreateTickUseCase
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPoints
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.math.absoluteValue

class SimpleCreateTickUseCaseImpl @Inject constructor(
    private val repo: TickRepository,
    @CurrentTime
    private val getTime: GetTime,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SimpleCreateTickUseCase {
    override suspend fun invoke(amount: Double, parentId: Long): Tick {
        require(parentId != NOID)
        val time = getTime()
        return withContext(ioDispatcher) {
            repo.createTick(
                Tick(
                    amount = amount,
                    timeModified = time,
                    timeCreated = time,
                    timeForData = time,
                    parentId = parentId,
                    id = NOID,
                )
            )
        }

    }
}

fun UiTick.getTime(sort: TickSort): Instant = when (sort) {
    TickSort.TimeForData  -> timeForData
    TickSort.TimeModified -> timeModified
    TickSort.TimeCreated  -> timeCreated
}

class TicksToGraphPointsImpl @Inject constructor() : TicksToGraphPoints {
    override fun invoke(
        filteredTicks: List<UiTick>,
        sort: TickSort,
        domain: TimeDomain,
        range: ClosedRange<Double>,
    ): List<PointByPercent> {
        val width = (domain.start - domain.end).absoluteValue
        val height = (range.start - range.endInclusive).absoluteValue
        return filteredTicks.map { tick ->
            PointByPercent(
                x = (tick.getTime(sort) - domain.start) / width,
                y = (tick.amount - range.start) / (height)
            )
        }
    }

}
