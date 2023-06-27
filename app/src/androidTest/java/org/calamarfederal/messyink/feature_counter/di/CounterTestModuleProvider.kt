package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.data.TickDao
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.GetTimeZone
import javax.inject.Singleton

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
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provide the current time of the system at initialization
     */
    @CurrentTime
    @Provides
    fun provideCurrentTime(): Instant = Clock.System.now()

    /**
     * Provides a simple callable which returns the current time
     */
    @CurrentTime
    @Provides
    fun provideCurrentTimeGetter(): GetTime = GetTime { Clock.System.now() }

    @CurrentTimeZone
    @Provides
    fun provideCurrentTimeZoneGetter(): GetTimeZone =
        GetTimeZone { TimeZone.currentSystemDefault() }
}
