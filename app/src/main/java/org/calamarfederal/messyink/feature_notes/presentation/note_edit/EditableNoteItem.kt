package org.calamarfederal.messyink.feature_notes.presentation.note_edit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.with
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.common.compose.Placeholder
import org.calamarfederal.messyink.feature_notes.presentation.common.ListItemPartViewNameAndField
import org.calamarfederal.messyink.feature_notes.presentation.common.ListItemPartViewSubtitle
import org.calamarfederal.messyink.feature_notes.presentation.common.NoteItemListItemDefaults
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem
import org.calamarfederal.messyink.feature_notes.presentation.state.uiNoteItemPreviews


@Preview
@Composable
private fun EditItemPrev() {
    EditableNoteItem(
        item = uiNoteItemPreviews().first(),
        onItemChange = {},
        isSelected = false,
        selectMode = false,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalTransitionApi::class,
)
@Composable
internal fun EditableNoteItem(
    item: UiNoteItem,
    onItemChange: (UiNoteItem) -> Unit,
    isSelected: Boolean,
    selectMode: Boolean,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        tonalElevation = LocalAbsoluteTonalElevation.current + when {
            isSelected -> 8.dp; else -> 0.dp
        },
        headlineText = {
            NameAndFieldText(
                name = item.name,
                field = item.field,
                onNameChange = { onItemChange(item.copy(name = it)) },
                onFieldChange = { onItemChange(item.copy(field = it)) },
                enabled = !selectMode,
            )
        },
        overlineText = {
            ItemSubtitle(
                subtitle = item.subtitle,
                onChange = { onItemChange(item.copy(subtitle = it)) },
                enabled = !selectMode,
                modifier = Modifier.fillMaxWidth()
            )
        },
        supportingText = {
            ItemDescription(
                description = item.description,
                onChange = { onItemChange(item.copy(description = it)) },
                enabled = !selectMode,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        leadingContent = {
            if (isSelected)
                Icon(Icons.Filled.Check, null, tint = LocalContentColor.current, modifier = modifier)
        },
        trailingContent = null,
    )

}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun NameAndFieldText(
    name: String,
    field: String,
    enabled: Boolean,
    onNameChange: (String) -> Unit,
    onFieldChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    var editMode by remember(enabled) { mutableStateOf(false) }

    TextButton(onClick = { editMode = true }) {
        AnimatedContent(targetState = editMode) { isEditMode ->
            if (isEditMode) {
                Row(modifier = modifier) {
                    val focusManager = LocalFocusManager.current

                    TextField(
                        value = name,
                        onValueChange = onNameChange,
                        singleLine = true,
                        enabled = enabled,
                        textStyle = textStyle,
                        placeholder = { Placeholder(text = "< name >", style = textStyle) },
                        keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Next) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .alignByBaseline()
                            .weight(1f),
                    )
                    Spacer(Modifier.widthIn(1.dp, 4.dp))
                    OutlinedTextField(
                        value = field,
                        onValueChange = onFieldChange,
                        singleLine = true,
                        enabled = enabled,
                        textStyle = textStyle,
                        placeholder = { Placeholder(text = "< field >", style = textStyle) },
                        keyboardActions = KeyboardActions { focusManager.moveFocus(FocusDirection.Exit) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        modifier = Modifier
                            .alignByBaseline()
                            .weight(1f),
                    )
                }
            } else {
                ListItemPartViewNameAndField(name = name, field = field)
            }
        }
    }

}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
private fun ItemSubtitle(
    subtitle: String,
    onChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var editMode by remember(enabled) { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val editFocusRequester = remember { FocusRequester() }

    TextButton(
        onClick = { editMode = true },
        enabled = enabled,
        modifier = modifier
            .onFocusChanged { if (!it.hasFocus && editMode) editMode = false }
            .focusable(enabled = enabled),
    ) {
        AnimatedContent(
            targetState = editMode,
            contentAlignment = Alignment.BottomCenter,
            transitionSpec = {
                when (targetState) {
                    true -> scaleIn(
                        initialScale = 1f, transformOrigin = TransformOrigin(.5f, 0f)
                    ) + expandIn(expandFrom = Alignment.BottomCenter) with fadeOut()

                    false -> fadeIn() with scaleOut(
                        transformOrigin = TransformOrigin(
                            .5f, 1f
                        )
                    ) + shrinkOut(shrinkTowards = Alignment.BottomCenter) + fadeOut()
                }
            },
            modifier = Modifier
                .align(Alignment.Bottom)
                .weight(1f),
        ) { isEditMode ->
            if (isEditMode) {
                OutlinedTextField(value = subtitle,
                    onValueChange = onChange,
                    singleLine = true,
                    placeholder = { Placeholder("subtitle") },
                    textStyle = NoteItemListItemDefaults.Subtitle.editStyle,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions { editMode = false },
                    enabled = enabled,
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.BottomStart)
                        .focusRequester(editFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) focusManager.moveFocus(FocusDirection.Enter)
                        }
                        .focusable(enabled = enabled))
                LaunchedEffect(Unit) {
                    editFocusRequester.requestFocus()
                }
            } else {
                ListItemPartViewSubtitle(
                    subtitle = subtitle.ifBlank { "Enter a subtitle" },
                    modifier = Modifier.wrapContentSize(align = Alignment.BottomStart),
                    style = if (subtitle.isBlank()) NoteItemListItemDefaults.Subtitle.placeholderStyle else NoteItemListItemDefaults.Subtitle.style
                )
            }
        }
    }
}

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)
@Composable
private fun ItemDescription(
    description: String,
    onChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var expandDescription by remember(enabled) { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val editFocusRequester = remember { FocusRequester() }

    TextButton(
        onClick = { expandDescription = true },
        enabled = enabled,
        modifier = modifier
            .onFocusChanged {
                if (!it.hasFocus && expandDescription) expandDescription = false
            }
            .focusable(enabled = enabled)
    ) {
        AnimatedContent(
            targetState = expandDescription,
            contentAlignment = Alignment.TopCenter,
            transitionSpec = {
                when (targetState) {
                    true -> scaleIn(
                        initialScale = 1f, transformOrigin = TransformOrigin(.5f, 1f)
                    ) + expandIn(expandFrom = Alignment.TopCenter) with fadeOut()

                    false -> fadeIn() with scaleOut(
                        transformOrigin = TransformOrigin(
                            .5f, 0f
                        )
                    ) + shrinkOut(shrinkTowards = Alignment.TopCenter) + fadeOut()
                }
            },
            modifier = Modifier
                .align(Alignment.Top)
                .weight(1f),
        ) { isExpanded ->
            if (isExpanded) {
                OutlinedTextField(
                    value = description,
                    onValueChange = onChange,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = { Placeholder("description") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions { expandDescription = false },
                    enabled = enabled,
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.TopStart)
                        .focusRequester(editFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) focusManager.moveFocus(FocusDirection.Enter)
                        }
                        .focusable(enabled = enabled),
                )
                LaunchedEffect(Unit) { editFocusRequester.requestFocus() }
            } else {
                Text(
                    text = description.ifBlank { "Enter a description" },
                    style = MaterialTheme.typography.labelMedium,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.wrapContentSize(align = Alignment.TopStart)
                )
            }

        }
    }
}
