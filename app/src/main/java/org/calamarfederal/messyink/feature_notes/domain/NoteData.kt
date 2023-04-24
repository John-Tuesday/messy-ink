package org.calamarfederal.messyink.feature_notes.domain

enum class CheckData {
    None, Checked, Unchecked, Partial, Default
}

data class NoteData(
    val title: String,
    val id: Long,
) {
    companion object
}

data class NoteItemData(
    val subtitle: String,
    val name: String,
    val field: String,
    val description: String,
    val checked: CheckData,
    val noteId: Long,
    val id: Long,
) {
    companion object
}
