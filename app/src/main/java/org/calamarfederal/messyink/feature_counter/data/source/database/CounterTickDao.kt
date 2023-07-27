package org.calamarfederal.messyink.feature_counter.data.source.database

import androidx.room.Dao

/**
 * Dao operations on [TickEntity] when grouped [TickEntity.parentId]
 */
@Dao
interface CounterTickDao : TickDao, CounterDao
