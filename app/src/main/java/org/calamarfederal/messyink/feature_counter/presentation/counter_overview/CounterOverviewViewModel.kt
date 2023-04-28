package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion
import kotlinx.coroutines.flow.stateIn
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * # View Model for viewing summary information of all Counters
 */
class CounterOverviewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCountersFlow: GetCountersFlow,
    private val getTicksSumByFlow: GetTicksSumByFlow,
) : ViewModel() {
    private fun <T> Flow<T>.stateInViewModel(initial: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial
    )

    /**
     * State of all counters
     */
    val countersState = getCountersFlow().stateInViewModel(listOf())

    /**
     * State of ticks sum, grouped by [UiTick.parentId]
     */
    val ticksSumState = getTicksSumByFlow().stateInViewModel(mapOf())
}
