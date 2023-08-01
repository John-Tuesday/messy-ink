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
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.TickSort
import org.calamarfederal.messyink.feature_counter.data.repository.CounterRepository
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.di.IODispatcher
import org.calamarfederal.messyink.feature_counter.domain.SimpleCreateTickUseCase
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode
import org.calamarfederal.messyink.feature_counter.data.model.NOID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private fun defaultCounter(time: Instant = Instant.DISTANT_PAST) = Counter(
    name = "DEFAULT",
    timeModified = time,
    timeCreated = time,
    id = NOID,
)

/**
 * # Game Mode Counter View Model
 * ## Design to fill the purpose of a health counter in Magic the Gathering, or any simple value
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameCounterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val counterRepo: CounterRepository,
    private val tickRepo: TickRepository,
    private val _simpleCreateTick: SimpleCreateTickUseCase,
) : ViewModel() {
    private fun <T> Flow<T>.stateInViewModel(initial: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initial,
    )

    private val counterIdState = savedStateHandle.getStateFlow(GameCounterNode.COUNTER_ID, NOID)
    private val tickSortState = MutableStateFlow(TickSort.TimeForData)

    /**
     * counter being examined; idk how to handle when DNE.
     */
    val counter = counterIdState
        .flatMapLatest { counterRepo.getCounterFlow(id = it) }
        .mapLatest { it ?: defaultCounter() }
        .stateInViewModel(defaultCounter())

    /**
     * Sum all Ticks of parent [counter]
     */
    val tickSum = counterIdState.combineTransform(tickSortState) { id, sort ->
        emitAll(tickRepo.getTicksSumOfFlow(parentId = id, timeType = sort))
    }.stateInViewModel(0.00)

    private val _primaryIncrement = MutableStateFlow(5.00)

    /**
     * Quick and usually larger increment
     */
    val primaryIncrement = _primaryIncrement.asStateFlow()

    private val _secondaryIncrement = MutableStateFlow(1.00)

    /**
     * Quick and usually smaller increment
     */
    val secondaryIncrement = _secondaryIncrement.asStateFlow()

    /**
     * Add new Tick as a child of [counter] with [amount]
     */
    fun addTick(amount: Double) {
        viewModelScope.launch {
            _simpleCreateTick(amount = amount, parentId = counterIdState.value)
        }
    }
}
