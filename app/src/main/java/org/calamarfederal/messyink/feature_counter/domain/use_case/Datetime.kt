package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.datetime.Clock
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.GetTime

/**
 * Gets the function which will fetch the current time
 *
 * made as a var for dependency injection, but uses [Clock.System.now] as its initial state to enable previews
 */
@CurrentTime
var CurrentTimeGetter: GetTime = GetTime(Clock.System::now)
