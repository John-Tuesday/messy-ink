package org.calamarfederal.messyink.feature_counter.data

import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.source.database.TickEntity

class MockCounterTickDao(
    private val tickTable: List<TickEntity>,
    private val counterTable: List<CounterEntity>,
) : CounterTickDaoStub() {}
