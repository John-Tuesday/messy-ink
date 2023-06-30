package org.calamarfederal.messyink.feature_counter.presentation.counter_history

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

class CounterHistoryTestContent : OnCreateHookImpl() {
    override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
        with(activity) {
            setContent {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "test"
                ) {
                    composable("test") { entry ->

                        val viewModel: CounterHistoryViewModel = hiltViewModel(entry)

                        val counter by viewModel.counter.collectAsState()
                        val ticks by viewModel.ticks.collectAsState()
                        val tickSum by viewModel.tickSum.collectAsState()
                        val tickAverage by viewModel.tickAverage.collectAsState()
                        val graphRange by viewModel.graphRange.collectAsState()
                        val graphDomain by viewModel.graphDomain.collectAsState()
                        val graphDomainLimits by viewModel.graphDomainLimits.collectAsState()
                        val graphDomainOptions = viewModel.graphDomainOptions

                        CounterHistoryScreen(
                            counter = counter,
                            ticks = ticks,
                            tickSum = tickSum,
                            tickAverage = tickAverage,
                            graphRange = graphRange,
                            graphDomain = graphDomain,
                            graphDomainLimits = graphDomainLimits,
                            graphDomainOptions = graphDomainOptions.toList(),
                            changeGraphDomain = { viewModel.changeGraphDomain(it) },
                            onAddTick = viewModel::addTick,
                            onDeleteTick = viewModel::deleteTick,
                            onResetCounter = viewModel::resetCounter,
                            onCounterChange = viewModel::changeCounter,
                            onNavigateUp = { navController.navigateUp() },
                        )
                    }
                }
            }
        }
    }
}
