package org.calamarfederal.messyink.feature_counter.data.source

import org.calamarfederal.messyink.feature_counter.data.source.database.CounterDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.TickDao

typealias CounterTickLocalSource = CounterTickDao

typealias CounterLocalSource = CounterDao

typealias TickLocalSource = TickDao
