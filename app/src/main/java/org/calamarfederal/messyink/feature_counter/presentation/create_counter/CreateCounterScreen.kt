package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    showNavigationIcon: Boolean,
) {
    Scaffold(
        topBar = {
            CreateCounterAppBar(
                onClose = onCancel,
                onDone = onDone,
                onDelete = onDelete,
                showNavigationIcon = showNavigationIcon,
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
    Column(modifier = modifier) {
        TextField(
            value = counter.name,
            onValueChange = onNameChange,
            label = { Text("name") },
            placeholder = { Text(counter.name) }
        )
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
            onCancel =  {},
            showNavigationIcon = true,
        )
    }
}
