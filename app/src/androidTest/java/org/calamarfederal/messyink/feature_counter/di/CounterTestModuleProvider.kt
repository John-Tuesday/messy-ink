package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.feature_counter.data.repository.di.CounterTickDaoModule
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.source.database.TickDao

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
