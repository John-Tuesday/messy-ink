package org.calamarfederal.messyink.feature_notes.navigation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief

@Composable
internal fun ColumnScope.ListOfNotesDrawerContent(
    selected: Long?,
    notes: List<UiNoteBrief>,
    onClick: (UiNoteBrief) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = notes, key = {it.id}) { note ->
            TextButton(enabled = note.id != selected, onClick = {onClick(note)}, modifier = Modifier.fillMaxWidth()) {
                Text(note.title)
            }
        }
    }
}
