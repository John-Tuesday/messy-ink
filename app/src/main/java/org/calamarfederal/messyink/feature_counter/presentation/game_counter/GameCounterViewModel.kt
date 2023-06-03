package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.DuplicateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.UndoTicks
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


/**
 * # Game Counter View Model
 *
 * Design to fill the purpose of a health counter in Magic the Gathering, or any simple value
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameCounterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val _getCounterFlow: GetCounterFlow,
    private val _getTicksSumOfFlow: GetTicksSumOfFlow,
    private val _duplicateTick: DuplicateTick,
    private val _updateCounter: UpdateCounter,
    private val _deleteTicksOf: DeleteTicksOf,
    private val _undoTicks: UndoTicks,
) : ViewModel() {
    companion object {
        /**
         * key for navigation argument & saving its counter id
         */
        const val COUNTER_ID = "counter_id"
    }

    private val ioScope = viewModelScope + SupervisorJob() + Dispatchers.IO

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

    private val counterIdState = savedStateHandle.getStateFlow(COUNTER_ID, NOID)

    /**
     * counter being examined; idk how to handle when DNE.
     */
    val counter = counterIdState
        .flatMapLatest { _getCounterFlow(it) }
        .mapLatest { it ?: UiCounter(name = "<DNE>", id = NOID) }
        .stateInViewModel(UiCounter(name = "<init>", id = NOID))

    /**
     * Sum of all [UiTick] with parent [counter]
     */
    val tickSum = counterIdState
        .flatMapLatest { _getTicksSumOfFlow(it) }
        .stateInIo(0.00)

    private val _primaryIncrement = MutableStateFlow(5.00)

    /**
     * Change primary increment
     */
    fun changePrimaryIncrement(inc: Double) {
        _primaryIncrement.value = inc
    }

    /**
     * Quick Larger increment
     */
    val primaryIncrement = _primaryIncrement.asStateFlow()

    private val _secondaryIncrement = MutableStateFlow(1.00)

    /**
     * Change secondary increment
     */
    fun changeSecondaryIncrement(inc: Double) {
        _secondaryIncrement.value = inc
    }

    /**
     * Quick Smaller increment
     */
    val secondaryIncrement = _secondaryIncrement.asStateFlow()

    /**
     * change [counter] name to [name]
     */
    fun onChangeName(name: String) {
        ioScope.launch { _updateCounter(counter.value.copy(name = name)) }
    }

    /**
     * Add new [UiTick] as a child of [counter] with [amount]
     */
    fun addTick(amount: Double) {
        ioScope.launch {
            _duplicateTick(UiTick(amount = amount, parentId = counterIdState.value, id = NOID))
        }
    }

    /**
     * Delete any tick owned by [counter] and modified within
     */
    fun undoTick() {
        ioScope.launch { _undoTicks(parentId = counterIdState.value, duration = 1.minutes) }
    }

    /**
     * UI Callback to redo an undo if possible
     */
    fun redoTick() {
        println("redo")
        TODO("needs use case")
    }

    /**
     * Deletes all ticks owned by [counter]
     */
    fun restartCounter(amount: Double = 0.00) {
        ioScope.launch { _deleteTicksOf(counterIdState.value) }
    }
}
