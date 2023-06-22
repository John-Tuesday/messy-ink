package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.Binds
import dagger.Module
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHook
import org.calamarfederal.messyink.OnCreateHookImpl
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

@HiltAndroidTest
class CreateCounterScreenKtTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @BindValue
    val contentSetter: OnCreateHookImpl = object : OnCreateHookImpl() {
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
                            val counterSupport by vm.counterSupport.collectAsState()

                            CreateCounterScreen(
                                counterSupport = counterSupport,
                                onNameChange = vm::changeName,
                                onCancel = { vm.discardCounter(); },
                                onDone = { vm.finalizeCounter(); },
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
    }

    @Test
    fun `Disallow creating a counter with empty field`() {
        composeRule.onNodeWithContentDescription("finalize counter").assertIsNotEnabled()
    }
}
