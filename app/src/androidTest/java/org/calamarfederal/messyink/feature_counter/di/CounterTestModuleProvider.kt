package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.feature_counter.data.repository.di.CounterTickDaoModule
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.source.database.TickDao
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.GetTimeZone
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CounterTickDaoModule::class]
)
object CounterTestModuleProvider {
    /**
     * Provide View Model scoped DAO to application scoped database
     */
    @Provides
    fun provideCounterDao(db: MessyInkDb): CounterDao = db.counterDao()

    /**
     * Provide View Model scoped [TickDao]
     */
    @Provides
    fun provideTickDao(db: MessyInkDb): TickDao = db.tickDao()

    /**
     * Provide View Model scoped [CounterTickDao]
     */
    @Provides
    fun provideCounterTickDao(db: MessyInkDb): CounterTickDao = db.CounterTickDao()
}
