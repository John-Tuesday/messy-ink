package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

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
