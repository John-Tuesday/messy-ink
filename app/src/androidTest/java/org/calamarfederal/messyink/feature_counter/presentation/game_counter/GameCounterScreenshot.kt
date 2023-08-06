package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.data.insertPrettyData
import org.calamarfederal.messyink.feature_counter.data.loadPrettyCounterPlayer1
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.navigateToGameCounter
import org.calamarfederal.messyink.navigation.MessyInkNavHost
import org.calamarfederal.messyink.saveScreenshot
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class GameCounterScreenshot {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dao: CounterTickDao

    lateinit var navController: NavHostController

    @BindValue
    val testContent: OnCreateHookImpl = object : OnCreateHookImpl() {
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

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            dao.insertPrettyData(composeRule.activity.resources)
        }
        val counterId = loadPrettyCounterPlayer1(composeRule.activity.resources).id
        composeRule.runOnIdle {
            navController.navigateToGameCounter(counterId = counterId)
        }
    }

    @Test
    fun `save screenshot with pretty data`() {
        val img = composeRule.onRoot().captureToImage()
        saveScreenshot("game-counter.png", img.asAndroidBitmap())
    }
}
