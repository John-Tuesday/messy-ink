package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.source.database.TickDao
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.data.repository.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.GetTimeZone
import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

/**
 * Specify provided [Instant] represents current time
 */
@Qualifier
@Retention(BINARY)
annotation class CurrentTime

/**
 * Specify provided time zone represents current time zone
 */
@Qualifier
@Retention(BINARY)
annotation class CurrentTimeZone

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
    fun provideCounterDao(db: MessyInkDb): CounterDao = db.counterDao()

    /**
     * Provide View Model scoped [TickDao]
     */
    @Provides
    @ViewModelScoped
    fun provideTickDao(db: MessyInkDb): TickDao = db.tickDao()

    /**
     * Provide View Model scoped [CounterTickDao]
     */
    @Provides
    @ViewModelScoped
    fun provideCounterTickDao(db: MessyInkDb): CounterTickDao = db.CounterTickDao()

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
    fun provideCurrentTime(): Instant = Clock.System.now()

    /**
     * Provides a simple callable which returns the current time
     */
    @CurrentTime
    @Provides
    fun provideCurrentTimeGetter(): GetTime = GetTime { Clock.System.now() }

    /**
     * Provides a simple callable which returns the current timezone
     */
    @CurrentTimeZone
    @Provides
    fun provideCurrentTimeZoneGetter(): GetTimeZone =
        GetTimeZone { TimeZone.currentSystemDefault() }
}
