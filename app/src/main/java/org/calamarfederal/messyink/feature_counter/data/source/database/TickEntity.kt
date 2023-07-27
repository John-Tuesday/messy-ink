package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Instant

private object TickTableNames {
    const val TableName: String = "counter_tick"
    const val TimeCreated: String = "time_created"
    const val TimeModified: String = "time_modified"
    const val TimeForData: String = "time_for_data"
}

/**
 * Room [Entity] for an event owned by [CounterEntity]
 *
 * [Instant] converts to [Long]
 */
@Entity(
    tableName = TickTableNames.TableName,
    foreignKeys = [ForeignKey(
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
    @ColumnInfo(name = TickTableNames.TimeModified)
    @field:TypeConverters(TimeTypeConverters::class)
    val timeModified: Instant,

    /**
     * time the [TickEntity] was created
     *
     * converts to [Long]
     */
    @ColumnInfo(name = TickTableNames.TimeCreated)
    @field:TypeConverters(TimeTypeConverters::class)
    val timeCreated: Instant,

    /**
     * time the [measurement][amount] took place
     *
     * converts to [Long]
     */
    @ColumnInfo(name = TickTableNames.TimeForData)
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
