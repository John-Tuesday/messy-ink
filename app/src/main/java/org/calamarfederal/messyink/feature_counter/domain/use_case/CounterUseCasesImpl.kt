@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.DuplicateCounter
import org.calamarfederal.messyink.feature_counter.domain.DuplicateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksFrom
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCounterAsSupportOrNull
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.UndoTicks
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterSupport
import org.calamarfederal.messyink.feature_counter.domain.UpdateTick
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.error
import javax.inject.Inject
import kotlin.time.Duration

/**
 * Default Implementation
 */
class GetCounterFlowImpl @Inject constructor(private val repo: CountersRepo) : GetCounterFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(id: Long): Flow<UiCounter?> =
        repo.getCounterFlow(id).mapLatest { it?.toUI() }
}

/**
 *  Default Implementation
 */
class GetCountersFlowImpl @Inject constructor(private val repo: CountersRepo) : GetCountersFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<List<UiCounter>> =
        repo.getCountersFlow().mapLatest { it.map { item -> item.toUI() } }
}

class GetCounterAsSupportImpl @Inject constructor(private val repo: CountersRepo) :
    GetCounterAsSupportOrNull {
    override suspend fun invoke(id: Long): UiCounterSupport? = repo.getCounterOrNull(id)?.let {
        UiCounterSupport(
            nameInput = it.name,
            id = it.id,
            nameHelp = null,
            nameError = false,
        )
    }
}

/**
 * Default Implementation
 */
class GetTicksOfFlowImpl @Inject constructor(private val repo: CountersRepo) : GetTicksOfFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(parentId: Long): Flow<List<UiTick>> =
        repo.getTicksFlow(parentId = parentId).mapLatest { it.map { item -> item.toUi() } }
}

/**
 * Default Implementation
 */
class DuplicateCounterImpl @Inject constructor(private val repo: CountersRepo) : DuplicateCounter {
    override suspend fun invoke(sample: UiCounter): UiCounter =
        repo.duplicateCounter(sample.copy(id = NOID).toCounter()).toUI()
}

/**
 * Default implementation - relies on [UiCounterSupport.error]
 */
class CreateCounterFromSupportImpl @Inject constructor(private val repo: CountersRepo) :
    CreateCounterFromSupport {
    override suspend fun invoke(support: UiCounterSupport): UiCounter? {
        if (support.error) return null

        return repo.duplicateCounter(
            UiCounter(name = support.nameInput, id = NOID).toCounter()
        ).toUI()
    }
}

/**
 * Default Implementation
 */
class DuplicateTickImpl @Inject constructor(private val repo: CountersRepo) : DuplicateTick {
    override suspend fun invoke(sample: UiTick): UiTick {
        require(sample.parentId != NOID)
        return repo.duplicateTick(sample.copy(id = NOID).toTick()).toUi()
    }
}

/**
 * Default Implementation
 */
class UpdateCounterImpl @Inject constructor(private val repo: CountersRepo) : UpdateCounter {
    override suspend fun invoke(changed: UiCounter) = repo.updateCounter(changed.toCounter())
}

class UpdateCounterFromSupportImpl @Inject constructor(private val repo: CountersRepo) :
    UpdateCounterFromSupport {
    override suspend fun invoke(support: UiCounterSupport): Boolean {
        if (support.error || support.id == null) return false

        return repo.updateCounter(UiCounter(name = support.nameInput, id = support.id).toCounter())
    }
}

/**
 * Default Implementation
 */
class UpdateCounterSupportImpl @Inject constructor(private val repo: CountersRepo) :
    UpdateCounterSupport {
    override suspend fun invoke(changed: UiCounterSupport): UiCounterSupport {

        return changed.copy(
            nameHelp = if (changed.nameInput.isBlank()) "Add a non-whitespace character" else null,
            nameError = changed.nameInput.isBlank(),
        )
    }
}

/**
 * Default Implementation
 */
class UndoTicksImpl @Inject constructor(
    private val repo: CountersRepo,
    @CurrentTime private val getTime: GetTime,
) : UndoTicks {
    override suspend fun invoke(parentId: Long, limit: Int?, duration: Duration) =
        repo.deleteTicksByTimeModified(
            parentId = parentId,
            limit = limit,
            start = getTime() - duration,
            end = Instant.DISTANT_FUTURE
        )
}

/**
 * Default Implementation
 */
class UpdateTickImpl @Inject constructor(private val repo: CountersRepo) : UpdateTick {
    override suspend fun invoke(changed: UiTick) = repo.updateTick(changed.toTick())
}

/**
 * Default Implementation
 */
class DeleteCounterImpl @Inject constructor(private val repo: CountersRepo) : DeleteCounter {
    override suspend fun invoke(id: Long) = repo.deleteCounter(id)
}

/**
 * Default Implementation
 */
class DeleteTicksImpl @Inject constructor(private val repo: CountersRepo) : DeleteTicks {
    override suspend fun invoke(ids: List<Long>) = repo.deleteTicks(ids)
    override suspend fun invoke(id: Long) = repo.deleteTick(id)
}

/**
 * Default Implementation
 */
class DeleteTicksOfImpl @Inject constructor(private val repo: CountersRepo) : DeleteTicksOf {
    override suspend fun invoke(parentId: Long) = repo.deleteTicksOf(parentId)
}

/**
 * Default Implementation
 */
class DeleteTicksFromImpl @Inject constructor(private val repo: CountersRepo) : DeleteTicksFrom {
    override suspend fun invoke(parentId: Long, start: Instant, end: Instant) =
        repo.deleteTicksByTimeForData(parentId = parentId, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksSumOfFlowImpl @Inject constructor(private val repo: CountersRepo) :
    GetTicksSumOfFlow {
    override fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        repo.getTicksSumOfFlow(parentId = parentId, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksAverageOfFlowImpl @Inject constructor(private val repo: CountersRepo) :
    GetTicksAverageOfFlow {
    override fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double> =
        repo.getTicksAverageOfFlow(parentId = parentId, start = start, end = end)
}

/**
 * Default Implementation
 */
class GetTicksSumByFlowImpl @Inject constructor(private val repo: CountersRepo) :
    GetTicksSumByFlow {
    override fun invoke(): Flow<Map<Long, Double>> = repo.getTicksSumByFlow()
}
