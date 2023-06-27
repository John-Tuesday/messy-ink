package org.calamarfederal.messyink.data.entity

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.TimeTypeConverters

/**
 * Room [Entity] for an event owned by [CounterEntity]
 *
 * [Instant] converts to [Long]
 */
@Entity(
    tableName = "counter_tick", foreignKeys = [ForeignKey(
        entity = CounterEntity::class,
        parentColumns = ["id"],
        childColumns = ["parent_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class TickEntity(
    /**
     * amount measured
     */
    val amount: Double,

    /**
     * time the [TickEntity] was last modified
     *
     * converts to [Long]
     */
    @ColumnInfo(name = "time_modified")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeModified: Instant,

    /**
     * time the [TickEntity] was created
     *
     * converts to [Long]
     */
    @ColumnInfo(name = "time_created")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeCreated: Instant,

    /**
     * time the [measurement][amount] took place
     *
     * converts to [Long]
     */
    @ColumnInfo(name = "time_for_data")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeForData: Instant,

    /**
     * [id][CounterEntity.id] of owning [CounterEntity]
     */
    @ColumnInfo(name = "parent_id", index = true)
    val parentId: Long,

    /**
     * id which must be unique amongst all [TickEntity]
     */
    @PrimaryKey
    val id: Long,
)

/**
 * Column of [TickEntity]
 */
sealed class TickColumn(
    /**
     * Name of the column in room
     */
    val columnName: String,
) {
    /**
     * Column measuring time
     */
    sealed class TimeType(columnName: String) : TickColumn(columnName)

    /**
     * Time last modified
     */
    object TimeModified : TimeType("time_modified")

    /**
     * Time first created
     */
    object TimeCreated : TimeType("time_created")

    /**
     * Default time used to sort or graph
     */
    object TimeForData : TimeType("time_for_data")
}
