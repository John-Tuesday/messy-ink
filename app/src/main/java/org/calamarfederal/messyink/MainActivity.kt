package org.calamarfederal.messyink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import org.calamarfederal.messyink.navigation.MessyInkEntry
import org.calamarfederal.messyink.navigation.appnode.FeatureCounter
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * Main (and only) Activity & Hilt entry point
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Simply enter into the specified Feature Graph
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MessyInkEntry(
                startDestination = FeatureCounter,
            )
        }
    }
}
