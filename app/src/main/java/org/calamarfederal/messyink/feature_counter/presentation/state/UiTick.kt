package org.calamarfederal.messyink.feature_counter.presentation.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.Instant

interface UiCheckedText {
    val textState: String
    val isError: Boolean
    val helpText: String?
}

@Stable
class MutableUiCheckedText(
    initialText: String = "",
    private val onChange: (String) -> Pair<Boolean, String?> = { false to null },
) : UiCheckedText {
    override var textState by mutableStateOf(initialText)
    private val checkState by derivedStateOf {
        onChange(textState)
    }

    override val isError get() = checkState.first
    override val helpText get() = checkState.second
}

@Stable
data class UiTickSupport0(
    val amountInput: String = "",
    val amountHelp: String? = null,
    val amountError: Boolean = false,
    val timeForDataInput: Instant = Instant.DISTANT_PAST,
    val timeForDataHelp: String? = null,
    val timeForDataError: Boolean = false,
    val timeModifiedInput: Instant = Instant.DISTANT_PAST,
    val timeModifiedHelp: String? = null,
    val timeModifiedError: Boolean = false,
    val timeCreatedInput: Instant = Instant.DISTANT_PAST,
    val timeCreatedHelp: String? = null,
    val timeCreatedError: Boolean = false,
    val parentId: Long,
    val id: Long? = null,
)
