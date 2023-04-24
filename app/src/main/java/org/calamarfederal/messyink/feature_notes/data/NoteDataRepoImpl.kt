package org.calamarfederal.messyink.feature_notes.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import org.calamarfederal.messyink.feature_notes.domain.NoteData
import org.calamarfederal.messyink.feature_notes.domain.NoteItemData
import org.calamarfederal.messyink.data.ViewAllDao
import org.calamarfederal.messyink.feature_notes.domain.NoteDataRepo
import kotlin.random.Random
import org.calamarfederal.messyink.data.entity.Note as DTONote
import org.calamarfederal.messyink.data.entity.NoteItem as DTONoteItem


class NoteDataRepoImpl(private val dao: ViewAllDao, private val random: Random = Random.Default) : NoteDataRepo {
    private fun genId(idPool: Set<Long>): Long {
        var id = random.nextLong()
        while (idPool.contains(id))
            id = random.nextLong()
        return id
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun allNotesFlow(): Flow<List<NoteData>> =
        dao.notes().distinctUntilChanged().mapLatest { it.map { note -> note.toData() } }

    override suspend fun getNote(noteId: Long): NoteData? = dao.getNote(id = noteId)?.toData()

    override suspend fun createNote(): NoteData {
        val pool = buildSet { dao.getNoteIds().forEach { add(it) } }
        var ret = DTONote(title = "", id = genId(pool))

        while (dao.insertNote(ret) == 0L)
            ret = ret.copy(id = genId(pool))

        return ret.toData()
    }

    override suspend fun updateNote(note: NoteData): Boolean = dao.updateNote(note.toDto()) == 1

    override suspend fun removeNoteById(noteId: Long) = dao.removeNote(noteId)
    override suspend fun removeNoteSelection(noteIds: List<Long>) = dao.removeNotes(noteIds)

    /**
     *  Items
     */

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun noteItemsFlow(noteId: Long): Flow<List<NoteItemData>> =
        dao.noteItems(noteId).mapLatest { it.map { item -> item.toNoteItem() } }

    override suspend fun getItem(noteId: Long): List<NoteItemData> =
        dao.getNoteItem(noteId).map { it.toNoteItem() }

    override suspend fun createItem(noteId: Long): NoteItemData {
        val pool = dao.itemIds().toSet()
        var item = DTONoteItem(
            subtitle = "",
            name = "",
            field = "",
            description = "",
            checked = DTONoteItem.CheckState.None,
            noteId = noteId,
            id = genId(pool)
        )

        while (dao.insertNoteItem(item) == 0L)
            item = item.copy(id = genId(pool))

        return item.toNoteItem()
    }

    override suspend fun updateItem(item: NoteItemData): Boolean =
        dao.updateNoteItem(item.toDto()) == 1

    override suspend fun removeItemById(itemId: Long) = dao.removeNoteItem(itemId)

    override suspend fun removeItemSelection(itemIds: List<Long>) = dao.removeNoteItems(itemIds)

}
