package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import androidx.compose.runtime.Immutable
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
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.model.NOID

/**
 * Ui State holder for communicating error / validation logic
 *
 * @property[isError]
 * @property[help] directions to fix [isError] or a warning
 */
@Immutable
data class HelpState(
    val help: String? = null,
    val isError: Boolean = !help.isNullOrEmpty(),
)

/**
 * Ui State for Editing Tick
 */
@Stable
interface EditTickUiState {
    /**
     * Current input in the TextField
     */
    val amountInput: TextFieldValue

    /**
     * Error reporting for [amountInput]
     */
    val amountHelpState: HelpState

    /**
     * Chosen time value
     */
    val timeForData: Instant

    /**
     * Error reporting for [timeForData]
     */
    val timeForDataHelpState: HelpState

    /**
     * Chosen time value
     */
    val timeModified: Instant

    /**
     * Error reporting for [timeModified]
     */
    val timeModifiedHelpState: HelpState

    /**
     * Chosen time value
     */
    val timeCreated: Instant

    /**
     * Error reporting for [timeCreated]
     */
    val timeCreatedHelpState: HelpState

    /**
     * Parent Id
     */
    val parentId: Long

    /**
     * Tick Id
     */
    val id: Long
}

/**
 * Check each [HelpState.isError]
 */
val EditTickUiState.anyError: Boolean
    get() = amountHelpState.isError || timeModifiedHelpState.isError || timeCreatedHelpState.isError || timeForDataHelpState.isError

/**
 * Ui State class for editing tick
 */
class MutableEditTickUiState constructor(
    amountHelpState: EditTickUiState.() -> State<HelpState> = {
        derivedStateOf {
            if (amountInput.text.toDoubleOrNull() == null) HelpState("Please enter a valid decimal number")
            else HelpState()
        }
    },
) : EditTickUiState {
    override var amountInput: TextFieldValue by mutableStateOf(TextFieldValue())
    override val amountHelpState: HelpState by amountHelpState()

    override var timeCreated: Instant by mutableStateOf(Instant.DISTANT_PAST)
    override var timeCreatedHelpState: HelpState by mutableStateOf(HelpState())
    override var timeModified: Instant by mutableStateOf(Instant.DISTANT_PAST)
    override var timeModifiedHelpState: HelpState by mutableStateOf(HelpState())
    override var timeForData: Instant by mutableStateOf(Instant.DISTANT_PAST)
    override var timeForDataHelpState: HelpState by mutableStateOf(HelpState())

    override var id: Long by mutableLongStateOf(NOID)
    override var parentId: Long by mutableLongStateOf(NOID)
}

internal fun mutableEditTickUiStateOf(
    tick: Tick,
    mutableState: MutableEditTickUiState = MutableEditTickUiState(),
): MutableEditTickUiState =
    mutableState.apply {
        amountInput = tick.amount.toString().let { TextFieldValue(it, TextRange(0, it.length)) }
        timeCreated = tick.timeCreated
        timeModified = tick.timeModified
        timeForData = tick.timeForData
        id = tick.id
        parentId = tick.parentId
    }
