package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.feature_counter.data.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
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
