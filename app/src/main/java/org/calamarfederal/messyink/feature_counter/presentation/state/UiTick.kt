package org.calamarfederal.messyink.feature_counter.presentation.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.datetime.Instant
import org.w3c.dom.Text

interface UiCheckedValue<T> {
    val value: T
    val isError: Boolean
    val helpText: String?
}

interface UiCheckedString {
    val textState: String
    val isError: Boolean
    val helpText: String?
}

interface UiCheckedTextFieldValue {
    val textState: TextFieldValue
    val isError: Boolean
    val helpText: String?
}

@Stable
class MutableUiCheckedValue<T>(
    initialValue: T,
    private val onChange: (T) -> Pair<Boolean, String?> = { false to null },
) : UiCheckedValue<T> {
    override val value: T by mutableStateOf(initialValue)

    private val checkState by derivedStateOf {
        onChange(value)
    }

    override val isError: Boolean get() = checkState.first
    override val helpText: String? get() = checkState.second
}

@Stable
class MutableUiCheckedString(
    initialText: String = "",
    private val onChange: (String) -> Pair<Boolean, String?> = { false to null },
) : UiCheckedString {
    override var textState by mutableStateOf(initialText)
    private val checkState by derivedStateOf {
        onChange(textState)
    }

    override val isError get() = checkState.first
    override val helpText get() = checkState.second
}

class MutableUiCheckedTextFieldValue(
    initialText: TextFieldValue = TextFieldValue(),
    private val onChange: (TextFieldValue) -> Pair<Boolean, String?> = { false to null },
) : UiCheckedTextFieldValue {
    override var textState by mutableStateOf(initialText)
    private val checkState by derivedStateOf {
        onChange(textState)
    }

    override val isError get() = checkState.first
    override val helpText get() = checkState.second
}

interface UiEditTick {
    val amountCheckedText: UiCheckedTextFieldValue
    val timeForDataChecked: UiCheckedValue<Instant>
    val timeModifiedChecked: UiCheckedValue<Instant>
    val timeCreatedChecked: UiCheckedValue<Instant>
    val parentId: Long
    val id: Long?
}

val UiEditTick.anyError: Boolean
    get() = amountCheckedText.isError || timeForDataChecked.isError || timeModifiedChecked.isError || timeCreatedChecked.isError

@Stable
class MutableUiEditTick(
    override val amountCheckedText: MutableUiCheckedTextFieldValue,
    override val timeForDataChecked: MutableUiCheckedValue<Instant>,
    override val timeModifiedChecked: MutableUiCheckedValue<Instant>,
    override val timeCreatedChecked: MutableUiCheckedValue<Instant>,
    initialParentId: Long,
    initialId: Long? = null,
) : UiEditTick {
    override var id: Long? by mutableStateOf(initialId)
    override var parentId: Long by mutableLongStateOf(initialParentId)

}

//@Stable
//data class UiTickSupport0(
//    val amountInput: String = "",
//    val amountHelp: String? = null,
//    val amountError: Boolean = false,
//    val timeForDataInput: Instant = Instant.DISTANT_PAST,
//    val timeForDataHelp: String? = null,
//    val timeForDataError: Boolean = false,
//    val timeModifiedInput: Instant = Instant.DISTANT_PAST,
//    val timeModifiedHelp: String? = null,
//    val timeModifiedError: Boolean = false,
//    val timeCreatedInput: Instant = Instant.DISTANT_PAST,
//    val timeCreatedHelp: String? = null,
//    val timeCreatedError: Boolean = false,
//    val parentId: Long,
//    val id: Long? = null,
//)
