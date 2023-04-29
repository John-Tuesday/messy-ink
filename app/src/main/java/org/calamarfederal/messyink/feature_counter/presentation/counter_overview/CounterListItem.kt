package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.TonalElevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CounterListItem(
    counter: UiCounter,
    modifier: Modifier = Modifier,
    summaryNumber: Double? = null,
    selected: Boolean = false,
    multiSelect: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.headlineLarge,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
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
        overlineContent = {
            Text("ID: ${counter.id}")
        },
        supportingContent = {
            Column {
                Text("modified: ${counter.timeModified.toLocalDateTime(timeZone).date}")
//                Text("created: ${counter.timeCreated.toLocalDateTime(timeZone).date}")
            }
        },
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
