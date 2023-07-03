package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.DeleteCounter
import org.calamarfederal.messyink.feature_counter.domain.DuplicateCounter
import org.calamarfederal.messyink.feature_counter.domain.GetCounterAsSupportOrNull
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounter
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterFromSupport
import org.calamarfederal.messyink.feature_counter.domain.UpdateCounterSupport
import org.calamarfederal.messyink.feature_counter.domain.use_case.CreateCounterFromSupportImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteCounterImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DuplicateCounterImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCounterAsSupportImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCounterFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCountersFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateCounterFromSupportImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateCounterImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateCounterSupportImpl

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
    abstract fun bindDuplicateCounter(impl: DuplicateCounterImpl): DuplicateCounter

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindCreateCounterFromSupport(impl: CreateCounterFromSupportImpl): CreateCounterFromSupport

//    /**
//     * Binds Implementation to interface
//     */
//    @Binds
//    abstract fun bindCreateTickFrom(impl: DuplicateTickImpl): DuplicateTick

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
    abstract fun bindDeleteCounter(impl: DeleteCounterImpl): DeleteCounter
}
