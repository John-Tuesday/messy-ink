package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateCounterScreen(
    counter: UiCounter,
    onNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    onDelete: () -> Unit,
) {
    Scaffold(
        topBar = {
            CreateCounterAppBar(
                onClose = onCancel,
                onDone = onDone,
                onDelete = onDelete,
            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            CreateCounterLayout(counter = counter, onNameChange = onNameChange)
        }
    }
}

@Composable
private fun CreateCounterLayout(
    counter: UiCounter,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = counter.name,
            onValueChange = onNameChange,
            label = { Text("name") },
            placeholder = { Text(counter.name) }
        )

        var expand by remember { mutableStateOf(false) }
        ExpandableDetail(
            counter = counter,
            expand = expand,
            onShowLess = { expand = false },
            onShowMore = { expand = true },
        )
    }
}

private fun Instant.toDateString(): String = "${toLocalDateTime(TimeZone.UTC).date}"

@Composable
private fun ExpandableDetail(
    counter: UiCounter,
    expand: Boolean,
    onShowLess: () -> Unit,
    onShowMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = expand,
            label = "show more details box animation",
        ) { showMore ->
            Column {
                Row {
                    Button(
                        onClick = if (showMore) onShowLess else onShowMore,
                    ) {
                        Box(modifier.size(ButtonDefaults.IconSize)) {
                            if (showMore)
                                Icon(Icons.Filled.ExpandLess, "show less")
                            else
                                Icon(Icons.Filled.ExpandMore, "show more")
                        }
                        Spacer(modifier.size(ButtonDefaults.IconSpacing))
                        Text("Details")
                    }
                }
                Text(
                    text = "id: ${counter.id}\nmodified: ${counter.timeModified.toDateString()} created: ${counter.timeCreated.toDateString()}",
                    maxLines = if (!showMore) 1 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCreateScreen() {
    MessyInkTheme {
        CreateCounterScreen(
            counter = previewUiCounters.first(),
            onNameChange = {},
            onDelete = {},
            onDone = {},
            onCancel = {},
        )
    }
}
