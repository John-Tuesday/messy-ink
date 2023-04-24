package org.calamarfederal.messyink.feature_notes.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.calamarfederal.messyink.feature_notes.presentation.state.UiCheckState
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem

object NoteItemListItemDefaults {
    object Subtitle {
        val style: TextStyle
            @Composable get() = MaterialTheme.typography.titleSmall.copy(
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Justify,
                baselineShift = BaselineShift.None,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Bottom,
                    trim = LineHeightStyle.Trim.Both
                ),
            )

    }

    object Description {
        val style: TextStyle
            @Composable get() = MaterialTheme.typography.bodySmall.copy(
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Justify,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Top,
                    trim = LineHeightStyle.Trim.Both
                ),
            )
    }

    object NameAndField {
        val style: TextStyle
            @Composable get() = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Top,
                    trim = LineHeightStyle.Trim.Both
                ),
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItemListItem(
    item: UiNoteItem,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineText = { ListItemPartViewNameAndField(name = item.name, field = item.field) },
        overlineText = if (item.subtitle.isBlank()) null else { ->
            ListItemPartViewSubtitle(subtitle = item.subtitle)
        },
        supportingText = if (item.description.isBlank()) null else { ->
            ListItemPartViewDescription(description = item.description)
        },
        trailingContent = {
            ListItemPartViewCheck(
                checkState = item.checked,
                onCheckChange = {},
                enabled = true,
            )

        }
    )
}

@Composable
fun ListItemPartViewNameAndField(
    name: String,
    field: String,
    modifier: Modifier = Modifier,
    style: TextStyle = NoteItemListItemDefaults.NameAndField.style,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = name,
            style = style,
            modifier = Modifier.alignByBaseline()
        )

        Spacer(Modifier.widthIn(min = 1.dp))

        Text(
            text = field,
            style = style,
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
fun ListItemPartViewSubtitle(
    subtitle: String,
    modifier: Modifier = Modifier,
    style: TextStyle = NoteItemListItemDefaults.Subtitle.style,
) {
    Text(
        modifier = modifier,
        text = subtitle,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
    )
}

@Composable
fun ListItemPartViewDescription(
    description: String,
    modifier: Modifier = Modifier,
    style: TextStyle = NoteItemListItemDefaults.Description.style,
) {
    Text(
        modifier = modifier,
        text = description,
        style = style,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun ListItemPartViewCheck(
    checkState: UiCheckState,
    onCheckChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val isChecked = checkState == UiCheckState.Checked
    FilledTonalIconToggleButton(
        modifier = modifier.wrapContentSize(),
        checked = isChecked,
        onCheckedChange = onCheckChange,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
    ) {
        when (checkState) {
            UiCheckState.Checked -> Icon(Icons.Filled.Check, null)
            UiCheckState.Unchecked -> Icon(Icons.Filled.Check, null)
            UiCheckState.Partial -> Icon(Icons.Outlined.Check, null)
            else -> Text(checkState.name)
        }
    }
}
