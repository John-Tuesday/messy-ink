package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
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
    fun provideCountersDao(db: MessyInkDb): CounterDao = db.counterDao()

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
