package org.calamarfederal.messyink.feature_notes.presentation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

fun interface NoteDrawer {
    operator fun invoke(content: @Composable (ColumnScope.() -> Unit)?)
}

