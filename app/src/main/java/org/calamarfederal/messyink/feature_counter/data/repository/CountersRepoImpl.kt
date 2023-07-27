package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.data.toCounter
import org.calamarfederal.messyink.feature_counter.data.toEntity
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import javax.inject.Inject
import kotlin.random.Random

/**
 * Generate new Id (randomly), not in Pool
 *
 * note: this is a temporary implementation so I don't rely on iterative ids
 */
private fun generateId(pool: Set<Long>, nextRand: () -> Long = { Random.nextLong() }): Long {
    var newId = nextRand()
    while (pool.contains(newId) || newId == NOID) newId = nextRand()
    return newId
}

/**
 * Implement CountersRepo using CounterDao
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CountersRepoImpl @Inject constructor(
    private val dao: CounterTickDao,
    @CurrentTime private val getCurrentTime: GetTime,
) : CountersRepo {
    private suspend fun getCounterIds(): List<Long> = dao.counterIds()

    override fun getCounterFlow(id: Long): Flow<Counter?> =
        dao.counterFlow(id).distinctUntilChanged().mapLatest { it?.toCounter() }

    override fun getCountersFlow(sort: CounterSort.TimeType): Flow<List<Counter>> =
        when (sort) {
            CounterSort.TimeType.TimeCreated  -> dao.countersByCreatedFlow()
            CounterSort.TimeType.TimeModified -> dao.countersByModifiedFlow()
        }.distinctUntilChanged()
            .map { data -> data.map { it.toCounter() } }

    override suspend fun createCounter(counter: Counter): Counter {
        val time = getCurrentTime()
        return counter.copy(
            timeCreated = time,
            timeModified = time,
            id = generateId(pool = getCounterIds().toSet()),
        ).also { dao.insertCounter(it.toEntity()) }
    }


    override suspend fun updateCounter(counter: Counter) =
        0 < dao.updateCounter(counter.copy(timeModified = getCurrentTime()).toEntity())

    override suspend fun deleteCounter(id: Long) = dao.deleteCounter(id)
}
