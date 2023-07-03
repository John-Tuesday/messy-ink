package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.calamarfederal.messyink.feature_counter.domain.CreateTick
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksFrom
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.UpdateTick
import org.calamarfederal.messyink.feature_counter.domain.use_case.CreateTickImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksFromImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksOfImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksAverageOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumByFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.UpdateTickImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class TickUseCasesModule {

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksOfFlow(impl: GetTicksOfFlowImpl): GetTicksOfFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindCreateTick(impl: CreateTickImpl): CreateTick

    /**
     * Binds Default Implementation
     */
    @Binds
    abstract fun bindUpdateTick(impl: UpdateTickImpl): UpdateTick

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
