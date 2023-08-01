package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterDao
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterEntity
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class GameCounterScreenIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val counterName = "counter-test"
    private val counterId = 1L
    private val primaryDelta = 5.00
    private val secondaryDelta = 1.00

    @Inject
    lateinit var dao: CounterDao

    @BindValue
    val s: OnCreateHookImpl = object : OnCreateHookImpl() {
        override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
            with(activity) {
                setContent {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = GameCounterNode.route,
                    ) {
                        composable(
                            route = GameCounterNode.route,
                            arguments = listOf(navArgument(GameCounterNode.COUNTER_ID) { defaultValue = counterId }),
                        ) {
                            GameCounterNode.GameCounterScreenBuilder(
                                onNavigateUp = {},
                            )
                        }
                    }
                }
            }
        }
    }

    @Before
    fun setUp() {
        hiltRule.inject()
            runBlocking {
                dao.insertCounter(
                    CounterEntity(
                        name = counterName,
                        timeCreated = Instant.fromEpochMilliseconds(100L),
                        timeModified = Instant.fromEpochMilliseconds(100L),
                        id = counterId,
                    )
                )
            }
    }

    private val primaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryIncButton)
    private val primaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.PrimaryDecButton)
    private val secondaryIncButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryIncButton)
    private val secondaryDecButton get() = composeRule.onNodeWithTag(GameCounterTestTags.SecondaryDecButton)
    private val tickSumText get() = composeRule.onNodeWithTag(GameCounterTestTags.TotalSumText)
    private val counterNameText get() = composeRule.onNodeWithTag(GameCounterTestTags.CounterNameText)

    @Test
    fun `dummy test`() {
        primaryIncButton.assertHasClickAction()
        secondaryIncButton.assertHasClickAction()
        secondaryDecButton.assertHasClickAction()
        primaryDecButton.assertHasClickAction()
    }

    @Test
    fun `Counter name`() {
        counterNameText.assertTextContains(counterName)
    }

    @Test
    fun `Buttons increase and decrease as advertised and reflects in sum`() {
        var sum = 0.00
        tickSumText.assertTextContains("$sum")

        sum += primaryDelta
        primaryIncButton.performClick()
        tickSumText.assertTextContains("$sum")

        sum += secondaryDelta
        secondaryIncButton.performClick()
        tickSumText.assertTextContains("$sum")

        sum -= secondaryDelta
        secondaryDecButton.performClick()
        tickSumText.assertTextContains("$sum")

        sum -= primaryDelta
        primaryDecButton.performClick()
        tickSumText.assertTextContains("$sum")

        sum -= primaryDelta
        primaryDecButton.performClick()
        tickSumText.assertTextContains("$sum")

        sum -= secondaryDelta
        secondaryDecButton.performClick()
        tickSumText.assertTextContains("$sum")

        sum += secondaryDelta
        secondaryIncButton.performClick()
        tickSumText.assertTextContains("$sum")

        sum += primaryDelta
        primaryIncButton.performClick()
        tickSumText.assertTextContains("$sum")
    }
}
