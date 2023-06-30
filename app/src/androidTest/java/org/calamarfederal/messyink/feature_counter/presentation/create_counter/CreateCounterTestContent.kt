package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterNavHost
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport

class CreateCounterTestContent : OnCreateHookImpl() {
    var otherCounterSupport: UiCounterSupport? by mutableStateOf(null)
    var onCancelHook: () -> Unit by mutableStateOf({})
    var onDoneHook: () -> Unit by mutableStateOf({})
    override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
        with(activity) {
            setContent {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "test",
                ) {
                    composable(route = "test") {
                        val vm: CreateCounterViewModel = hiltViewModel(it)
                        val vmCounterSupport by vm.counterSupport.collectAsState()

                        CreateCounterScreen(
                            counterSupport = otherCounterSupport ?: vmCounterSupport,
                            onNameChange = vm::changeName,
                            onCancel = onCancelHook,
                            onDone = onDoneHook,
                        )
                    }
                }
            }
        }
    }
}
