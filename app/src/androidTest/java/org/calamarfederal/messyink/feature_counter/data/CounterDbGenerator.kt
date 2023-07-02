package org.calamarfederal.messyink.feature_counter.data

import kotlinx.datetime.Instant
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.CounterTickDao
import org.calamarfederal.messyink.data.entity.CounterEntity
import org.calamarfederal.messyink.data.entity.TickEntity
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Arbitrary day for use as a more recent test foundation
 *
 * Monday, May 22, 2023 4:59:27 AM GMT-05:00
 */
val TestTime: Instant = Instant.fromEpochMilliseconds(1684749567000)

fun generateCounters(
    startTime: Instant = TestTime,
    stepTime: Duration = 1.days,
    startId: Long = 1L,
    incId: (Long) -> Long = { it + 1 },
): Sequence<CounterEntity> = generateSequence(
    CounterEntity(
        name = "name: $startId",
        timeCreated = startTime,
        timeModified = startTime,
        id = startId,
    )
) {
    val id = incId(it.id)
    CounterEntity(
        name = "name: $id",
        timeCreated = it.timeCreated + stepTime,
        timeModified = it.timeModified + stepTime,
        id = id,
    )
}

fun generateTicks(
    startTime: Instant = TestTime,
    stepTime: Duration = 1.days,
    parentId: Long = 1L,
    startId: Long = 1L,
    incId: (Long) -> Long = { it + 1 },
    amount: (Long) -> Double = { it.toDouble() },
): Sequence<TickEntity> = generateSequence(
    TickEntity(
        amount = amount(startId),
        timeCreated = startTime,
        timeModified = startTime,
        timeForData = startTime,
        parentId = parentId,
        id = startId,
    )
) {
    val id = incId(it.id)

    TickEntity(
        amount = amount(id),
        timeCreated = it.timeCreated + stepTime,
        timeModified = it.timeModified + stepTime,
        timeForData = it.timeForData + stepTime,
        parentId = parentId,
        id = id,
    )
}

suspend fun CounterTickDao.generateTestData(
    counters: Int = 20,
    ticksForCounter: (Int) -> Int = { 10 },
    startTime: Instant = TestTime,
) {
    val generateTickId = generateSequence(1L) { it + 1L }.iterator()
    generateCounters(startTime = startTime).take(counters).forEachIndexed { index, counter ->
        insertCounter(counter)

        generateTicks(
            startTime = counter.timeCreated,
            parentId = counter.id,
            startId = generateTickId.next(),
            incId = { generateTickId.next() }
        ).take(ticksForCounter(index)).forEach {
            insertTick(it)
        }
    }
}
