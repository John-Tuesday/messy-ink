package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.source.database.TickDao
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.GetTimeZone
import javax.inject.Singleton

/**
 * Arbitrary day for use as a more recent test foundation
 *
 * Monday, May 22, 2023 4:59:27 AM GMT-05:00
 */
val TestTime: Instant = Instant.fromEpochMilliseconds(1684749567000)
val TestTz: TimeZone = TimeZone.UTC

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CounterModuleProvider::class]
)
object CounterTestModuleProvider {
    /**
     * Provide View Model scoped DAO to application scoped database
     */
    @Provides
    @Singleton
    fun provideCountersDao(db: MessyInkDb): CounterDao = db.counterDao()

    @Provides
    @Singleton
    fun provideTickDao(db: MessyInkDb): TickDao = db.tickDao()

    @Provides
    @Singleton
    fun provideCounterTickDao(db: MessyInkDb): CounterTickDao = db.CounterTickDao()

    /**
     * Dispatcher to move local database operations off UI thread
     */
    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provide the current time of the system at initialization
     */
    @CurrentTime
    @Provides
    fun provideCurrentTime(): Instant = TestTime

    /**
     * Provides a simple callable which returns the current time
     */
    @CurrentTime
    @Provides
    fun provideCurrentTimeGetter(): GetTime = GetTime { TestTime }

    @CurrentTimeZone
    @Provides
    fun provideCurrentTimeZoneGetter(): GetTimeZone =
        GetTimeZone { TestTz }
}
