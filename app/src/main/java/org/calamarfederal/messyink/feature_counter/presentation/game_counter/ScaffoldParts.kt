package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import android.view.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GameCounterAppBar(
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateUp: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {},
        actions = {
            IconButton(onClick = onUndo) {
                Icon(Icons.Filled.Undo, "undo")
            }
            IconButton(onClick = onReset) {
                Icon(Icons.Filled.RestartAlt, "reset amount")
            }
            IconButton(onClick = onRedo) {
                Icon(Icons.Filled.Redo, "undo")
            }
        },
        navigationIcon = {
            if (onNavigateUp != null) {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.Filled.ArrowBack, "navigate up")
                }
            }
        },
    )
}
