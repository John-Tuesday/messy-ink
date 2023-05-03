package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
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
}
