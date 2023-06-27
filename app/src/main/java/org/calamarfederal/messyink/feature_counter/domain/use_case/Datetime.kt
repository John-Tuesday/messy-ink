package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.di.CurrentTimeZone
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.domain.GetTimeZone

/**
 * Gets the function which will fetch the current time
 *
 * made as a var for dependency injection, but uses [Clock.System.now] as its initial state to enable previews
 */
@CurrentTime
var CurrentTimeGetter: GetTime = GetTime(Clock.System::now)

/**
 * Gets the function which fetches the current time zone
 *
 * made a var for dependency injection, but uses [TimeZone.currentSystemDefault] for previews
 */
@CurrentTimeZone
var CurrentTimeZoneGetter: GetTimeZone = GetTimeZone(TimeZone::currentSystemDefault)
