package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.DuplicateCounter
import org.calamarfederal.messyink.feature_counter.domain.DuplicateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksFrom
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetCounterAsSupportOrNull
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterSupport
import org.calamarfederal.messyink.feature_counter.domain.UpdateTick
import org.calamarfederal.messyink.feature_counter.domain.use_case.CreateCounterFromSupportImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DuplicateCounterImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DuplicateTickImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteCounterImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksFromImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksOfImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCounterAsSupportImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCounterFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCountersFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksAverageOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumByFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateCounterFromSupportImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateCounterImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateCounterSupportImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateTickImpl

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
     * Binds default implementation to interface
     */
    @Binds
    abstract fun bindGetCounterAsSupportOrNull(impl: GetCounterAsSupportImpl): GetCounterAsSupportOrNull

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksOfFlow(impl: GetTicksOfFlowImpl): GetTicksOfFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindDuplicateCounter(impl: DuplicateCounterImpl): DuplicateCounter

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindCreateCounterFromSupport(impl: CreateCounterFromSupportImpl): CreateCounterFromSupport

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindCreateTickFrom(impl: DuplicateTickImpl): DuplicateTick

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindUpdateCounter(impl: UpdateCounterImpl): UpdateCounter

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindUpdateCounterFromSupport(impl: UpdateCounterFromSupportImpl): UpdateCounterFromSupport

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindUpdateCounterSupport(impl: UpdateCounterSupportImpl): UpdateCounterSupport

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindUpdateTick(impl: UpdateTickImpl): UpdateTick

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindDeleteCounter(impl: DeleteCounterImpl): DeleteCounter

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindDeleteTicks(impl: DeleteTicksImpl): DeleteTicks

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindDeleteTicksOf(impl: DeleteTicksOfImpl): DeleteTicksOf

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindDeleteTicksFrom(impl: DeleteTicksFromImpl): DeleteTicksFrom

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
