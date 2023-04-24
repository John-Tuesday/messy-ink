package org.calamarfederal.messyink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.calamarfederal.messyink.navigation.MessyInkEntry
import org.calamarfederal.messyink.navigation.appnode.FeatureCounter
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessyInkEntry(
                startDestination = FeatureCounter,
            )
        }
    }
}
