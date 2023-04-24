package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.datetime.Instant

data class Counter(
    val name: String,
    val timeModified: Instant,
    val timeCreated: Instant,
    val id: Long,
)

data class Tick(
    val amount: Double,
    val timeCreated: Instant,
    val timeForData: Instant,
    val parentId: Long,
    val id: Long,
)


