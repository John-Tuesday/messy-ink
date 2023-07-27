package org.calamarfederal.messyink.feature_counter.data.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepository
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepositoryImpl
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepo
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class CounterModule {
    @Binds
    @ViewModelScoped
    abstract fun counterRepo(impl: CountersRepoImpl): CountersRepo

}

@Module
@InstallIn(ViewModelComponent::class)
abstract class TickModule {
    @Binds
    @ViewModelScoped
    abstract fun tickRepo(impl: TickRepositoryImpl): TickRepository
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class CounterTickModule {
    @Binds
    @ViewModelScoped
    abstract fun counterTickRepo(impl: CounterTickRepositoryImpl): CounterTickRepository
}
