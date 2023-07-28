package org.calamarfederal.messyink.feature_counter.data.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepository
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepositoryImpl
import org.calamarfederal.messyink.feature_counter.data.repository.CounterRepository
import org.calamarfederal.messyink.feature_counter.data.repository.CounterRepositoryImpl
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class CounterModule {
    @Binds
    abstract fun counterRepo(impl: CounterRepositoryImpl): CounterRepository

}

@Module
@InstallIn(ViewModelComponent::class)
abstract class TickModule {
    @Binds
    abstract fun tickRepo(impl: TickRepositoryImpl): TickRepository
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class CounterTickModule {
    @Binds
    abstract fun counterTickRepo(impl: CounterTickRepositoryImpl): CounterTickRepository
}
