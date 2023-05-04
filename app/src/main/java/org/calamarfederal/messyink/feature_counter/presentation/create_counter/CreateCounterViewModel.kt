package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFrom
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import javax.inject.Inject

@HiltViewModel
class CreateCounterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val _createCounterFrom: CreateCounterFrom,
    private val _updateCounter: UpdateCounter,
    private val _deleteCounter: DeleteCounter,
) : ViewModel() {
    private val _counterState = MutableStateFlow(UiCounter(id = NOID))
    val counterState = _counterState.asStateFlow()

    private val ioScope: CoroutineScope = viewModelScope + SupervisorJob()

    init {
        ioScope.launch {
            _counterState.value = _createCounterFrom(UiCounter(id = NOID))
            _counterState.collectLatest { _updateCounter(it) }
        }
    }

    fun changeName(name: String) {
        _counterState.update { it.copy(name = name) }
    }

    fun deleteCounter() {
        viewModelScope.launch { _deleteCounter(_counterState.value.id) }
    }
}
