package org.calamarfederal.messyink.feature_counter.data

import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickEntity

class MockCounterTickDao(
    private val tickTable: List<TickEntity>,
    private val counterTable: List<CounterEntity>,
) : CounterTickDaoStub() {}
