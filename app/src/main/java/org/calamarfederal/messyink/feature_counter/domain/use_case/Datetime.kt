package org.calamarfederal.messyink.feature_counter.domain.use_case

import kotlinx.datetime.Clock
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.GetTime

@CurrentTime
var CurrentTimeGetter: GetTime = GetTime(Clock.System::now)
