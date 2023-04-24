package org.calamarfederal.messyink.feature_notes.presentation.note_edit

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.calamarfederal.messyink.feature_notes.presentation.common.NoteItemListItemDefaults

@Composable
private fun TextStyle.asPlaceholder() = copy(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Light)

internal val NoteItemListItemDefaults.Subtitle.placeholderStyle: TextStyle
    @Composable get() = MaterialTheme.typography.labelSmall.asPlaceholder()
internal val NoteItemListItemDefaults.Subtitle.editStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium
internal val NoteItemListItemDefaults.Description.placeholderStyle: TextStyle
    @Composable get() = MaterialTheme.typography.labelSmall.asPlaceholder()
internal val NoteItemListItemDefaults.Description.editStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium
internal val NoteItemListItemDefaults.NameAndField.editStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium
internal val NoteItemListItemDefaults.NameAndField.placeholderStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium.asPlaceholder()

