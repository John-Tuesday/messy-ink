package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.domain.TickSort.TimeType
import kotlin.time.Duration.Companion.days

class MockCountersRepo(
    private val seedTime: Instant = Instant.fromEpochMilliseconds(0L),
    /**
     * Parent Id of all the test Tick
     */
    val parentId: Long = 1L,
    private val amount: Double = 1.00,
) : CountersRepoStub() {
    val testUiTicks = (1 .. 100).map {
        val time = seedTime + it.days
        Tick(
            amount = amount,
            timeForData = time + it.days,
            timeModified = time + (2 * it).days,
            timeCreated = time + (3 * it).days,
            id = it.toLong(),
            parentId = parentId,
        )
    }

    override fun getTicksFlow(parentId: Long, sort: TimeType): Flow<List<Tick>> {
        return flowOf(testUiTicks)
    }
}
