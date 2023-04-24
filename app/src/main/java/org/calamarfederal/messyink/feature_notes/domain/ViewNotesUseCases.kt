package org.calamarfederal.messyink.feature_notes.domain

import kotlinx.coroutines.flow.Flow
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem

fun interface GetAllNotesFlow {
    operator fun invoke(): Flow<List<UiNoteBrief>>
}

fun interface GetNote {
    suspend operator fun invoke(noteId: Long): UiNoteBrief?
}

fun interface CreateNote {
    suspend operator fun invoke(): UiNoteBrief
}

fun interface ReportNoteChange {
    suspend operator fun invoke(note: UiNoteBrief)
}

fun interface DeleteNote {
    suspend operator fun invoke(note: UiNoteBrief)
}

fun interface DeleteNotes {
    suspend operator fun invoke(notes: Set<UiNoteBrief>)
}

fun interface GetItemsFlow {
    operator fun invoke(noteId: Long): Flow<List<UiNoteItem>>
}

fun interface GetItems {
    suspend operator fun invoke(noteId: Long): List<UiNoteItem>
}

fun interface CreateNoteItem {
    suspend operator fun invoke(noteId: Long): UiNoteItem
}

fun interface ReportItemChange {
    suspend operator fun invoke(item: UiNoteItem)
}

fun interface DeleteItem {
    suspend operator fun invoke(item: UiNoteItem)
}

fun interface DeleteItems {
    suspend operator fun invoke(items: Set<UiNoteItem>)
}

