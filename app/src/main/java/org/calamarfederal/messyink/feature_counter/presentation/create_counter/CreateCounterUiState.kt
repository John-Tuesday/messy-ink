package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.data.model.Counter
import org.calamarfederal.messyink.feature_counter.data.model.NOID
import org.calamarfederal.messyink.feature_counter.presentation.tick_edit.HelpState

/**
 * Ui State Holder for creating / editing counter
 */
@Stable
interface CreateCounterUiState {
    /**
     * Counter name input
     */
    val name: TextFieldValue

    /**
     * Counter name input feedback
     */
    val nameHelpState: HelpState

    /**
     * when editing, this will not be null
     */
    val timeCreated: Instant?

    /**
     * when editing, this will not be null
     */
    val timeModified: Instant?

    /**
     * id of counter or [NOID]
     */
    val id: Long
}

/**
 * True when any [HelpState.isError] is true (ignores null)
 */
val CreateCounterUiState.anyError: Boolean
    get() = nameHelpState.isError

/**
 * Mutable Create Counter Ui State
 */
class MutableCreateCounterUiState(
    nameHelpStateGetter: CreateCounterUiState.() -> State<HelpState> = {
        derivedStateOf {
            if (name.text.isBlank())
                HelpState("Please enter a name with non-whitespace characters")
            else
                HelpState()
        }
    },
) : CreateCounterUiState {
    override var name: TextFieldValue by mutableStateOf(TextFieldValue(""))
    override val nameHelpState: HelpState by nameHelpStateGetter()
    override var timeCreated: Instant? by mutableStateOf(null)
    override var timeModified: Instant? by mutableStateOf(null)
    override var id: Long by mutableLongStateOf(NOID)
}

/**
 * change fields in [mutableState] to match [counter]
 */
fun mutableCreateCounterUiStateOf(
    counter: Counter,
    mutableState: MutableCreateCounterUiState = MutableCreateCounterUiState(),
): MutableCreateCounterUiState = mutableState.apply {
    name = TextFieldValue(text = counter.name, selection = TextRange(0, counter.name.length))
    timeCreated = counter.timeCreated
    timeModified = counter.timeCreated
    id = counter.id
}
