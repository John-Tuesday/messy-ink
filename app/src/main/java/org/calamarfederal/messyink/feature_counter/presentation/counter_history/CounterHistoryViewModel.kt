package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.common.math.MinMax
import org.calamarfederal.messyink.common.math.include
import org.calamarfederal.messyink.common.math.minMaxOf
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.GetTickSupport
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPoints
import org.calamarfederal.messyink.feature_counter.domain.UpdateTickFromSupport
import org.calamarfederal.messyink.feature_counter.domain.use_case.getTime
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode
import org.calamarfederal.messyink.feature_counter.presentation.state.AllTime
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

/**
 * find the min and max [UiTick.amount] and time (chosen by [sort]) of [ticks]
 */
private fun minMaxOfDomainRange(
    ticks: List<UiTick>,
    sort: TickSort,
): Pair<TimeDomain?, ClosedRange<Double>?> {
    var domain: MinMax<Instant>? = null
    var range: MinMax<Double>? = null
    for (tick in ticks) {
        range = range?.let { it include tick.amount } ?: minMaxOf(tick.amount, tick.amount)
        val time = tick.getTime(sort)
        domain = domain?.let { it include time } ?: minMaxOf(time, time)
    }
    return domain?.let { TimeDomain(it) } to range
}

/**
 * # Counter Details View Model
 *
 * track both summary stats and specific details of [counter]
 */
@HiltViewModel
class CounterHistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ioDispatcher: CoroutineDispatcher,
    @CurrentTime
    private val _currentTime: GetTime,
    private val _getAllTicks: GetTicksOfFlow,
    private val _getTickSupport: GetTickSupport,
    private val _updateTick: UpdateTickFromSupport,
    private val _deleteTick: DeleteTicks,
    private val _ticksToGraphPoints: TicksToGraphPoints,
) : ViewModel() {
    private fun <T> Flow<T>.stateInViewModel(initial: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial,
    )

    private fun <T> Flow<T>.stateInIo(initial: T) = stateIn(
        ioScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial,
    )

    private fun <T> Flow<T>.stateInWork(initial: T) = stateIn(
        workScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial,
    )

    private val workScope: CoroutineScope get() = viewModelScope + SupervisorJob()

    private val ioScope: CoroutineScope get() = viewModelScope + SupervisorJob() + ioDispatcher

    private val counterIdState =
        savedStateHandle.getStateFlow(CounterHistoryNode.COUNTER_ID, NOID)

    private val _tickSortState = MutableStateFlow(TickSort.TimeForData)
    val tickSortState = _tickSortState.asStateFlow()
    private val _timeDomainState =
        MutableStateFlow(TimeDomain(_currentTime().let { it - 1.days .. it }))

    /**
     * User-chosen domain for the graph
     */
    val timeDomainState: StateFlow<TimeDomain> = _timeDomainState.asStateFlow()
    private val _amountRangeState = MutableStateFlow<ClosedRange<Double>>(0.00 .. 1.00)

    /**
     * User-chosen range for the graph
     */
    val amountRangeState = _amountRangeState.asStateFlow()

    private val _editTickSupportState = MutableStateFlow<UiTickSupport?>(null)

    /**
     * Tick in edit-workspace
     */
    val editTickSupportState = _editTickSupportState.asStateFlow()

    /**
     * Unfiltered ticks of chosen counter
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTicksState = combineTransform(_tickSortState, counterIdState) { sort, parentId ->
        emit(_getAllTicks(parentId = parentId, sort = sort))
    }.transformLatest { emitAll(it) }.stateInIo(listOf())

    private val domainRangeLimitsState = combine(allTicksState, _tickSortState) { ticks, sort ->
        minMaxOfDomainRange(ticks, sort)
    }.stateInIo(null)

    /**
     * Actual min and max domain values in [allTicksState]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val domainLimitState =
        domainRangeLimitsState.mapLatest { it?.first ?: TimeDomain.AllTime }
            .stateInViewModel(TimeDomain.AllTime)

    /**
     * Actual min and max domain values in [allTicksState]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val rangeLimitState =
        domainRangeLimitsState.mapLatest { it?.second ?: 0.00 .. 1.00 }
            .stateInViewModel(0.00 .. 1.00)

    private val filteredTicksState = combine(
        allTicksState,
        _tickSortState,
        _timeDomainState,
        _amountRangeState
    ) { ticks, sort, domain, range ->
        ticks.filter { it.getTime(sort) in domain && it.amount in range }
    }.stateInWork(listOf())

    /**
     * Points to be graphed
     */
    val tickGraphPointsState = combine(
        filteredTicksState,
        _tickSortState,
        _timeDomainState,
        _amountRangeState
    ) { ticks, sort, domain, range ->
        _ticksToGraphPoints(filteredTicks = ticks, sort = sort, domain = domain, range = range)
    }.stateInWork(listOf())


    init {
        viewModelScope.launch {
            allTicksState.firstOrNull { it.size > 1 }!! //?: return@launch
            val (domain, range) = domainRangeLimitsState
                .filterNotNull()
                .first { it.first != null && it.second != null }
            changeGraphZoom(domain = domain, range = range)
        }
    }

    /**
     * Request to change domain and/or range
     */
    fun changeGraphZoom(domain: TimeDomain? = null, range: ClosedRange<Double>? = null) {
        _timeDomainState.update { domain ?: it }
        _amountRangeState.update { range ?: it }
    }

    fun changeTickSort(sort: TickSort) {
        _tickSortState.value = sort
    }

    /**
     * Add a tick from the UI
     */
    fun addTick(amount: Double) {
        ioScope.launch {
        }
    }

    /**
     * Set [editTickSupport] to represent the tick with [id]
     */
    fun startTickEdit(id: Long) {
        ioScope.launch {
            _editTickSupportState.update { _getTickSupport(id) }
        }
    }

    /**
     * Update [editTickSupport] and check for errors
     */
    fun updateTickEdit(support: UiTickSupport) {
        _editTickSupportState.update { it?.let { support } }
        val amountError = support.amountInput.toDoubleOrNull() == null
        val amountHelp = if (amountError) "Enter in a valid decimal number" else null
        _editTickSupportState.update {
            it?.copy(
                amountHelp = amountHelp,
                amountError = amountError
            )
        }
    }

    /**
     * Clear [editTickSupport] and do not modify any data
     */
    fun discardTickEdit() {
        _editTickSupportState.value = null
    }

    /**
     * Update corresponding tick. Sets [editTickSupport] to null on success
     */
    fun finalizeTickEdit() {
        ioScope.launch {
            if (_updateTick(_editTickSupportState.value ?: return@launch))
                _editTickSupportState.value = null
        }
    }

    /**
     * Delete tick with matching [id]
     */
    fun deleteTick(id: Long) {
        ioScope.launch { _deleteTick(id) }
    }
}
