package org.calamarfederal.messyink.common.presentation.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn


/**
 * Convert [LocalDate] to a the epoch millisecond equivalent in UTC
 *
 * as android requires
 */
fun LocalDate.toUtcMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
