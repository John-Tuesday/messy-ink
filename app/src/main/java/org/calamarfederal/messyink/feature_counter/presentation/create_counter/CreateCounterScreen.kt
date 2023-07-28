package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * # Create or Edit Counter
 * ## Intended to be an m3 implementation of a Fullscreen Dialog
 *
 * [onCancel] should discard changes and [onDone] should save them.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateCounterScreen(
    counterName: TextFieldValue,
    counterNameError: Boolean,
    counterNameHelp: String?,
    onNameChange: (TextFieldValue) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    isEditCounter: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            CreateCounterAppBar(
                onClose = onCancel,
                onDone = onDone,
                enableDone = !counterNameError,
                scrollBehavior = scrollBehavior,
                title = if (isEditCounter) "Edit Counter" else "Create Counter",
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
                counterName = counterName,
                counterNameError = counterNameError,
                counterNameHelp = counterNameHelp,
                onNameChange = onNameChange,
                onDone = onDone,
            )
        }
    }
}

/**
 * Layout of all user-controlled  Counter attributes
 *
 * currently only Counter.name
 */
@Composable
private fun CreateCounterLayout(
    counterName: TextFieldValue,
    counterNameError: Boolean,
    counterNameHelp: String?,
    onNameChange: (TextFieldValue) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val titleFocusRequester = remember { FocusRequester() }

        EditTitleField(
            title = "",
            titleInput = counterName,
            onTitleChange = { onNameChange(it) },
            onDone = { if (!counterNameError) onDone() },
            isError = counterNameError,
            helpText = counterNameHelp,
            focusRequester = titleFocusRequester,
        )

        HorizontalDivider()

        LaunchedEffect(Unit) {
            titleFocusRequester.requestFocus()
        }
    }
}

@Composable
private fun EditTitleField(
    title: String,
    titleInput: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    helpText: String? = null,
    focusRequester: FocusRequester = FocusRequester(),
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
            modifier = Modifier
                .focusRequester(focusRequester)
                .semantics { if (isError) error(helpText ?: "Unknown Error") }
                .testTag(CreateCounterTestTags.TitleTextField)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCreateScreen() {
    MessyInkTheme {
        CreateCounterScreen(
            counterName = TextFieldValue("Test Counter Name"),
            counterNameError = false,
            counterNameHelp = null,
            isEditCounter = false,
            onNameChange = {},
            onDone = {},
            onCancel = {},
        )
    }
}
