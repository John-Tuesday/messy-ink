package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.calamarfederal.messyink.feature_counter.presentation.common.EditCounterDetailsCard
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterLayout
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter

@Composable
internal fun GameCounterTab(
    counter: UiCounter,
    tickSum: Double?,
    onAddTick: (Double) -> Unit,
    onResetCounter: () -> Unit,
    onCounterChange: (UiCounter) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("This should be a dedicated screen not a tabbed screen")
    }
}
