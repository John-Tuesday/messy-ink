package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepository
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepositoryImpl
import org.calamarfederal.messyink.feature_counter.data.repository.di.CounterTickModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CounterTickModule::class],
)
abstract class CounterTestModuleBinder {
    /**
     * Binds CountersRepo Implementation to Interface
     */
    @Binds
    @Singleton
    abstract fun counterTickRepo(impl: CounterTickRepositoryImpl): CounterTickRepository
}
