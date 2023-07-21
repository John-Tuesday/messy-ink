@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.singleOrNull
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.CreateCounter
import org.calamarfederal.messyink.feature_counter.domain.GetCounterAsSupportOrNull
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.error
import javax.inject.Inject

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
    override fun invoke(sort: CounterSort.TimeType): Flow<List<UiCounter>> =
        repo.getCountersFlow(sort).mapLatest { it.map { item -> item.toUI() } }
}

/**
 * Default Implementation
 */
class GetCounterAsSupportImpl @Inject constructor(private val repo: CountersRepo) :
    GetCounterAsSupportOrNull {
    override suspend fun invoke(id: Long): UiCounterSupport? =
        repo.getCounterFlow(id).firstOrNull()?.toSupport()
}

/**
 * Default Implementation
 */
class CreateCounterImpl @Inject constructor(private val repo: CountersRepo) : CreateCounter {
    override suspend fun invoke(sample: UiCounter): UiCounter =
        repo.createCounter(sample.copy(id = NOID).toCounter()).toUI()
}

/**
 * Default implementation - relies on [UiCounterSupport.error]
 */
class CreateCounterFromSupportImpl @Inject constructor(private val repo: CountersRepo) :
    CreateCounterFromSupport {
    override suspend fun invoke(support: UiCounterSupport): UiCounter? {
        if (support.error) return null

        return repo.createCounter(
            UiCounter(name = support.nameInput, id = NOID).toCounter()
        ).toUI()
    }
}

/**
 * Default Implementation
 */
class UpdateCounterImpl @Inject constructor(private val repo: CountersRepo) : UpdateCounter {
    override suspend fun invoke(changed: UiCounter) = repo.updateCounter(changed.toCounter())
}

/**
 * Default Implementation
 */
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
class DeleteCounterImpl @Inject constructor(private val repo: CountersRepo) : DeleteCounter {
    override suspend fun invoke(id: Long) = repo.deleteCounter(id)
}
