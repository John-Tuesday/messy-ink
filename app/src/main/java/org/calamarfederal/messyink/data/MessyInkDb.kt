package org.calamarfederal.messyink.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.Note
import org.calamarfederal.messyink.data.entity.NoteItem
import org.calamarfederal.messyink.data.entity.TickEntity

private const val DB_VERSION: Int = 9

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
    abstract fun viewAllDoa(): ViewAllDao
    abstract fun counterDao(): CounterDao
}
