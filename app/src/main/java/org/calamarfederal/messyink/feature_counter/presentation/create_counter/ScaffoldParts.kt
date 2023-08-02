package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import org.calamarfederal.messyink.R

/**
 * Provide m3 Fullscreen Dialog header for [CreateCounterScreen]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateCounterAppBar(
    onClose: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    enableDone: Boolean = false,
    title: String = "",
) {
    MediumTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag(CreateCounterTestTags.CloseButton),
            ) {
                Icon(Icons.Filled.Close, stringResource(R.string.close))
            }
        },
        actions = {
            FilledTonalIconButton(
                onClick = onDone,
                enabled = enableDone,
                modifier = Modifier.testTag(CreateCounterTestTags.SubmitButton),
            ) {
                Icon(Icons.Filled.Done, stringResource(R.string.save))
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
