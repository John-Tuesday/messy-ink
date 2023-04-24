package org.calamarfederal.messyink.feature_notes.domain

import kotlinx.coroutines.flow.Flow

interface NoteDataRepo {
    companion object

    fun allNotesFlow(): Flow<List<NoteData>>
    suspend fun getNote(noteId: Long): NoteData?
    suspend fun createNote(): NoteData
    suspend fun updateNote(note: NoteData): Boolean
    suspend fun removeNoteById(noteId: Long)
    suspend fun removeNoteSelection(noteIds: List<Long>)

    fun noteItemsFlow(noteId: Long): Flow<List<NoteItemData>>
    suspend fun getItem(noteId: Long): List<NoteItemData>
    suspend fun createItem(noteId: Long): NoteItemData
    suspend fun updateItem(item: NoteItemData): Boolean
    suspend fun removeItemById(itemId: Long)
    suspend fun removeItemSelection(itemIds: List<Long>)
}
