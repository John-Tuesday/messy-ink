package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.MockCountersRepo
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.junit.Before
import org.junit.Test

class GetTicksFromTest {
    private lateinit var getTicksOfFlow: GetTicksOfFlow
    private lateinit var repo: MockCountersRepo

    @Before
    fun setUp() {
        repo = MockCountersRepo()
        getTicksOfFlow = GetTicksOfFlowImpl(repo)
    }

    @Test
    fun `Ticks are sorted by sort type TimeForData`() {
        val flow = getTicksOfFlow(parentId = repo.parentId, sort = TickSort.TimeType.TimeForData)
        val ticks = runBlocking { flow.first() }

        assert(ticks.sortedBy { it.timeForData } == ticks)
    }

    @Test
    fun `Ticks are sorted by sort type TimeCreated`() {
        val flow = getTicksOfFlow(parentId = repo.parentId, sort = TickSort.TimeType.TimeCreated)
        val ticks = runBlocking { flow.first() }

        assert(ticks.sortedBy { it.timeCreated } == ticks)
    }

    @Test
    fun `Ticks are sorted by sort type TimeModified`() {
        val flow = getTicksOfFlow(parentId = repo.parentId, sort = TickSort.TimeType.TimeModified)
        val ticks = runBlocking { flow.first() }

        assert(ticks.sortedBy { it.timeModified } == ticks)
    }
}
