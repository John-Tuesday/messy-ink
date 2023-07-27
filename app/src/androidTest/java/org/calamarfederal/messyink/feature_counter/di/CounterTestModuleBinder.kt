package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepo
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CounterModuleBindings::class],
)
abstract class CounterTestModuleBinder {
    /**
     * Binds CountersRepo Implementation to Interface
     */
    @Binds
    @Singleton
    abstract fun bindCounterRepo(repoImpl: CountersRepoImpl): CountersRepo
}
