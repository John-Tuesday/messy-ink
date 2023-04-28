package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFrom
import org.calamarfederal.messyink.feature_counter.domain.CreateTickFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.use_case.CreateCounterFromImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.CreateTickFromImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCounterFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCountersFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksAverageOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumByFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumOfFlowImpl

/**
 * Bind Use Cases to their implementation
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class CounterUseCasesModule {

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetCounterFlow(impl: GetCounterFlowImpl): GetCounterFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetCountersFlow(impl: GetCountersFlowImpl): GetCountersFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksOfFlow(impl: GetTicksOfFlowImpl): GetTicksOfFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindCreateCounterFrom(impl: CreateCounterFromImpl): CreateCounterFrom

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindCreateTickFrom(impl: CreateTickFromImpl): CreateTickFrom

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksSumOfFlow(impl: GetTicksSumOfFlowImpl): GetTicksSumOfFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksAverageOfFlow(impl: GetTicksAverageOfFlowImpl): GetTicksAverageOfFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksSumByFlow(impl: GetTicksSumByFlowImpl): GetTicksSumByFlow
}
