package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.CounterSort
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.TickSort
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * # [UiCounter] Overview View Model
 * ## provide summary details and simple actions
 */
@HiltViewModel
class CounterOverviewViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val _getCountersFlow: GetCountersFlow,
    private val _getTicksSumByFlow: GetTicksSumByFlow,
    private val _createTick: CreateTick,
    private val _deleteCounter: DeleteCounter,
    private val _deleteTicksFrom: DeleteTicksOf,
) : ViewModel() {
    private fun <T> Flow<T>.stateInViewModel(initial: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial
    )

    private fun <T> Flow<T>.stateInIo(initial: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial
    )

    private val ioScope: CoroutineScope get() = viewModelScope + SupervisorJob() + ioDispatcher

    private val counterSortState = MutableStateFlow(CounterSort.TimeType.TimeCreated)
    private val tickSortState = MutableStateFlow(TickSort.TimeType.TimeCreated)

    /**
     * State of all counters
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val countersState =
        counterSortState
            .flatMapLatest { _getCountersFlow(it) }
            .stateInViewModel(listOf())

    /**
     * State of ticks sum, grouped by [UiTick.parentId]
     */
    val ticksSumState = _getTicksSumByFlow().stateInViewModel(mapOf())

    /**
     * Add default increment tick; for no it's just `1.00`
     */
    fun incrementCounter(id: Long) {
        ioScope.launch { _createTick(UiTick(amount = 1.00, parentId = id, id = NOID)) }
    }

    /**
     * Add default decrement tick; for now it's just `-1.00`
     */
    fun decrementCounter(id: Long) {
        ioScope.launch { _createTick(UiTick(amount = -1.00, parentId = id, id = NOID)) }
    }

    /**
     * @param[id] valid [UiCounter.id]
     */
    fun deleteCounter(id: Long) {
        ioScope.launch { _deleteCounter(id) }
    }

    /**
     * Delete all [UiTick] with `[UiTick.parentId] == [counterId]`
     */
    fun clearCounterTicks(counterId: Long) {
        ioScope.launch { _deleteTicksFrom(parentId = counterId) }
    }
}
