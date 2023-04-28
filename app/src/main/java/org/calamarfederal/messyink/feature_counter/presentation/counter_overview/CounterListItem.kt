package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.ui.theme.TonalElevation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CounterListItem(
    counter: UiCounter,
    modifier: Modifier = Modifier,
    summaryNumber: Double? = null,
    selected: Boolean = false,
    multiSelect: Boolean = false,
) {
    ListItem(
        modifier = modifier,
        headlineText = {
            Text(
                text = counter.name,
                style = with(LocalTextStyle.current) {
                    if (selected) copy(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                    else this
                }
            )
        },
        leadingContent = summaryNumber?.let { { Text(text = it.toStringAllowShorten()) } },
        trailingContent = {
            AnimatedVisibility(visible = selected && multiSelect) {
                Icon(Icons.Filled.Check, null)
            }
        },
        tonalElevation = if (!selected)
            LocalAbsoluteTonalElevation.current
        else
            TonalElevation.heightOfNext(LocalAbsoluteTonalElevation.current, minimumLayer = 3)
    )
}
