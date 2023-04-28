package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * # View Model for viewing summary information of all Counters
 */
@HiltViewModel
class CounterOverviewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val _getCountersFlow: GetCountersFlow,
    private val _getCounterFlow: GetCounterFlow,
    private val _getTicksSumByFlow: GetTicksSumByFlow,
    private val _getTicksOfFlow: GetTicksOfFlow,
    private val _deleteCounter: DeleteCounter,
    private val _deleteTicksFrom: DeleteTicksFrom,
) : ViewModel() {
    private fun <T> Flow<T>.stateInViewModel(initial: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial
    )

    private val useCaseScope: CoroutineScope get() = viewModelScope + SupervisorJob()

    /**
     * State of all counters
     */
    val countersState = _getCountersFlow().stateInViewModel(listOf())

    /**
     * State of ticks sum, grouped by [UiTick.parentId]
     */
    val ticksSumState = _getTicksSumByFlow().stateInViewModel(mapOf())

    private val _selectedCounterId = MutableStateFlow<Long?>(null)
    val selectedCounter = countersState.combine(_selectedCounterId) {counters, selectId ->
        counters.find{ it.id == selectId }
    }.stateInViewModel(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ticksOfSelectedCounter = selectedCounter
        .distinctUntilChanged { old, new -> old?.id == new?.id }
        .flatMapLatest { if (it != null) _getTicksOfFlow(it.id) else flowOf(null) }
        .stateInViewModel(initial = null)

    init {
//        countersState.onEach { counters ->
//            _selectedCounter.value = counters.find { it.id == _selectedCounter.value?.id }
//        }.launchIn(useCaseScope)
    }

    fun selectCounter(counter: UiCounter) {
        _selectedCounterId.value = counter.id
    }

    /**
     * @param[id] valid [UiCounter.id]
     */
    fun deleteCounter(id: Long) {
        useCaseScope.launch { _deleteCounter(id) }
    }

    /**
     * Delete all ticks owned by [counterId]
     *
     * @param[counterId] valid [UiCounter.id]
     */
    fun clearCounterTicks(counterId: Long) {
        useCaseScope.launch { _deleteTicksFrom(parentId = counterId) }
    }
}
