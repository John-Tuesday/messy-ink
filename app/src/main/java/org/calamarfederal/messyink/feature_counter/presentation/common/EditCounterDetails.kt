package org.calamarfederal.messyink.feature_counter.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardOptions.Companion
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter

@Composable
internal fun EditCounterDetailsCard(
    counter: UiCounter,
    onChange: (UiCounter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = modifier.padding(16.dp)) {
            Text("Counter Details")
            EditName(
                name = counter.name,
                onSubmit = { onChange(counter.copy(name = it)) },
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
        Text("Name")
        var input by rememberSaveable(name) { mutableStateOf(name)}
        TextField(
            value = input,
            onValueChange = { input = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions { onSubmit(input) }
        )
    }
}

