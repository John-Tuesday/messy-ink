package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
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
    private val _getTicksSumByFlow: GetTicksSumByFlow,
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
        useCaseScope.launch { _deleteTicksFrom(parentId =  counterId) }
    }
}
