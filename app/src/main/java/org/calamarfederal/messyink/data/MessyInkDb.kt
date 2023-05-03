package org.calamarfederal.messyink.data

import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.Note
import org.calamarfederal.messyink.data.entity.NoteItem
import org.calamarfederal.messyink.data.entity.TickEntity

private const val DB_VERSION: Int = 10

/**
 * [RoomDatabase] for [CounterEntity] and [Note] types and related
 */
@Database(
    version = DB_VERSION,
    entities = [
        Note::class,
        NoteItem::class,
        CounterEntity::class,
        TickEntity::class,
    ],
    exportSchema = false,
)
abstract class MessyInkDb : RoomDatabase() {
    /**
     * Dao for [Note] feature
     */
    abstract fun viewAllDoa(): ViewAllDao

    /**
     * Dao for [CounterEntity] feature
     */
    abstract fun counterDao(): CounterDao
}


/**
 * Optional return value for [Update], [Delete], or [Insert]
 */
typealias RowsChanged = Int
/**
 * Optional return value for [Insert] if and only if there is one [Entity]
 */
typealias RowId = Long

/**
 * matches objects using [PrimaryKey]
 */
typealias DeleteNotes = Delete

/**
 * matches objects using [PrimaryKey]
 */
typealias UpdateNotes = Update

/**
 * if one [Entity] as a parameter, can return [RowId]
 *
 * if array or collection of [Entity] as parameter, can return an array or collection of [RowId]
 */
typealias InsertNotes = Insert
