package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.repository.CounterRepository
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.di.IODispatcher
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import javax.inject.Inject

private fun CreateCounterUiState.toCounterOrNull(): Counter? {
    if (anyError) return null
    return Counter(
        name = name.text,
        timeModified = timeModified ?: return null,
        timeCreated = timeCreated ?: return null,
        id = id,
    )
}

/**
 * # Create or Edit a Counter
 * ## provide input feedback and validation; wait for confirmation before saving changes
 *
 * On Init
 * 1. get counter id from [savedStateHandle] using [CreateCounterNode.INIT_COUNTER_ID]
 * 2. use the id to load the initial counter state
 * 3. use the id for all future updates: effectively Edit Mode
 * 4. If id is `null` or the returned counter is `null` or invalid, Create a counter when changes are saved
 *
 * in other words, if provided an id, it will start with those values, and try to save to the same id
 * otherwise it will create a new Counter
 */
@HiltViewModel
class CreateCounterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val counterRepo: CounterRepository,
    @CurrentTime
    private val currentTime: GetTime,
) : ViewModel() {
    private val ioScope: CoroutineScope get() = viewModelScope + ioDispatcher

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _createCounterUiState = savedStateHandle.getStateFlow(
        key = CreateCounterNode.INIT_COUNTER_ID,
        initialValue = savedStateHandle[CreateCounterNode.INIT_COUNTER_ID] ?: NOID
    ).flatMapLatest { counterRepo.getCounterFlow(it) }
        .mapLatest {
            it?.let { mutableCreateCounterUiStateOf(it) } ?: MutableCreateCounterUiState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MutableCreateCounterUiState()
        )

    /**
     * Ui State for creating / editing a counter
     */
    val createCounterUiState: StateFlow<CreateCounterUiState> get() = _createCounterUiState

    /**
     * change the name of the working copy
     *
     * triggers support logic
     */
    fun changeName(name: TextFieldValue) {
        _createCounterUiState.value.name = name
    }

    /**
     * resets counter to blank including its id
     */
    fun discardCounter() {}

    /**
     * Commit Update to Counter or Create Counter if it doesn't exist
     *
     * makes no checks for success, and discards the resulting counter
     */
    fun finalizeCounter() {
        ioScope.launch {
            createCounterUiState.value.let {
                if (it.id == NOID) {
                    val time = currentTime()
                    counterRepo.createCounter(
                        Counter(
                            name = it.name.text,
                            timeModified = time,
                            timeCreated = time,
                            id = NOID,
                        )
                    )
                } else
                    counterRepo.updateCounter(
                        it.toCounterOrNull()?.copy(timeModified = currentTime()) ?: return@launch
                    )
            }
        }
    }
}
