package org.calamarfederal.messyink.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

/**
 * Convert [Instant] to a room compatible type
 */
class TimeTypeConverters {
//    @TypeConverter
//    fun fromInstantToTimeStamp(instant: Instant?): Long? = instant?.toEpochMilliseconds()
//    @TypeConverter
//    fun fromTimestampToInstant(stamp: Long?): Instant? = stamp?.let {Instant.fromEpochMilliseconds(it) }

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
