package org.calamarfederal.messyink.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.TimeTypeConverters

@Entity(
    tableName = "counters",
)
data class CounterEntity(
    val name: String,

    @ColumnInfo(name = "time_modified")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeModified: Instant,

    @ColumnInfo(name = "time_created")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeCreated: Instant,

    @PrimaryKey val id: Long,
)

@Entity(
    tableName = "counter_ticks", foreignKeys = [ForeignKey(
        entity = CounterEntity::class,
        parentColumns = ["id"],
        childColumns = ["parent_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class TickEntity(
    val amount: Double,

    @ColumnInfo(name = "time_created")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeCreated: Instant,

    @ColumnInfo(name = "time_for_data")
    @field:TypeConverters(TimeTypeConverters::class)
    val timeForData: Instant,

    @ColumnInfo(name = "parent_id", index = true)
    val parentId: Long,

    @PrimaryKey
    val id: Long,
)

