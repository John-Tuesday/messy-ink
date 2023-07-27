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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.data.model.CounterSort
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepo
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.domain.SimpleCreateTickUseCase
import org.calamarfederal.messyink.feature_counter.domain.use_case.toUI
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
    private val counterRepo: CountersRepo,
    private val tickRepo: TickRepository,
    private val _simpleCreateTick: SimpleCreateTickUseCase,
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

    private val counterSortState = MutableStateFlow(CounterSort.TimeCreated)
    private val tickSortState = MutableStateFlow(TickSort.TimeCreated)

    /**
     * State of all counters
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val countersState =
        counterSortState
            .flatMapLatest { counterRepo.getCountersFlow(it) }
            .mapLatest { it.map { counter -> counter.toUI() } }
            .stateInViewModel(listOf())

    /**
     * State of ticks sum, grouped by [UiTick.parentId]
     */
    val ticksSumState = tickRepo.getTicksSumByFlow().stateInViewModel(mapOf())

    /**
     * Add default increment tick; for no it's just `1.00`
     */
    fun incrementCounter(id: Long) {
        viewModelScope.launch { _simpleCreateTick(amount = 1.00, parentId = id) }
    }

    /**
     * Add default decrement tick; for now it's just `-1.00`
     */
    fun decrementCounter(id: Long) {
        viewModelScope.launch { _simpleCreateTick(amount = -1.00, parentId = id) }
    }

    /**
     * @param[id] valid [UiCounter.id]
     */
    fun deleteCounter(id: Long) {
//        ioScope.launch { _deleteCounter(id) }
        ioScope.launch { counterRepo.deleteCounter(id) }
    }

    /**
     * Delete all [UiTick] with `[UiTick.parentId] == [counterId]`
     */
    fun clearCounterTicks(counterId: Long) {
//        ioScope.launch { _deleteTicksFrom(parentId = counterId) }
        ioScope.launch { tickRepo.deleteTicksOf(counterId) }
    }
}
