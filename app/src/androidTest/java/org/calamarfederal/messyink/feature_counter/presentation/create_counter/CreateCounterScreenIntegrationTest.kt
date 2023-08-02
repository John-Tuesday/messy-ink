package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.data.model.NOID
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CreateCounterNode
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CreateCounterScreenIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    lateinit var counterId: MutableStateFlow<Long>

    @BindValue
    val contentSetter: OnCreateHookImpl = object : OnCreateHookImpl() {
        override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
            with(activity) {
                setContent {
                    NavHost(
                        navController = rememberNavController(),
                        startDestination = "test",
                    ) {
                        composable(
                            route = "test"
                        ) {
                            val id by counterId.collectAsStateWithLifecycle()
                            CreateCounterNode.CreateCounterScreenBuilder(
                                onCancel = {},
                                onDone = {},
                                counterId = id,
                            )
                        }
                    }
                }
            }
        }
    }

    @Inject
    lateinit var dao: CounterDao

    @Inject
    @CurrentTime
    lateinit var timeGetter: GetTime

    @Before
    fun setUp() {
        counterId = MutableStateFlow(NOID)
        hiltRule.inject()
    }

    @Test
    fun `dummy test`() {
    }

    @Test
    fun `Creating a valid counter is actually added`() {
        val counterFlow = dao.countersFlow()
        runBlocking {
            assert(counterFlow.first().isEmpty())
        }

        val testName = "new counter name"
        val testTime = timeGetter()
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField).performTextInput(testName)
        composeRule.onNodeWithTag(CreateCounterTestTags.SubmitButton).performClick()
        composeRule.waitForIdle()
        runBlocking {
            val counters = counterFlow.first()
            assert(counters.size == 1) {
                "${counters.size}"
            }
            val counter = counters[0]
            assert(counter.name == testName)
            assert(counter.timeCreated == testTime)
            assert(counter.timeModified == testTime)
        }
    }

    @Test
    fun `Edit counter actually publishes changes`() {
        val oldCounter = CounterEntity(
            name = "abc",
            timeCreated = Instant.fromEpochMilliseconds(0L),
            timeModified = Instant.fromEpochMilliseconds(0L),
            id = 2L,
        )
        runBlocking {
            dao.insertCounter(oldCounter)
        }
        val counterFlow = dao.countersFlow()
        runBlocking {
            assert(counterFlow.first().contains(oldCounter))
        }

        counterId.update { oldCounter.id }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField)
            .assertTextContains(oldCounter.name)

        val testName = "new counter name"
        val testTime = timeGetter()
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField).performTextClearance()
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField).performTextInput(testName)
        composeRule.onNodeWithTag(CreateCounterTestTags.SubmitButton).performClick()
        composeRule.waitForIdle()
        runBlocking {
            val counters = counterFlow.first()
            assert(counters.size == 1) {
                "${counters.size}"
            }
            val counter = counters[0]
            assert(counter.name == testName)
            assert(counter.timeCreated == oldCounter.timeCreated)
            assert(counter.timeModified == testTime)
        }
    }

    @Test
    fun `Edit counter then cancel discards any potential changes`() {
        val oldCounter = CounterEntity(
            name = "abc",
            timeCreated = Instant.fromEpochMilliseconds(0L),
            timeModified = Instant.fromEpochMilliseconds(0L),
            id = 2L,
        )
        runBlocking {
            dao.insertCounter(oldCounter)
        }
        val counterFlow = dao.countersFlow()
        runBlocking {
            assert(counterFlow.first().contains(oldCounter))
        }

        counterId.update { oldCounter.id }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField)
            .assertTextContains(oldCounter.name)

        val testName = "new counter name"
        composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField).performTextInput(testName)
        composeRule.onNodeWithTag(CreateCounterTestTags.CloseButton).performClick()
        composeRule.waitForIdle()
        runBlocking {
            val counters = counterFlow.first()
            assert(counters.size == 1) {
                "${counters.size}"
            }
            val counter = counters[0]
            assert(counter == oldCounter)
        }
    }
}
