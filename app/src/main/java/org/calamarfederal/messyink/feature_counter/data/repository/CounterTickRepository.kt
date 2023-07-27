package org.calamarfederal.messyink.feature_counter.data.repository

import javax.inject.Inject

interface CounterTickRepository : CountersRepo, TickRepository

class CounterTickRepositoryImpl @Inject constructor(
    private val counterRepo: CountersRepo,
    private val tickRepo: TickRepository,
) : CountersRepo by counterRepo, TickRepository by tickRepo, CounterTickRepository
