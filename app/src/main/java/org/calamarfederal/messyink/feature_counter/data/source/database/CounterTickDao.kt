package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Dao operations on [TickEntity] when grouped [TickEntity.parentId]
 */
@Dao
interface CounterTickDao : TickDao, CounterDao
