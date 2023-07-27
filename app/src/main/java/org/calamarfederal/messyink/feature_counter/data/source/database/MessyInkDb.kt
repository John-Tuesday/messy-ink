package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.RoomDatabase
import androidx.room.Update

private const val DB_VERSION: Int = 12

/**
 * [RoomDatabase] for the whole app
 */
@Database(
    version = DB_VERSION,
    entities = [
        CounterEntity::class,
        TickEntity::class,
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 11, to = 12)
    ]
)
abstract class MessyInkDb : RoomDatabase() {

    /**
     * Simple [CounterEntity] Dao
     */
    abstract fun counterDao(): CounterDao

    /**
     * Simple [TickEntity] Dao
     */
    abstract fun tickDao(): TickDao

    /**
     * Dao [TickEntity] and [CounterEntity]
     */
    abstract fun CounterTickDao(): CounterTickDao

}

/**
 * Optional return value for [Update], [Delete], or [Insert]
 */
typealias RowsChanged = Int
/**
 * Optional return value for [Insert] if and only if there is one Entity/Row
 */
typealias RowId = Long

/**
 * matches objects using PrimaryKey
 */
typealias DeleteNotes = Delete

/**
 * matches objects using PrimaryKey
 */
typealias UpdateNotes = Update

/**
 * if one Entity as a parameter, can return [RowId]
 *
 * if array or collection of Entity as parameter, can return an array or collection of [RowId]
 */
typealias InsertNotes = Insert
