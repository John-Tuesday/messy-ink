package org.calamarfederal.messyink.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class TimeTypeConverters {
//    @TypeConverter
//    fun fromInstantToTimeStamp(instant: Instant?): Long? = instant?.toEpochMilliseconds()
//    @TypeConverter
//    fun fromTimestampToInstant(stamp: Long?): Instant? = stamp?.let {Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun fromInstantToTimeStamp(instant: Instant): Long = instant.toEpochMilliseconds()

    @TypeConverter
    fun fromTimestampToInstant(stamp: Long): Instant = Instant.fromEpochMilliseconds(stamp)
}
