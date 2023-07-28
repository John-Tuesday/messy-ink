package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.calamarfederal.messyink.feature_counter.domain.SimpleCreateTickUseCase
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPoints
import org.calamarfederal.messyink.feature_counter.domain.SimpleCreateTickUseCaseImpl
import org.calamarfederal.messyink.feature_counter.domain.TicksToGraphPointsImpl

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
    abstract fun bindCreateTick(impl: SimpleCreateTickUseCaseImpl): SimpleCreateTickUseCase

    /**
     * Binds Implementation to interface
     */
    @Binds
    abstract fun bindTicksToGraphPoints(impl: TicksToGraphPointsImpl): TicksToGraphPoints
}
