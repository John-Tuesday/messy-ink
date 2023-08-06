package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.R
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.insertPrettyData
import org.calamarfederal.messyink.feature_counter.data.loadPrettyCounterWorkout
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterHistoryNode.navigateToCounterHistory
import org.calamarfederal.messyink.navigation.MessyInkNavHost
import org.calamarfederal.messyink.saveScreenshot
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CounterHistoryScreenshots {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dao: CounterTickDao

    @BindValue
    val prettyContent: OnCreateHookImpl = object : OnCreateHookImpl() {
        override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
            with(activity) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                setContent {
                    MessyInkTheme {
                        val navHostController = rememberNavController()
                        MessyInkNavHost.NavHostGraph(navHostController)
                        LaunchedEffect(navHostController) {
                            navController = navHostController
                        }
                    }
                }
            }
        }
    }

    lateinit var navController: NavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            dao.insertPrettyData(composeRule.activity.resources)
        }
        val counterId = loadPrettyCounterWorkout(composeRule.activity.resources).id
        composeRule.runOnIdle {
            navController.navigateToCounterHistory(counterId = counterId)
        }
    }

    private val tickLogNavButton
        get() = composeRule.onNode(
            hasText(composeRule.activity.getString(R.string.tick_log)) and hasClickAction()
        )
    private val tickGraphNavButton
        get() = composeRule
            .onNode(hasText(composeRule.activity.getString(R.string.tick_graph)) and hasClickAction())

    @Test
    fun `Graph View Screenshot`() {
        val img = composeRule.onRoot().captureToImage()
        saveScreenshot("history-graph.png", img.asAndroidBitmap())
    }

    @Test
    fun `Tick Log View Screenshot`() {
        tickLogNavButton.performClick()
        val img = composeRule.onRoot().captureToImage()
        saveScreenshot("history-log.png", img.asAndroidBitmap())
    }
}
