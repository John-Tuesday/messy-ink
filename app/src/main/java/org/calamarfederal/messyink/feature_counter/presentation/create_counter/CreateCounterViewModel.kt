package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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

    /**
     * Tentative Name of the counter
     */
    var counterName by mutableStateOf(TextFieldValue(text = ""))
        private set

    /**
     * True if there are any errors in name preventing save
     */
    val counterNameError by derivedStateOf {
        counterName.text.isBlank()
    }

    /**
     * Instructions on how to correct [counterNameError] or any relevant warning
     */
    val counterHelp by derivedStateOf {
        if (counterName.text.isBlank()) "Please enter non-whitespace text" else null
    }

    init {
        val id: Long? = savedStateHandle[CreateCounterNode.INIT_COUNTER_ID]
        if (id != null && id != NOID)
            ioScope.launch {
                _getCounter(id)?.let {
                    counterName = TextFieldValue(
                        text = it.nameInput,
                        selection = TextRange(0, it.nameInput.length)
                    )
                } ?: println("Could not find counter")
            }.also { println("Found Id: $id") }
        else
            println("Did not find id")
    }

    /**
     * change the name of the working copy
     *
     * triggers support logic
     */
    fun changeName(name: TextFieldValue) {
        counterName = name
    }

    /**
     * resets counter to blank including its id
     */
    fun discardCounter() {
        counterName = TextFieldValue()
        savedStateHandle[CreateCounterNode.INIT_COUNTER_ID] = null
    }

    /**
     * Commit Update to Counter or Create Counter if it doesn't exist
     *
     * makes no checks for success, and discards the resulting counter
     */
    fun finalizeCounter() {
        ioScope.launch {
            val support = UiCounterSupport(
                nameInput = counterName.text,
                nameError = counterNameError,
                nameHelp = counterHelp,
                id = savedStateHandle.get<Long?>(CreateCounterNode.INIT_COUNTER_ID)
                    ?.let { it: Long -> if (it == NOID) null else it }
            )
            if (support.id == null) _createCounter(support)
            else _updateCounter(support)
        }
    }
}
