package org.calamarfederal.messyink.feature_counter.data.repository

import javax.inject.Inject

/**
 * Combine [CounterRepository] and [TickRepository]
 */
interface CounterTickRepository : CounterRepository, TickRepository

/**
 * Delegate inheritance to [counterRepo] and [tickRepo]
 */
class CounterTickRepositoryImpl @Inject constructor(
    private val counterRepo: CounterRepository,
    private val tickRepo: TickRepository,
) : CounterRepository by counterRepo, TickRepository by tickRepo, CounterTickRepository
