package org.calamarfederal.messyink.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.entity.CounterColumn
import org.calamarfederal.messyink.data.entity.TickColumn

/**
 * Convert [Instant] to a room compatible type
 */
class TimeTypeConverters {
    /**
     * Unix milliseconds -- not seconds
     */
    @TypeConverter
    fun fromInstantToTimeStamp(instant: Instant): Long = instant.toEpochMilliseconds()

    /**
     * Unix milliseconds -- not seconds
     */
    @TypeConverter
    fun fromTimestampToInstant(stamp: Long): Instant = Instant.fromEpochMilliseconds(stamp)
}

/**
 * Convert [TickColumn] to its corresponding column name
 */
class TickColumnConverters {
    /**
     * Convert to string of column name
     */
    @TypeConverter
    fun fromColumnToString(column: TickColumn): String = column.columnName
}

/**
 * Convert [CounterColumn] to its corresponding column name
 */
class CounterColumnConverter {
    /**
     * Convert to string of column name
     */
    @TypeConverter
    fun fromColumnToString(column: CounterColumn): String = column.columnName
}
