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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.common.math.MinMax
import org.calamarfederal.messyink.common.math.minAndMaxOfOrNull
import org.calamarfederal.messyink.common.math.minMaxOf
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTickSupport
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPoints
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateTickFromSupport
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode
import org.calamarfederal.messyink.feature_counter.presentation.state.AllTime
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainAgoTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

private fun UiTick.fromSort(sort: TickSort.TimeType) = when (sort) {
    TickSort.TimeType.TimeCreated  -> timeCreated
    TickSort.TimeType.TimeModified -> timeModified
    TickSort.TimeType.TimeForData  -> timeForData
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
    private val _getCounterFlow: GetCounterFlow,
    private val _getTicksOfFlow: GetTicksOfFlow,
    private val _getTicksSumOfFlow: GetTicksSumOfFlow,
    private val _getTicksAverageOfFlow: GetTicksAverageOfFlow,
    private val _getTickSupport: GetTickSupport,
    private val _createTick: CreateTick,
    private val _updateTick: UpdateTickFromSupport,
    private val _updateCounter: UpdateCounter,
    private val _deleteTicksOf: DeleteTicksOf,
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

    private val ioScope: CoroutineScope = viewModelScope + SupervisorJob() + ioDispatcher

    private val counterIdState =
        savedStateHandle.getStateFlow(CounterHistoryNode.COUNTER_ID, NOID)

    private val counterSortState = MutableStateFlow(CounterSort.TimeType.TimeCreated)
    private val tickSortState = MutableStateFlow(TickSort.TimeType.TimeForData)

    /**
     * Counter being examined.
     *
     * When no valid [UiCounter] can be found from [CounterHistoryNode.COUNTER_ID], start a new one?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val counter: StateFlow<UiCounter> = counterIdState
        .flatMapLatest { _getCounterFlow(it) }
        .mapLatest { it ?: UiCounter(name = "<deleted>", id = NOID) }
        .stateInViewModel(UiCounter(name = "<none>", id = NOID))

    /**
     * All [UiTick] with [UiTick.parentId] equal to [counter]
     */
    val ticks: StateFlow<List<UiTick>> =
        counterIdState.combineTransform(tickSortState) { id, sort ->
            emitAll(_getTicksOfFlow(id, sort))
        }.stateInViewModel(listOf())

    private val _editTickSupport = MutableStateFlow<UiTickSupport?>(null)

    /**
     * Tick being edited or null if none is being edited
     */
    val editTickSupport = _editTickSupport.asStateFlow()

    /**
     * Sum of [ticks] or `null` when empty
     */
    val tickSum: StateFlow<Double?> =
        counterIdState.combineTransform(tickSortState) { id, sort ->
            emitAll(_getTicksSumOfFlow(id, sort))
        }.stateInViewModel(null)

    /**
     * Average of [ticks] or `null` when empty
     */
    val tickAverage: StateFlow<Double?> =
        counterIdState.combineTransform(tickSortState) { id, sort ->
            emitAll(_getTicksAverageOfFlow(id, sort))
        }.stateInViewModel(null)

    private val _graphDomain =
        MutableStateFlow(TimeDomainAgoTemplate("24hrs ago", 1.days, _currentTime).domain())

    /**
     * Domain for all graphs
     */
    val graphDomain = _graphDomain.asStateFlow()
    private val _graphRange = combine(ticks, graphDomain, tickSortState) { tickList, domain, sort ->
        tickList
            .filter { it.fromSort(sort) in domain }
            .minAndMaxOfOrNull { it.amount }?.let {
                if (it.min == it.max) minMaxOf(it.min, if (it.min == 0.00) 1.00 else 0.00)
                else it
            } ?: minMaxOf(0.00, 1.00)
    }.stateInViewModel(minMaxOf(0.00, 1.00))

    /**
     * Maximum limits the domain should ever be
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val graphDomainLimits = ticks.combine(tickSortState) { ticks, sort ->
        ticks.minAndMaxOfOrNull { it.fromSort(sort) }?.let { TimeDomain(it) } ?: TimeDomain.AllTime
    }.stateInViewModel(TimeDomain.AllTime)

    /**
     * Points to be graphed which represent the current [ticks] within [graphDomain] sorted by [tickSortState]
     */
//    val graphPoints: StateFlow<List<PointByPercent>> =
//        combine(ticks, graphDomain, tickSortState) { tickList, domain, tickSort ->
//            val boundTicks = tickList.filter { it.fromSort(tickSort) in domain }
//            val width = (domain.start - domain.end).absoluteValue
//            val height = boundTicks.minAndMaxOfOrNull { it.amount }?.let {
//                if (it.min == it.max) minMaxOf(it.min, if (it.min == 0.00) 1.00 else 0.00)
//                else it
//            } ?: MinMax(min = 0.00, max = 1.00)
//            boundTicks.map { tick ->
//                PointByPercent(
//                    x = (tick.fromSort(tickSort) - domain.start) / width,
//                    y = (tick.amount - height.min) / (height.max - height.min)
//                )
//            }
//        }.stateInViewModel(listOf())
    val graphPoints: StateFlow<List<PointByPercent>> =
        combine(ticks, graphDomain, _graphRange, tickSortState) { tickList, domain, range, tickSort ->
            _ticksToGraphPoints(ticks = tickList, domain = domain, range = range, sort = tickSort)
        }.stateInViewModel(listOf())

    init {
        ioScope.launch {
            changeGraphDomain(graphDomainLimits.first { it != TimeDomain.AllTime })
        }
    }

    /**
     * UI State holder which determines the Y value minimum and maximum in the graph
     */
    val graphRange: StateFlow<ClosedRange<Double>> = combine(ticks, graphDomain) { ticks, domain ->
        ticks.filter { it.timeForData in domain }.minAndMaxOfOrNull { it.amount } ?: (0.00 .. 10.00)
    }.stateInViewModel(0.00 .. 10.00)

    /**
     * Request to change the domain from the UI
     */
    fun changeGraphDomain(domain: TimeDomain) {
        _graphDomain.value = domain
    }

    /**
     * Add a tick from the UI
     */
    fun addTick(amount: Double) {
        ioScope.launch {
            _createTick(
                UiTick(
                    amount = amount,
                    parentId = counterIdState.value,
                    id = NOID,
                )
            )
        }
    }

    /**
     * Request change to counter from UI
     */
    fun changeCounter(counter: UiCounter) {
        ioScope.launch { _updateCounter(counter) }
    }

    /**
     * Set [editTickSupport] to represent the tick with [id]
     */
    fun startTickEdit(id: Long) {
        ioScope.launch { _editTickSupport.value = _getTickSupport(id) }
    }

    /**
     * Update [editTickSupport] and check for errors
     */
    fun updateTickEdit(support: UiTickSupport) {
        _editTickSupport.update { it?.let { support } }
        val amountError = support.amountInput.toDoubleOrNull() == null
        val amountHelp = if (amountError) "Enter in a valid decimal number" else null
        _editTickSupport.update { it?.copy(amountHelp = amountHelp, amountError = amountError) }
    }

    /**
     * Clear [editTickSupport] and do not modify any data
     */
    fun discardTickEdit() {
        _editTickSupport.value = null
    }

    /**
     * Update corresponding tick. Sets [editTickSupport] to null on success
     */
    fun finalizeTickEdit() {
        ioScope.launch {
            if (_updateTick(_editTickSupport.value ?: return@launch))
                _editTickSupport.value = null
        }
    }

    /**
     * Delete tick with matching [id]
     */
    fun deleteTick(id: Long) {
        ioScope.launch { _deleteTick(id) }
    }

    /**
     * Deletes all ticks
     */
    fun resetCounter() {
        ioScope.launch { _deleteTicksOf(counterIdState.value) }
    }
}
