package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import org.calamarfederal.messyink.feature_counter.presentation.state.NOID
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport
import org.calamarfederal.messyink.feature_counter.presentation.state.error
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * # Create or Edit [UiCounter]
 * ## Intended to be an m3 implementation of a Fullscreen Dialog
 *
 * [onCancel] should discard changes and [onDone] should save them.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateCounterScreen(
    counterSupport: UiCounterSupport,
    onNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            CreateCounterAppBar(
                onClose = onCancel,
                onDone = onDone,
                enableDone = !counterSupport.error,
                scrollBehavior = scrollBehavior,
                title = if (counterSupport.id == null || counterSupport.id == NOID)
                    "Create Counter" else "Edit Counter",
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            CreateCounterLayout(
                counterSupport = counterSupport,
                onNameChange = onNameChange,
                onDone = onDone,
            )
        }
    }
}

/**
 * Layout of all user-controlled  [UiCounter] attributes
 *
 * currently only [UiCounter.name]
 */
@Composable
private fun CreateCounterLayout(
    counterSupport: UiCounterSupport,
    onNameChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EditTitleField(
            title = counterSupport.nameInput,
            titleInput = counterSupport.nameInput,
            onTitleChange = onNameChange,
            onDone = { if (!counterSupport.error) onDone() },
            isError = counterSupport.nameError,
            helpText = counterSupport.nameHelp,
        )

        Divider()
    }
}

@Composable
private fun EditTitleField(
    title: String,
    titleInput: String,
    onTitleChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    helpText: String? = null,
) {

    Column(modifier = modifier) {
        Text(
            text = "Title:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = titleInput,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = title,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                )
            },
            isError = isError,
            supportingText = { helpText?.let { Text(it) } },
            textStyle = MaterialTheme.typography.titleLarge,
            singleLine = true,
            keyboardActions = KeyboardActions { onDone() },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCreateScreen() {
    MessyInkTheme {
        CreateCounterScreen(
            counterSupport = UiCounterSupport(),
            onNameChange = {},
            onDone = {},
            onCancel = {},
        )
    }
}
