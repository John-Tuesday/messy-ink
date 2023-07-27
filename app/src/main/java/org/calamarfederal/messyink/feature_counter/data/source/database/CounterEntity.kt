package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Instant

private object CounterTableNames {
    const val TableName = "counter"
    const val TimeModified = "time_modified"
    const val TimeCreated = "time_created"
}

/**
 * Room [Entity] for counters
 *
 * [timeModified] and [timeCreated] are converted to timestamp [Long] in the db
 */
@Entity(tableName = CounterTableNames.TableName)
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
    @ColumnInfo(name = CounterTableNames.TimeModified)
    @field:TypeConverters(TimeTypeConverters::class)
    val timeModified: Instant,

    /**
     * time the counter was first created
     *
     * converts to [Long]
     */
    @ColumnInfo(name = CounterTableNames.TimeCreated)
    @field:TypeConverters(TimeTypeConverters::class)
    val timeCreated: Instant,

    /**
     * unique id used to connect to related tables
     */
    @PrimaryKey val id: Long,
)
