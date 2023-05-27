package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.CreateTickFrom
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateTick
import org.calamarfederal.messyink.feature_counter.presentation.state.AbsoluteAllTime
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomain
import org.calamarfederal.messyink.feature_counter.presentation.state.TimeDomainTemplate
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/**
 * # Counter Details View Model
 *
 * track both summary stats and specific details of [counter]
 */
@HiltViewModel
class CounterDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val _getCounterFlow: GetCounterFlow,
    private val _getTicksOfFlow: GetTicksOfFlow,
    private val _getTicksSumOfFlow: GetTicksSumOfFlow,
    private val _getTicksAverageOfFlow: GetTicksAverageOfFlow,
    private val _createTickFrom: CreateTickFrom,
    private val _updateTick: UpdateTick,
    private val _updateCounter: UpdateCounter,
    private val _deleteTicksOf: DeleteTicksOf,
    private val _deleteTick: DeleteTicks,
) : ViewModel() {
    companion object {
        /**
         * Key for getting and setting [counter.value.id] in [savedStateHandle]
         */
        const val COUNTER_ID = "counter_id"
    }

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

    private val ioScope: CoroutineScope = viewModelScope + SupervisorJob()

    private val counterIdState = savedStateHandle.getStateFlow(COUNTER_ID, NOID)

    /**
     * Counter being examined.
     *
     * When no valid [UiCounter] can be found from [COUNTER_ID], start a new one?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val counter: StateFlow<UiCounter> = counterIdState
        .flatMapLatest { _getCounterFlow(it) }
        .mapLatest { it ?: UiCounter(name = "<deleted>", id = NOID) }
        .stateInViewModel(UiCounter(name = "<none>", id = NOID))

    /**
     * All [UiTick] with [UiTick.parentId] equal to [counter]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val ticks: StateFlow<List<UiTick>> = counterIdState
        .flatMapLatest { _getTicksOfFlow(it) }
        .stateInViewModel(listOf())

    /**
     * Sum of [ticks] or `null` when empty
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val tickSum: StateFlow<Double?> = counterIdState
        .flatMapLatest { _getTicksSumOfFlow(it) }
        .stateInViewModel(null)

    /**
     * Average of [ticks] or `null` when empty
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val tickAverage: StateFlow<Double?> = counterIdState
        .flatMapLatest { _getTicksAverageOfFlow(it) }
        .stateInViewModel(null)

    private val _graphDomain = MutableStateFlow(TimeDomain.AbsoluteAllTime)

    /**
     * Domain for all graphs
     */
    val graphDomain = _graphDomain.asStateFlow()

    /**
     * All (and only?) quick options to change [graphDomain]
     */
    val graphDomainOptions = listOf(
        TimeDomainTemplate.YearAgo,
        TimeDomainTemplate.MonthAgo,
        TimeDomainTemplate.WeekAgo,
        TimeDomainTemplate.DayAgo,
        TimeDomainTemplate("6 Hours ago", 6.hours),
        TimeDomainTemplate.HourAgo,
    )

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
            _createTickFrom(
                UiTick(
                    amount = amount,
                    parentId = counterIdState.value,
                    id = NOID
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
