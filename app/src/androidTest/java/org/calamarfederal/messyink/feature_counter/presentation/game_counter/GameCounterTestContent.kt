package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.gameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter

class GameCounterTestContent(
    private val counter: UiCounter,
    private val sum: Double = 0.00,
    private val primaryInc: Double = 5.00,
    private val secondaryInc: Double = 1.00,
) : OnCreateHookImpl() {
    override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
        with(activity) {
            setContent {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "test",
                ) {
                    composable(route = "test") {
                        GameCounterScreen(
                            counter = counter,
                            tickSum = sum,
                            primaryIncrement = primaryInc,
                            onChangePrimaryIncrement = {},
                            secondaryIncrement = secondaryInc,
                            onChangeSecondaryIncrement = {},
                            onAddTick = {},
                            onUndo = {},
                            onRedo = {},
                            onReset = {},
                            onEditCounter = {},
                        )
                    }
                }
            }
        }
    }
}
