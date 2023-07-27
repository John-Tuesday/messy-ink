package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.GetTimeZone

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
    replaces = [CurrentTimeModule::class]
)
object CurrentTimeTestModule {
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
