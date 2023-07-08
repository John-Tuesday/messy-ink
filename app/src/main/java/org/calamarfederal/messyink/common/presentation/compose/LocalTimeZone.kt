package org.calamarfederal.messyink.common.presentation.compose

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.datetime.TimeZone

/**
 * Get the system default [TimeZone]
 */
val LocalTimeZone = staticCompositionLocalOf { TimeZone.currentSystemDefault() }
