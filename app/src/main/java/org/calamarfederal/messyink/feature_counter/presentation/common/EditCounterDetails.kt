package org.calamarfederal.messyink.feature_counter.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.TimeZone
import org.calamarfederal.messyink.common.compose.toDbgString
import org.calamarfederal.messyink.feature_counter.domain.GetTime
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.previewUiCounters

/**
 * Card for editing counter-level details
 */
@Composable
fun EditCounterDetailsCard(
    counter: UiCounter,
    onChange: (UiCounter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = "Counter Details",
                style = MaterialTheme.typography.titleLarge,
            )
            EditName(
                name = counter.name,
                onSubmit = { onChange(counter.copy(name = it)) },
            )
            Text(
                text = counter.timeCreated.toDbgString(
                    tz = TimeZone.currentSystemDefault(),
                    now = { System.now() },
                )
            )
        }
    }
}

@Composable
private fun EditName(
    name: String,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = "Name", style = MaterialTheme.typography.labelLarge)
        var input by rememberSaveable(name) { mutableStateOf(name) }
        TextField(
            value = input,
            onValueChange = { input = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions { onSubmit(input) }
        )
    }
}

@Preview
@Composable
private fun EditCounterDetailsPreview() {
    EditCounterDetailsCard(
        counter = previewUiCounters.first(),
        onChange = {}
    )
}
