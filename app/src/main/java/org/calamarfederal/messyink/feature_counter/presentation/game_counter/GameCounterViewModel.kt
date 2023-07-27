package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


/**
 * # Game Mode Counter View Model
 * ## Design to fill the purpose of a health counter in Magic the Gathering, or any simple value
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameCounterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ioDispatcher: CoroutineDispatcher,
    private val _getCounterFlow: GetCounterFlow,
    private val _getTicksSumOfFlow: GetTicksSumOfFlow,
    private val _createTick: CreateTick,
    private val _updateCounter: UpdateCounter,
    private val _deleteTicksOf: DeleteTicksOf,
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

    private val ioScope = viewModelScope + SupervisorJob() + ioDispatcher

    private val counterIdState = savedStateHandle.getStateFlow(GameCounterNode.COUNTER_ID, NOID)
    private val tickSortState = MutableStateFlow(TickSort.TimeForData)

    /**
     * counter being examined; idk how to handle when DNE.
     */
    val counter = counterIdState
        .flatMapLatest { _getCounterFlow(it) }
        .mapLatest { it ?: UiCounter(name = "<DNE>", id = NOID) }
        .stateInViewModel(UiCounter(name = "<init>", id = NOID))

    /**
     * Sum all [UiTick] of parent [counter]
     */
    val tickSum = counterIdState.combineTransform(tickSortState) { id, sort ->
        emitAll(_getTicksSumOfFlow(id, sort))
    }.stateInIo(0.00)

    private val _primaryIncrement = MutableStateFlow(5.00)

    /**
     * Quick and usually larger increment
     */
    val primaryIncrement = _primaryIncrement.asStateFlow()

    /**
     * Change primary increment
     */
    fun changePrimaryIncrement(inc: Double) {
        _primaryIncrement.value = inc
    }

    private val _secondaryIncrement = MutableStateFlow(1.00)

    /**
     * Quick and usually smaller increment
     */
    val secondaryIncrement = _secondaryIncrement.asStateFlow()

    /**
     * Change secondary increment
     */
    fun changeSecondaryIncrement(inc: Double) {
        _secondaryIncrement.value = inc
    }

    /**
     * change [counter.name][UiCounter.name] to [name]
     */
    fun onChangeName(name: String) {
        ioScope.launch { _updateCounter(counter.value.copy(name = name)) }
    }

    /**
     * Add new [UiTick] as a child of [counter] with [amount]
     */
    fun addTick(amount: Double) {
        ioScope.launch {
            _createTick(UiTick(amount = amount, parentId = counterIdState.value, id = NOID))
        }
    }

    /**
     * # Not Implemented
     *
     * undo last action / action group / or actions within a time period such that it can be redone
     */
    fun undoTick() {
        TODO("needs to implemented properly, with corresponding redo")
//        ioScope.launch { _undoTicks(parentId = counterIdState.value, duration = 1.minutes) }
    }

    /**
     * # Not Implemented
     *
     * redo last action / action group / or actions within a time period such that it can be redone
     */
    fun redoTick() {
        TODO("needs to implemented properly, with corresponding undo")
    }

    /**
     * Deletes all ticks owned by [counter], then adds on ticks with [amount]
     */
    fun restartCounter(amount: Double = 0.00) {
        ioScope.launch { _deleteTicksOf(counterIdState.value); addTick(amount) }
    }
}
