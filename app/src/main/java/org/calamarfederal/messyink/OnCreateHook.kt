package org.calamarfederal.messyink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import org.calamarfederal.messyink.navigation.FeatureCounter
import org.calamarfederal.messyink.navigation.MessyInkNavHost
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import javax.inject.Inject

/**
 * ## Entry point into a ComponentActivity
 */
fun interface OnCreateHook {
    /**
     * meant to be called inside of [ComponentActivity.onCreate] and should call [ComponentActivity.setContent]
     */
    operator fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?)
}

/**
 * Production Implementation of [OnCreateHook]
 *
 * left as `open class` so tests can quickly replace by
 * `@BindValue val hook: OnCreateHookImpl = object : OnCreateHookImpl { /* TODO */ }`
 */
open class OnCreateHookImpl @Inject constructor() : OnCreateHook {
    override fun invoke(activity: ComponentActivity, savedInstanceState: Bundle?) {
        with(activity) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            setContent {
                MessyInkTheme {
                    MessyInkNavHost.NavHostGraph()
                }
            }
        }
    }
}
