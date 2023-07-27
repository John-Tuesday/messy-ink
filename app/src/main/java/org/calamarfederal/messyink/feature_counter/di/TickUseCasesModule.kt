package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicks
import org.calamarfederal.messyink.feature_counter.domain.DeleteTicksOf
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.SimpleCreateTickUseCase
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPoints
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.DeleteTicksOfImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumByFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.SimpleCreateTickUseCaseImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.TicksToGraphPointsImpl

/**
 * Bind Tick use case implementation to interface
 */
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
    abstract fun bindCreateTick(impl: SimpleCreateTickUseCaseImpl): SimpleCreateTickUseCase

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
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksSumOfFlow(impl: GetTicksSumOfFlowImpl): GetTicksSumOfFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindGetTicksSumByFlow(impl: GetTicksSumByFlowImpl): GetTicksSumByFlow

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindTicksToGraphPoints(impl: TicksToGraphPointsImpl): TicksToGraphPoints
}
