package org.calamarfederal.messyink.data.entity

import androidx.room.*

@Entity(tableName = "notes")
data class Note(
    @ColumnInfo(index = true) val title: String,
    @PrimaryKey val id: Long,
)

@Entity(
    tableName = "note_items",
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("note_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class NoteItem(
    val subtitle: String,
    val name: String,
    val field: String,
    val description: String,
    val checked: CheckState,
    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,
    @PrimaryKey
    val id: Long,
) {
    enum class CheckState {
        None, Checked, Unchecked, Partial, Default,
    }
}
