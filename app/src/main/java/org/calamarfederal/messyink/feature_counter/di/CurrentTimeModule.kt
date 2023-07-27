package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
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

@Module
@InstallIn(SingletonComponent::class)
object CurrentTimeModule {
    @Provides
    @CurrentTime
    fun provideCurrentTime(): GetTime = GetTime { Clock.System.now() }

    @Provides
    @CurrentTimeZone
    fun provideCurrentTimeZone(): GetTimeZone = GetTimeZone { TimeZone.currentSystemDefault() }
}
