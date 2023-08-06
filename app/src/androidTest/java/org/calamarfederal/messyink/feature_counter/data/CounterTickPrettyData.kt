package org.calamarfederal.messyink.feature_counter.data

import android.content.res.Resources
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.data.source.database.TickEntity
import org.calamarfederal.messyink.feature_counter.di.TestTime
import org.calamarfederal.messyink.R
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

private val baseTime get() = TestTime

const val prettyCounterWorkoutId: Long = 1L
fun loadPrettyCounterWorkout(res: Resources): CounterEntity = CounterEntity(
    name = res.getString(R.string.counter_name_workout),
    timeModified = baseTime,
    timeCreated = baseTime,
    id = prettyCounterWorkoutId,
)

fun loadPrettyCounterPlayer1(res: Resources): CounterEntity = CounterEntity(
    name = res.getString(R.string.counter_name_player_1),
    timeModified = baseTime + 3.days,
    timeCreated = baseTime + 3.days,
    id = 2L,
)

fun loadPrettyCounterPlayer2(res: Resources): CounterEntity = CounterEntity(
    name = res.getString(R.string.counter_name_player_2),
    timeModified = baseTime + 3.days,
    timeCreated = baseTime + 3.days,
    id = 3L,
)

private fun loadPrettyWorkoutTicks(workoutCounter: CounterEntity): List<TickEntity> = listOf(
    TickEntity(
        amount = 1.00,
        timeModified = baseTime,
        timeCreated = baseTime,
        timeForData = baseTime,
        parentId = workoutCounter.id,
        id = 10L,
    ),
    TickEntity(
        amount = 1.00,
        timeModified = baseTime + 1.days,
        timeCreated = baseTime + 1.days,
        timeForData = baseTime + 1.days,
        parentId = workoutCounter.id,
        id = 11L,
    ),
    TickEntity(
        amount = 0.00,
        timeModified = baseTime + 2.days,
        timeCreated = baseTime + 2.days,
        timeForData = baseTime + 2.days,
        parentId = workoutCounter.id,
        id = 12L,
    ),
    TickEntity(
        amount = 1.00,
        timeModified = baseTime + 3.days,
        timeCreated = baseTime + 3.days,
        timeForData = baseTime + 3.days,
        parentId = workoutCounter.id,
        id = 13L,
    ),
)

private fun loadPrettyTicksPlayer1(prettyCounterPlayer1: CounterEntity): List<TickEntity> = listOf(
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

private fun loadPrettyTicksPlayer2(prettyCounterPlayer2: CounterEntity): List<TickEntity> = listOf(
    TickEntity(
        amount = 20.00,
        timeForData = prettyCounterPlayer2.timeCreated,
        timeCreated = prettyCounterPlayer2.timeCreated,
        timeModified = prettyCounterPlayer2.timeCreated,
        parentId = prettyCounterPlayer2.id,
        id = 30L,
    ),
    TickEntity(
        amount = -2.00,
        timeForData = prettyCounterPlayer2.timeCreated + 2.minutes,
        timeCreated = prettyCounterPlayer2.timeCreated + 2.minutes,
        timeModified = prettyCounterPlayer2.timeCreated + 2.minutes,
        parentId = prettyCounterPlayer2.id,
        id = 31L,
    ),
    TickEntity(
        amount = -4.00,
        timeForData = prettyCounterPlayer2.timeCreated + 5.minutes,
        timeCreated = prettyCounterPlayer2.timeCreated + 5.minutes,
        timeModified = prettyCounterPlayer2.timeCreated + 5.minutes,
        parentId = prettyCounterPlayer2.id,
        id = 32L,
    ),
)

suspend fun CounterTickDao.insertPrettyData(res: Resources) {
    loadPrettyCounterWorkout(res).let {
        insertCounter(it)
        for (t in loadPrettyWorkoutTicks(it))
            insertTick(t)
    }
    loadPrettyCounterPlayer1(res).let {
        insertCounter(it)
        for (t in loadPrettyTicksPlayer1(it))
            insertTick(t)
    }
    loadPrettyCounterPlayer2(res).let {
        insertCounter(it)
        for (t in loadPrettyTicksPlayer2(it))
            insertTick(t)
    }
}
