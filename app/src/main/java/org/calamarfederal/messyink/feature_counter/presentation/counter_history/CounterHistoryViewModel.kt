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
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.common.presentation.compose.charts.PointByPercent
import org.calamarfederal.messyink.common.math.MinMax
import org.calamarfederal.messyink.common.math.minAndMaxOf
import org.calamarfederal.messyink.common.math.minAndMaxOfOrNull
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateTick
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode
import org.calamarfederal.messyink.feature_counter.presentation.state.AllTime
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainAgoTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainLambdaTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
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
    private val _getCounterFlow: GetCounterFlow,
    private val _getTicksOfFlow: GetTicksOfFlow,
    private val _getTicksSumOfFlow: GetTicksSumOfFlow,
    private val _getTicksAverageOfFlow: GetTicksAverageOfFlow,
    private val _createTick: CreateTick,
    private val _updateTick: UpdateTick,
    private val _updateCounter: UpdateCounter,
    private val _deleteTicksOf: DeleteTicksOf,
    private val _deleteTick: DeleteTicks,
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

    private val _graphDomain = MutableStateFlow(TimeDomainAgoTemplate("24hrs ago", 1.days).domain())

    /**
     * Domain for all graphs
     */
    val graphDomain = _graphDomain.asStateFlow()

    /**
     * Maximum limits the domain should ever be
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val graphDomainLimits =
        ticks.mapLatest { ticks ->
            ticks.minAndMaxOfOrNull { it.timeForData }?.let { TimeDomain(it) } ?: TimeDomain.AllTime
        }.stateInViewModel(TimeDomain.AllTime)

    /**
     * All (and only?) quick options to change [graphDomain]
     */
    val graphDomainOptions = listOf(
        TimeDomainAgoTemplate("Day", 1.days),
        TimeDomainLambdaTemplate(
            label = "Fit",
            domainBuilder = { TimeDomain(ticks.value.minAndMaxOf { it.timeForData }) },
        ),
    )

    /**
     * Points to be graphed which represent the current [ticks] within [graphDomain] sorted by [tickSortState]
     */
    val graphPoints: StateFlow<List<PointByPercent>> =
        combine(ticks, graphDomain, tickSortState) { tickList, domain, tickSort ->
            println("domain: ${domain.start} .. ${domain.end}")
            println("tickSort: $tickSort")

            val boundTicks = tickList.filter { it.fromSort(tickSort) in domain }
            val width = (domain.start - domain.end).absoluteValue
            val height =
                boundTicks.minAndMaxOfOrNull { it.amount } ?: MinMax(min = 0.00, max = 1.00)
            boundTicks.map { tick ->
                PointByPercent(
                    x = (tick.fromSort(tickSort) - domain.start) / width,
                    y = (tick.amount - height.min) / (height.max - height.min)
                )
            }
        }.stateInViewModel(listOf())

    init {
        ioScope.launch {
            ticks.drop(1).take(1).collect {
                if (it.isNotEmpty()) changeGraphDomain(graphDomainOptions.last().domain())
            }
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
     * Request change to a Tick from UI
     */
    fun changeTick(tick: UiTick) {
        ioScope.launch { _updateTick(tick) }
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
