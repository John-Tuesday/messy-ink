package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MaterialLevel
import org.calamarfederal.messyink.ui.theme.MessyInkTheme
import org.calamarfederal.messyink.ui.theme.toMaterialLevelCiel

@Composable
internal fun CounterListItem(
    counter: UiCounter,
    modifier: Modifier = Modifier,
    summaryNumber: Double? = null,
    selected: Boolean = false,
    multiSelect: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.headlineLarge,
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = counter.name,
                style = if (selected) textStyle.merge(
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                ) else textStyle,
            )
        },
        leadingContent = {
            Text(
                text = summaryNumber?.toStringAllowShorten() ?: "--",
                style = if (selected) textStyle.merge(
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                ) else textStyle,
            )
        },
        trailingContent = {
            AnimatedVisibility(visible = selected && multiSelect) {
                Icon(Icons.Filled.Check, null)
            }
        },
        tonalElevation = if (!selected)
            LocalAbsoluteTonalElevation.current
        else
            LocalAbsoluteTonalElevation.current
                .toMaterialLevelCiel()
                .coerceAtLeast(MaterialLevel(3)).elevation
    )
}

@Preview
@Composable
private fun PreviewCounterItem() {
    MessyInkTheme {
        CounterListItem(counter = previewUiCounters.first())
    }
}
