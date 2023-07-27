package org.calamarfederal.messyink.feature_counter.data.repository

import javax.inject.Inject

/**
 * Combine [CountersRepo] and [TickRepository]
 */
interface CounterTickRepository : CountersRepo, TickRepository

/**
 * Delegate inheritance to [counterRepo] and [tickRepo]
 */
class CounterTickRepositoryImpl @Inject constructor(
    private val counterRepo: CountersRepo,
    private val tickRepo: TickRepository,
) : CountersRepo by counterRepo, TickRepository by tickRepo, CounterTickRepository
