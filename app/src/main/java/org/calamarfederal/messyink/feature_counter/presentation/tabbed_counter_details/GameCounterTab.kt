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
    var showEditCounter by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        GameCounterLayout(
            counter = counter,
            tickSum = tickSum ?: 0.00,
            onAddTick = onAddTick,
            onReset = onResetCounter,
            onUndo = {},
            onRedo = {},
            onEditCounter = { showEditCounter = true },
            modifier = Modifier.padding(16.dp),
        )
    }
    AnimatedVisibility(visible = showEditCounter) {
        Dialog(onDismissRequest = { showEditCounter = false }) {
            EditCounterDetailsCard(counter = counter, onChange = onCounterChange)
        }
    }
}
