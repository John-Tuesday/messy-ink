package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo

/**
 * Bindings for each Counter-feature ViewModel
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class CounterModuleBindings {
    /**
     * Binds CountersRepo Implementation to Interface
     */
    @Binds
    @ViewModelScoped
    abstract fun bindCounterRepo(repoImpl: CountersRepoImpl): CountersRepo
}

/**
 * Provider for each Counter-feature ViewModel
 */
@Module
@InstallIn(ViewModelComponent::class)
object CounterModuleProvider {
    /**
     * Provide View Model scoped DAO to application scoped database
     */
    @Provides
    @ViewModelScoped
    fun provideCountersDao(db: MessyInkDb): CounterDao = db.counterDao()
}
