package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter

@Composable
internal fun GameCounterTab(
    counter: UiCounter,
    tickSum: Double?,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Text(text = "${counter.name} : $tickSum")
    }
}
