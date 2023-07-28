package org.calamarfederal.messyink.feature_counter.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.CounterSort
import org.calamarfederal.messyink.feature_counter.data.source.CounterLocalSource
import org.calamarfederal.messyink.feature_counter.data.model.NOID
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
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

internal fun Counter.toEntity() = CounterEntity(
    name = name,
    timeCreated = timeCreated,
    timeModified = timeModified,
    id = id,
)

internal fun CounterEntity.toCounter() = Counter(
    name = name,
    timeCreated = timeCreated,
    timeModified = timeModified,
    id = id,
)

/**
 * Implement CountersRepo using CounterDao
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CounterRepositoryImpl @Inject constructor(
    private val dao: CounterLocalSource,
) : CounterRepository {
    private suspend fun getCounterIds(): List<Long> = dao.counterIds()

    override fun getCounterFlow(id: Long): Flow<Counter?> =
        dao.counterFlow(id).distinctUntilChanged().mapLatest { it?.toCounter() }

    override fun getCountersFlow(sort: CounterSort): Flow<List<Counter>> =
        when (sort) {
            CounterSort.TimeCreated  -> dao.countersByCreatedFlow()
            CounterSort.TimeModified -> dao.countersByModifiedFlow()
        }.distinctUntilChanged()
            .map { data -> data.map { it.toCounter() } }

    override suspend fun createCounter(counter: Counter): Counter =
        counter.copy(id = generateId(pool = getCounterIds().toSet()))
            .also { dao.insertCounter(it.toEntity()) }


    override suspend fun updateCounter(counter: Counter) =
        0 < dao.updateCounter(counter.toEntity())

    override suspend fun deleteCounter(id: Long) = dao.deleteCounter(id)
}
