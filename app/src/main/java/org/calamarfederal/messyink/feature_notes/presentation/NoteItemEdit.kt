package org.calamarfederal.messyink.feature_notes.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.common.compose.Placeholder
import org.calamarfederal.messyink.feature_notes.presentation.state.UiCheckState
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem

@Preview
@Composable
fun PreviewItemsEdit() {
    NoteItemEditForum(item = UiNoteItem(noteId = 1L, id = 2L), onChange = {})
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun NoteItemEditForum(
    item: UiNoteItem,
    onChange: (UiNoteItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowColumn(
        modifier = modifier
    ) {
        ShortFields(item, onChange)

        OutlinedTextField(
            value = item.description,
            onValueChange = { onChange(item.copy(description = it)) },
            placeholder = { Placeholder("description") },
            textStyle = MaterialTheme.typography.bodyMedium,
//            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
//            keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Next) },
            modifier = Modifier.wrapContentHeight()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ShortFields(
    item: UiNoteItem,
    onChange: (UiNoteItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center,
    ) {
        val focusManager = LocalFocusManager.current
        TextField(
            value = item.subtitle,
            onValueChange = { onChange(item.copy(subtitle = it)) },
            placeholder = { Placeholder("subtitle") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Next) },
        )
        TextField(
            value = item.name,
            onValueChange = { onChange(item.copy(name = it)) },
            placeholder = { Placeholder("name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Next) },
        )
        TextField(
            value = item.field,
            onValueChange = { onChange(item.copy(field = it)) },
            placeholder = { Placeholder("field") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Next) },
        )
        ChooseCheckState(
            checkState = item.checked,
            onCheckChanged = { onChange(item.copy(checked = it)) }
        )
        /**
         * # Description
         */

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseCheckState(
    checkState: UiCheckState,
    onCheckChanged: (UiCheckState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        TextField(
            value = checkState.name,
            onValueChange = {},
            label = { Text("Check") },
            singleLine = true,
            readOnly = true,
            modifier = Modifier.menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            for (check in UiCheckState.values() /*UiCheckState.entries*/) {
                DropdownMenuItem(
                    text = { Text(check.name) },
                    onClick = { onCheckChanged(check); expanded = false })
            }
        }
    }
}


@Composable
private fun ItemMoreOptionsMenu(
    options: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        var expanded by remember { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.MoreVert, "Show Item Options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            for ((name, onClick) in options)
                DropdownMenuItem(text = { Text(text = name) }, onClick = onClick)
        }
    }
}

