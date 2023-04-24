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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFrom
import org.calamarfederal.messyink.feature_counter.domain.CreateTickFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameCounterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCounterFlow: GetCounterFlow,
    private val getTicksSumOfFlow: GetTicksSumOfFlow,
    private val createCounterFrom: CreateCounterFrom,
    private val createTickFrom: CreateTickFrom,
) : ViewModel() {
    companion object {
        const val COUNTER_ID_KEY = "counter_id"
    }

    private val ioScope = viewModelScope + SupervisorJob() + Dispatchers.IO

    private fun <T> Flow<T>.stateInViewModel(initial: T) = stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), initial
    )

    private fun <T> Flow<T>.stateInIo(initial: T) = stateIn(
        ioScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), initial
    )

    val counter = getCounterFlow(savedStateHandle.get<Long>(COUNTER_ID_KEY) ?: NOID)
        .mapLatest {
            it ?: createCounterFrom(UiCounter(id = NOID)).also { spawned ->
                savedStateHandle[COUNTER_ID_KEY] = spawned.id
            }
        }
        .stateInIo(UiCounter(name = "init", id = NOID))

    val tickSum = counter.distinctUntilChanged { old, new -> old.id == new.id }
        .flatMapLatest { getTicksSumOfFlow(it.id) }.stateInIo(0.00)

    fun onChangeName(name: String) {
        TODO("need use case and ui implementation")
    }

    fun addTick(amount: Double) {
        ioScope.launch {
            createTickFrom(UiTick(amount = amount, parentId = counter.value.id, id = NOID))
        }
    }

    fun undoTick() {
        println("undo")
        TODO("needs use case")
    }

    fun redoTick() {
        println("redo")
        TODO("needs use case")
    }

    fun restartCounter(amount: Double = 0.00) {
        println("reset")
        TODO("needs use case")
    }
}
