package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.presentation.navigation.EditTickNode
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import javax.inject.Inject

private fun EditTickUiState.toTick(): Tick? {
    return Tick(
        amount = amountInput.text.toDoubleOrNull() ?: return null,
        timeCreated = timeCreated,
        timeModified = timeModified,
        timeForData = timeForData,
        parentId = parentId,
        id = id,
    )
}

/**
 * Edit Tick View Model
 */
@HiltViewModel
class EditTickViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val tickRepo: TickRepository,
) : ViewModel() {
    private val tickIdState: StateFlow<Long> = savedStateHandle.getStateFlow(
        EditTickNode.TICK_ID,
        savedStateHandle[EditTickNode.TICK_ID] ?: NOID,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _editTickUiState = tickIdState.flatMapLatest {
        tickRepo.getTickFlow(it)
    }.mapLatest {
        it?.let { mutableEditTickUiStateOf(it) } ?: MutableEditTickUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        MutableEditTickUiState(),
    )

    /**
     * Ui State for editing Tick
     */
    val editTickUiState: StateFlow<EditTickUiState> get() = _editTickUiState

    /**
     * Ui callback to change amount input
     */
    fun onAmountChanged(text: TextFieldValue) {
        _editTickUiState.value.amountInput = text
    }

    /**
     * Ui callback to change time for data
     */
    fun onTimeForDataChanged(time: Instant) {
        _editTickUiState.value.timeForData = time
    }

    /**
     * Ui callback to change time modified
     */
    fun onTimeModifiedChanged(time: Instant) {
        _editTickUiState.value.timeModified = time
    }

    /**
     * Ui callback to change time created
     */
    fun onTimeCreatedChanged(time: Instant) {
        _editTickUiState.value.timeCreated = time
    }

    /**
     * Write the changes or do nothing if there is an error
     */
    fun finalizeTick() {
        viewModelScope.launch {
            tickRepo.updateTick(
                editTickUiState.value.toTick() ?: return@launch
            )
        }
    }
}
