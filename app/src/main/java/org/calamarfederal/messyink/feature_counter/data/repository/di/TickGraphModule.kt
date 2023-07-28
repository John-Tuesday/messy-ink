package org.calamarfederal.messyink.feature_counter.data.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.calamarfederal.messyink.feature_counter.data.repository.TickGraphRepository
import org.calamarfederal.messyink.feature_counter.data.repository.TickGraphRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class TickGraphModule {
    @Binds
    abstract fun tickGraphRepo(impl: TickGraphRepositoryImpl): TickGraphRepository
}
