package org.calamarfederal.messyink.feature_notes.presentation.state

import androidx.compose.runtime.Stable

@Stable
enum class UiCheckState {
    None, Checked, Unchecked, Partial, Default
}

@Stable
data class UiNoteBrief(
    val title: String = "",
    val id: Long,
) {
    companion object
}

@Stable
data class UiNoteItem(
    val subtitle: String = "",
    val name: String = "",
    val field: String = "",
    val description: String = "",
    val checked: UiCheckState = UiCheckState.None,
    val noteId: Long,
    val id: Long,
) {
    companion object
}

private fun UiNoteItem.Companion.selfLabeled(
    noteId: Long,
    id: Long,
    checked: UiCheckState = UiCheckState.Default,
    suffix: String = " ",
) = UiNoteItem(
    subtitle = "subtitle$suffix",
    name = "name$suffix",
    field = "field$suffix",
    description = "description$suffix",
    checked = checked,
    noteId = noteId,
    id = id,
)

private val loopPositiveInts get() = generateSequence(1) { it.inc() % Int.MAX_VALUE.dec() }

fun uiNoteItemPreviews(noteId: Long = 1L): Sequence<UiNoteItem> =
    generateSequence(1L) { it.inc() % Int.MAX_VALUE.dec() }.map {
        UiNoteItem.selfLabeled(noteId = noteId, id = it)
    }

val uiNoteBriefPreviews: Sequence<UiNoteBrief>
    get() = generateSequence(1L) { it.inc() % Int.MAX_VALUE.dec() }.map {
        UiNoteBrief(title = "Title: $it", id = it)
    }
