package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.GetCounterAsSupportOrNull
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterSupport
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport
import javax.inject.Inject

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
    private val ioDispatcher: CoroutineDispatcher,
    private val _updateCounterSupport: UpdateCounterSupport,
    private val _getCounter: GetCounterAsSupportOrNull,
    private val _createCounter: CreateCounterFromSupport,
    private val _updateCounter: UpdateCounterFromSupport,
) : ViewModel() {
    private val ioScope: CoroutineScope get() = viewModelScope + ioDispatcher

    private val _counterSupport = MutableStateFlow(UiCounterSupport())

    /**
     * UI State for current Counter input with errors and support
     */
    val counterSupport: StateFlow<UiCounterSupport> get() = _counterSupport

    init {
        val id: Long? = savedStateHandle[CreateCounterNode.INIT_COUNTER_ID]
        if (id != null && id != NOID)
            ioScope.launch { _getCounter(id)?.let { _counterSupport.value = it } }

        _counterSupport
            .distinctUntilChanged { old, new -> old.nameInput == new.nameInput }
            .conflate()
            .onEach {
                _counterSupport.compareAndSet(it, _updateCounterSupport(it))
            }
            .launchIn(ioScope)
    }

    /**
     * change the name of the working copy
     *
     * triggers support logic
     */
    fun changeName(name: String) {
        _counterSupport.update { it.copy(nameInput = name) }
    }

    /**
     * resets [counterSupport] to blank including its id
     */
    fun discardCounter() {
        _counterSupport.update { UiCounterSupport() }
    }

    /**
     * Commit Update to Counter or Create Counter if it doesn't exist
     *
     * makes no checks for success, and discards the resulting counter
     */
    fun finalizeCounter() {
        ioScope.launch {
            val support = counterSupport.value
            if (support.id == null) _createCounter(support)
            else _updateCounter(support)
        }
    }
}
