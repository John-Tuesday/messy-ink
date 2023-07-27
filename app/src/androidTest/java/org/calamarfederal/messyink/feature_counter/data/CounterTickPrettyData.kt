package org.calamarfederal.messyink.feature_counter.data

import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.source.database.TickEntity
import org.calamarfederal.messyink.feature_counter.di.TestTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

private val baseTime get() = TestTime

val prettyCounterWorkout: CounterEntity
    get() =
        CounterEntity(
            name = "Workout",
            timeModified = baseTime,
            timeCreated = baseTime,
            id = 1L,
        )

private val prettyCounterPlayer1: CounterEntity
    get() =
        CounterEntity(
            name = "Player 1 Health",
            timeModified = baseTime + 3.days,
            timeCreated = baseTime + 3.days,
            id = 2L,
        )
private val prettyCounterPlayer2: CounterEntity
    get() =
        CounterEntity(
            name = "Player 2 Health",
            timeModified = baseTime + 3.days,
            timeCreated = baseTime + 3.days,
            id = 3L,
        )
private val prettyCounters: List<CounterEntity>
    get() = listOf(
        prettyCounterWorkout,
        prettyCounterPlayer1,
        prettyCounterPlayer2,
    )

private val prettyWorkoutTicks: List<TickEntity>
    get() = listOf(
        TickEntity(
            amount = 1.00,
            timeModified = baseTime,
            timeCreated = baseTime,
            timeForData = baseTime,
            parentId = prettyCounterWorkout.id,
            id = 10L,
        ),
        TickEntity(
            amount = 1.00,
            timeModified = baseTime + 1.days,
            timeCreated = baseTime + 1.days,
            timeForData = baseTime + 1.days,
            parentId = prettyCounterWorkout.id,
            id = 11L,
        ),
        TickEntity(
            amount = 0.00,
            timeModified = baseTime + 2.days,
            timeCreated = baseTime + 2.days,
            timeForData = baseTime + 2.days,
            parentId = prettyCounterWorkout.id,
            id = 12L,
        ),
        TickEntity(
            amount = 1.00,
            timeModified = baseTime + 3.days,
            timeCreated = baseTime + 3.days,
            timeForData = baseTime + 3.days,
            parentId = prettyCounterWorkout.id,
            id = 13L,
        ),
    )

private val prettyTicksPlayer1: List<TickEntity>
    get() = listOf(
        TickEntity(
            amount = 20.00,
            timeForData = prettyCounterPlayer1.timeCreated,
            timeCreated = prettyCounterPlayer1.timeCreated,
            timeModified = prettyCounterPlayer1.timeCreated,
            parentId = prettyCounterPlayer1.id,
            id = 20L,
        ),
        TickEntity(
            amount = -7.00,
            timeForData = prettyCounterPlayer1.timeCreated + 3.minutes,
            timeCreated = prettyCounterPlayer1.timeCreated + 3.minutes,
            timeModified = prettyCounterPlayer1.timeCreated + 3.minutes,
            parentId = prettyCounterPlayer1.id,
            id = 21L,
        ),
        TickEntity(
            amount = 3.00,
            timeForData = prettyCounterPlayer1.timeCreated + 4.minutes,
            timeCreated = prettyCounterPlayer1.timeCreated + 4.minutes,
            timeModified = prettyCounterPlayer1.timeCreated + 4.minutes,
            parentId = prettyCounterPlayer1.id,
            id = 23L,
        ),
    )

private val prettyTicksPlayer2: List<TickEntity>
    get() = listOf(
        TickEntity(
            amount = 20.00,
            timeForData = prettyCounterPlayer2.timeCreated,
            timeCreated = prettyCounterPlayer2.timeCreated,
            timeModified = prettyCounterPlayer2.timeCreated,
            parentId = prettyCounterPlayer1.id,
            id = 30L,
        ),
        TickEntity(
            amount = -2.00,
            timeForData = prettyCounterPlayer2.timeCreated + 2.minutes,
            timeCreated = prettyCounterPlayer2.timeCreated + 2.minutes,
            timeModified = prettyCounterPlayer2.timeCreated + 2.minutes,
            parentId = prettyCounterPlayer1.id,
            id = 31L,
        ),
        TickEntity(
            amount = -4.00,
            timeForData = prettyCounterPlayer2.timeCreated + 5.minutes,
            timeCreated = prettyCounterPlayer2.timeCreated + 5.minutes,
            timeModified = prettyCounterPlayer2.timeCreated + 5.minutes,
            parentId = prettyCounterPlayer1.id,
            id = 32L,
        ),
    )

private val prettyTicks: List<TickEntity>
    get() = prettyWorkoutTicks + prettyTicksPlayer1 + prettyTicksPlayer2

suspend fun CounterTickDao.insertPrettyData() {
    prettyCounters.onEach { insertCounter(it) }
    prettyTicks.onEach { insertTick(it) }
}
