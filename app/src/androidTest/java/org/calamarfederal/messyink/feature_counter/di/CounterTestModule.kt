package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.GetTimeZone

@Module
@TestInstallIn(
    components = [ViewModelScoped::class],
    replaces = [CounterTestModuleBindings::class]
)
abstract class CounterTestModuleBindings {
    @Binds
    @ViewModelScoped
    abstract fun bindCounterRepo(repoIml: CountersRepoImpl): CountersRepo
}

/**
 * Base static time when providing 'current' time
 *
 * set to `May 5, 2023 5:46:10 AM GMT-05:00`
 */
private val timeConst: Instant = Instant.fromEpochMilliseconds(1683283570000L)
private val timeZoneConst: TimeZone = TimeZone.UTC

@Module
@TestInstallIn(
    components = [ViewModelScoped::class],
    replaces = [CounterModuleProvider::class],
)
object CounterTestModuleProvider {
    @Provides
    @ViewModelScoped
    fun provideCountersDao(db: MessyInkDb): CounterDao = db.counterDao()

    /**
     * Dispatcher to move local database operations off UI thread
     */
    @Provides
    @ViewModelScoped
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provide the current time of the system at initialization
     */
    @CurrentTime
    @Provides
    fun provideCurrentTime(): Instant = timeConst

    /**
     * Provides a simple callable which returns the current time
     */
    @CurrentTime
    @Provides
    fun provideCurrentTimeGetter(): GetTime = GetTime { timeConst }

    @CurrentTimeZone
    @Provides
    fun provideCurrentTimeZoneGetter(): GetTimeZone =
        GetTimeZone { timeZoneConst }
}
