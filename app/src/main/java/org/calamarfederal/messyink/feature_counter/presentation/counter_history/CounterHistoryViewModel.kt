package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.NOID
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.TickGraphRepository
import org.calamarfederal.messyink.feature_counter.data.repository.TickGraphState
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * # Counter Details View Model
 *
 * track both summary stats and specific details of counter and its ticks
 */
@HiltViewModel
class CounterHistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val tickRepo: TickRepository,
    private val tickGraphRepository: TickGraphRepository,
) : ViewModel() {
    private fun <T> Flow<T>.stateInViewModel(initial: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial,
    )

    private val counterIdState =
        savedStateHandle.getStateFlow(CounterHistoryNode.COUNTER_ID, NOID)

    private val _tickSortState = MutableStateFlow(TickSort.TimeForData)

    /**
     * Order which ticks are sorted before any operation
     */
    val tickSortState = _tickSortState.asStateFlow()

    private val _chosenDomainState = MutableStateFlow<ClosedRange<Instant>?>(null)
    private val chosenDomainState = _chosenDomainState.asStateFlow()

    private val _chosenRangeState = MutableStateFlow<ClosedRange<Double>?>(null)
    private val chosenRangeState = _chosenRangeState.asStateFlow()

    /**
     * Prepped graph points from ticks
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val tickGraphState: StateFlow<TickGraphState> = combine(
        counterIdState,
        tickSortState,
        chosenDomainState,
        chosenRangeState
    ) { counterId, sort, domain, range ->
        tickGraphRepository.getGraphPointsFlow(
            counterId = counterId,
            sort = sort,
            domain = domain,
            range = range,
        )
    }.flatMapLatest { it }
        .stateInViewModel(TickGraphState())

    /**
     * All ticks, sorted; for tickLog
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTicksState = combine(counterIdState, tickSortState) { counterId, sort ->
        tickRepo.getTicksFlow(parentId = counterId, sort = sort)
    }.flatMapLatest { it }
        .stateInViewModel(listOf())

    /**
     * Change the graph viewport domain (width) and range (height)
     */
    fun changeGraphZoom(domain: TimeDomain? = null, range: ClosedRange<Double>? = null) {
        _chosenDomainState.update { domain?.run { start .. end } }
        _chosenRangeState.update { range }
    }

    /**
     * Change the [tickSortState]
     */
    fun changeTickSort(sort: TickSort) {
        _tickSortState.value = sort
    }

    /**
     * Delete tick with matching [id]
     */
    fun deleteTick(id: Long) {
        viewModelScope.launch { tickRepo.deleteTick(id) }
    }
}
