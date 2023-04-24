package org.calamarfederal.messyink.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.calamarfederal.messyink.data.entity.Note
import org.calamarfederal.messyink.data.entity.NoteItem

/**
 * # Return types and meaning
 *
 * ## @Update and @Delete:
 *      Int -> rows affected
 *
 * ## @Insert with one param
 *      Long -> rowId
 * ## @Insert with multiple param
 *      List<Long> -> List<rowId>
 */

@Dao
interface ViewAllDao {

    /**
     * # IDS
     */

    @Query("SELECT id FROM notes")
    suspend fun getNoteIds(): List<Long>

    @Query("SELECT id FROM note_items")
    suspend fun itemIds(): List<Long>

    /**
     * # Note
     */

    @Query("SELECT * FROM notes")
    fun notes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNote(id: Long): Note?

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun removeNote(id: Long)

    @Query("DELETE FROM notes where id in (:ids)")
    suspend fun removeNotes(ids: List<Long>)

    @Update
    suspend fun updateNote(note: Note): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNote(note: Note): Long

    /**
     * # Items
     */

    @Query("SELECT * FROM note_items WHERE note_id = :noteId")
    fun noteItems(noteId: Long): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes JOIN note_items ON notes.id = note_items.note_id")
    fun notesToNoteItems(): Flow<Map<Note, NoteItem>>

    @Query("SELECT * FROM note_items WHERE note_id = :noteId")
    fun getNoteItem(noteId: Long): List<NoteItem>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNoteItem(item: NoteItem): Long

    @Update
    suspend fun updateNoteItem(item: NoteItem): Int

    @Query("DELETE FROM note_items WHERE id = :itemId")
    suspend fun removeNoteItem(itemId: Long)

    @Query("DELETE FROM note_items WHERE id in (:itemIds)")
    suspend fun removeNoteItems(itemIds: List<Long>)
}
