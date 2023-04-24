package org.calamarfederal.messyink.feature_notes.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import org.calamarfederal.messyink.feature_notes.presentation.state.UiCheckState
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem

/**
 * # Conversion Ui <---> Data
 */

private fun CheckData.toUi(): UiCheckState = when (this) {
    CheckData.None -> UiCheckState.None
    CheckData.Checked -> UiCheckState.Checked
    CheckData.Unchecked -> UiCheckState.Unchecked
    CheckData.Partial -> UiCheckState.Partial
    CheckData.Default -> UiCheckState.Default
}

private fun UiCheckState.toData(): CheckData = when (this) {
    UiCheckState.None -> CheckData.None
    UiCheckState.Checked -> CheckData.Checked
    UiCheckState.Unchecked -> CheckData.Unchecked
    UiCheckState.Partial -> CheckData.Partial
    UiCheckState.Default -> CheckData.Default
}

private fun NoteData.toUi() = UiNoteBrief(title = title, id = id)
private fun UiNoteBrief.toData() = NoteData(title = title, id = id)
private fun NoteItemData.toUi() =
    UiNoteItem(
        subtitle = subtitle,
        name = name,
        field = field,
        description = description,
        checked = checked.toUi(),
        noteId = noteId,
        id = id
    )

private fun UiNoteItem.toData() =
    NoteItemData(
        subtitle = subtitle,
        name = name,
        field = field,
        description = description,
        checked = checked.toData(),
        noteId = noteId,
        id = id
    )

/**
 * # Implementations
 */

class GetAllNotesFlowImpl(private val repo: NoteDataRepo) : GetAllNotesFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<List<UiNoteBrief>> =
        repo.allNotesFlow().mapLatest { it.map { data -> data.toUi() } }
}

class GetNoteImpl(private val repo: NoteDataRepo) : GetNote {
    override suspend fun invoke(noteId: Long): UiNoteBrief? = repo.getNote(noteId)?.toUi()
}

class CreateNoteImpl(private val repo: NoteDataRepo) : CreateNote {
    override suspend fun invoke(): UiNoteBrief = repo.createNote().toUi()
}

class ReportNoteChangeImpl(private val repo: NoteDataRepo) : ReportNoteChange {
    override suspend fun invoke(note: UiNoteBrief) {
        if (note.title.isNotBlank())
            repo.updateNote(note.toData())
    }
}

class DeleteNoteImpl(private val repo: NoteDataRepo) : DeleteNote {
    override suspend fun invoke(note: UiNoteBrief) = repo.removeNoteById(noteId = note.id)
}

class DeleteNotesImpl(private val repo: NoteDataRepo) : DeleteNotes {
    override suspend fun invoke(notes: Set<UiNoteBrief>) =
        repo.removeNoteSelection(notes.map { it.id })
}

class GetItemsFlowImpl(private val repo: NoteDataRepo) : GetItemsFlow {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(noteId: Long): Flow<List<UiNoteItem>> =
        repo.noteItemsFlow(noteId = noteId).distinctUntilChanged()
            .mapLatest { it.map { d -> d.toUi() } }
}

class GetItemsImpl(private val repo: NoteDataRepo) : GetItems {
    override suspend fun invoke(noteId: Long): List<UiNoteItem> {
        return repo.getItem(noteId).map { it.toUi() }
    }
}

class CreateNoteItemImpl(private val repo: NoteDataRepo) : CreateNoteItem {
    override suspend fun invoke(noteId: Long): UiNoteItem = repo.createItem(noteId = noteId).toUi()
}

class ReportItemChangeImpl(private val repo: NoteDataRepo) : ReportItemChange {
    override suspend fun invoke(item: UiNoteItem) {
        repo.updateItem(item.toData())
    }
}

class DeleteItemImpl(private val repo: NoteDataRepo) : DeleteItem {
    override suspend fun invoke(item: UiNoteItem) = repo.removeItemById(item.id)
}

class DeleteItemsImpl(private val repo: NoteDataRepo) : DeleteItems {
    override suspend fun invoke(items: Set<UiNoteItem>) =
        repo.removeItemSelection(items.map { it.id })
}
