package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.R
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
    val amountHelpState: TickAmountHelp

    /**
     * Chosen time value
     */
    val timeForData: Instant

    /**
     * Error reporting for [timeForData]
     */
    val timeForDataHelpState: TickTimeHelp

    /**
     * Chosen time value
     */
    val timeModified: Instant

    /**
     * Error reporting for [timeModified]
     */
    val timeModifiedHelpState: TickTimeHelp

    /**
     * Chosen time value
     */
    val timeCreated: Instant

    /**
     * Error reporting for [timeCreated]
     */
    val timeCreatedHelpState: TickTimeHelp

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
    amountHelpState: EditTickUiState.() -> State<TickAmountHelp> = {
        derivedStateOf {
            if (amountInput.text.isBlank())
                TickAmountHelp.BlankHelp
            else if (amountInput.text.toDoubleOrNull() == null)
                TickAmountHelp.NonNumberHelp
            else
                TickAmountHelp.NoHelp
        }
    },
) : EditTickUiState {
    override var amountInput: TextFieldValue by mutableStateOf(TextFieldValue())
    override val amountHelpState: TickAmountHelp by amountHelpState()

    override var timeCreated: Instant by mutableStateOf(Instant.DISTANT_PAST)
    override var timeCreatedHelpState: TickTimeHelp by mutableStateOf(TickTimeHelp.NoHelp)
    override var timeModified: Instant by mutableStateOf(Instant.DISTANT_PAST)
    override var timeModifiedHelpState: TickTimeHelp by mutableStateOf(TickTimeHelp.NoHelp)
    override var timeForData: Instant by mutableStateOf(Instant.DISTANT_PAST)
    override var timeForDataHelpState: TickTimeHelp by mutableStateOf(TickTimeHelp.NoHelp)

    override var id: Long by mutableLongStateOf(NOID)
    override var parentId: Long by mutableLongStateOf(NOID)
}

/**
 * Feedback and Help for any of Tick's time fields
 *
 * @property[isError] if this should be an Error
 */
enum class TickTimeHelp(val isError: Boolean) {
    /**
     * No error all is fine
     */
    NoHelp(isError = false),

    /**
     * General error
     */
    InvalidHelp(isError = true)
}

/**
 * Get help text through string resources
 */
val TickTimeHelp.help: String?
    @Composable
    get() = when (this) {
        TickTimeHelp.NoHelp      -> null
        TickTimeHelp.InvalidHelp -> stringResource(R.string.unknown_error)
    }

/**
 * Get help text through string resources using [context]
 */
fun TickTimeHelp.getHelp(context: Context): String? = when (this) {
    TickTimeHelp.NoHelp      -> null
    TickTimeHelp.InvalidHelp -> context.getString(R.string.unknown_error)
}

/**
 * Feedback and Help for Tick amount
 *
 * @property[isError] if this should be an Error
 */
enum class TickAmountHelp(val isError: Boolean) {
    /**
     * No error all is fine
     */
    NoHelp(isError = false),

    /**
     * Amount input is blank
     */
    BlankHelp(isError = true),

    /**
     * Amount input cannot be converted to a Double
     */
    NonNumberHelp(isError = true),
}

/**
 * Get help text through string resources
 */
val TickAmountHelp.help: String?
    @Composable
    get() = when (this) {
        TickAmountHelp.NoHelp        -> null
        TickAmountHelp.BlankHelp     -> stringResource(R.string.tick_amount_help_blank)
        TickAmountHelp.NonNumberHelp -> stringResource(R.string.tick_amount_help_non_number)
    }

/**
 * Get help text through string resources using [context]
 */
fun TickAmountHelp.getHelp(context: Context) = when (this) {
    TickAmountHelp.NoHelp        -> null
    TickAmountHelp.BlankHelp     -> context.getString(R.string.tick_amount_help_blank)
    TickAmountHelp.NonNumberHelp -> context.getString(R.string.tick_amount_help_non_number)
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
