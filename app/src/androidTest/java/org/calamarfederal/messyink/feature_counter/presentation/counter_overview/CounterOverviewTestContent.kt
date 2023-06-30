package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterNavHost

class CounterOverviewContent : OnCreateHookImpl() {
    override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
        with(activity) {
            setContent {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "test"
                ) {
                    composable("test") { entry ->
                        val viewModel: CounterOverviewViewModel = hiltViewModel(entry)
                        val counters by viewModel.countersState.collectAsState()
                        val tickSum by viewModel.ticksSumState.collectAsState()

                        CounterOverviewScreen(
                            counters = counters,
                            tickSums = tickSum,
                            onCounterIncrement = { viewModel.incrementCounter(it.id) },
                            onCounterDecrement = { viewModel.decrementCounter(it.id) },
                            onDeleteCounter = { viewModel.deleteCounter(it.id) },
                            onClearCounterTicks = { viewModel.clearCounterTicks(it.id) },
                            onCreateCounter = {},
                            onNavigateToCounterDetails = {},
                            onNavigateToCounterGameMode = {},
                            onNavigateToCounterEdit = {},
                        )
                    }
                }

            }
        }

    }
}
