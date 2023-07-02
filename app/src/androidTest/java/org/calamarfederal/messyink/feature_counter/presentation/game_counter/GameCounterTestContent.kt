package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode
import org.calamarfederal.messyink.feature_counter.presentation.navigation.GameCounterNode.gameCounterNode

class GameCounterTestContent(private val counterId: Long) : OnCreateHookImpl() {
    override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
        with(activity) {
            setContent {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = GameCounterNode.buildDestination(counterId),
                ) {
                    gameCounterNode(onEditCounter = {})
                }
            }
        }
    }
}
