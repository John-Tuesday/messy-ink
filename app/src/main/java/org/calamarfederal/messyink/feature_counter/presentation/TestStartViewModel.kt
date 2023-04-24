package org.calamarfederal.messyink.feature_counter.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFrom
import org.calamarfederal.messyink.feature_counter.domain.CreateTickFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TestStartViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCountersFlow: GetCountersFlow,
    private val getTicksFlow: GetTicksOfFlow,
    private val getSumOfTicksByFlow: GetTicksSumByFlow,
    private val createCounterFrom: CreateCounterFrom,
    private val createTickFrom: CreateTickFrom
) : ViewModel() {
    private fun <T> Flow<T>.stateInViewModel(initial: T): StateFlow<T> =
        stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initial,
        )

    /**
     * # State holders
     *
     */

    val countersState = getCountersFlow().stateInViewModel(listOf())
    val tickSumByCounterId = getSumOfTicksByFlow().stateInViewModel(mapOf())

    /**
     * # Ui Callbacks
     */
    fun testCreateCounter(sample: UiCounter = UiCounter(name = "lol", id = NOID)) {
        viewModelScope.launch {
            createCounterFrom(sample)
        }
    }

    fun testAddTick(sample: UiTick) {
        viewModelScope.launch {
            createTickFrom(sample)
        }
    }
}