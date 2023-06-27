package org.calamarfederal.messyink.feature_counter.domain

sealed interface TickSort {
    enum class TimeType : TickSort {
        TimeCreated, TimeModified, TimeForData,
    }
}

sealed interface CounterSort {
    enum class TimeType : CounterSort {
        TimeCreated, TimeModified
    }
}
