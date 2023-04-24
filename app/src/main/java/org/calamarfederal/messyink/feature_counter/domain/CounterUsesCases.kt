package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick


fun interface GetCounterFlow {
    operator fun invoke(id: Long): Flow<UiCounter?>
}

fun interface GetCountersFlow {
    operator fun invoke(): Flow<List<UiCounter>>
}

fun interface GetTicksOfFlow {
    operator fun invoke(parentId: Long): Flow<List<UiTick>>
}

fun interface CreateCounterFrom {
    suspend operator fun invoke(sample: UiCounter): UiCounter
}

fun interface CreateTickFrom {
    suspend operator fun invoke(sample: UiTick): UiTick
}

fun interface GetTicksSumOfFlow {
    operator fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double>

    operator fun invoke(parentId: Long): Flow<Double> =
        invoke(parentId = parentId, start = Instant.DISTANT_PAST, end = Instant.DISTANT_FUTURE)
}
fun interface GetTicksAverageFlow {
    operator fun invoke(parentId: Long, start: Instant, end: Instant): Flow<Double>

    operator fun invoke(parentId: Long): Flow<Double> =
        invoke(parentId = parentId, start = Instant.DISTANT_PAST, end = Instant.DISTANT_FUTURE)
}
fun interface GetTicksSumByFlow {
    operator fun invoke(): Flow<Map<Long, Double>>
}
