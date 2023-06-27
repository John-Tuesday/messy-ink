package org.calamarfederal.messyink.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.TimeTypeConverters

/**
 * Room [Entity] for counters
 *
 * [timeModified] and [timeCreated] are converted to timestamp [Long] in the db
 */
@Entity(tableName = "counter")
data class CounterEntity(
    /**
     * user given name
     */
    val name: String,

    /**
     * time of last modification to the counter itself
     *
     * converts to [Long]
     */
    @ColumnInfo(name = "time_modified")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeModified: Instant,

    /**
     * time the counter was first created
     *
     * converts to [Long]
     */
    @ColumnInfo(name = "time_created")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeCreated: Instant,

    /**
     * unique id used to connect to related tables
     */
    @PrimaryKey val id: Long,
)

/**
 * Identify columns for queries (typically sorting)
 */
sealed class CounterColumn(
    /**
     * Name of the column in room
     */
    val columnName: String,
) {
    /**
     * Column measuring time
     */
    sealed class TimeType(columnName: String) : CounterColumn(columnName)

    /**
     * Time Created
     */
    object TimeCreated : TimeType("time_created")

    /**
     * Time Modified
     */
    object TimeModified : TimeType("time_modified")
}
