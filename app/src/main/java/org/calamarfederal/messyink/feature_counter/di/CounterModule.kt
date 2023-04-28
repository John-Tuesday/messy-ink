package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo

@Module
@InstallIn(ViewModelComponent::class)
object CounterModule {
    @Provides
    @ActivityRetainedScoped
    fun provideDao(db: MessyInkDb): CounterDao = db.counterDao()

    @Provides
    @ActivityRetainedScoped
    fun provideRepo(dao: CounterDao): CountersRepo = CountersRepoImpl(dao)
}
