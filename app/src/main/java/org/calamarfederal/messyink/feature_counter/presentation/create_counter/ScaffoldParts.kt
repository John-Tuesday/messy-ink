package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateCounterAppBar(
    onClose: () -> Unit,
    onDone: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showNavigationIcon: Boolean = true,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text("Create Counter?") },
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, "close")
                }
            }
        },
        actions = {
            FilledIconButton(onClick = onDone) {
                Icon(Icons.Filled.Done, "finalize counter")
            }
        }
    )
}
