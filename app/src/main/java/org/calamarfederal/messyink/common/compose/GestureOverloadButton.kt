package org.calamarfederal.messyink.common.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.common.compose.exposed.ExposedButtonColors
import org.calamarfederal.messyink.common.compose.exposed.ExposedButtonDefaults
import org.calamarfederal.messyink.common.compose.exposed.ExposedButtonElevation
import org.calamarfederal.messyink.common.compose.exposed.shadowElevation
import org.calamarfederal.messyink.common.compose.exposed.tonalElevation


/**
 * replicate the default [Button] except add optional [onLongClick] and [onDoubleClick] callbacks
 *
 * defaults are taken from [ButtonDefaults] or facsimiled when necessary
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Button(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ExposedButtonColors = ExposedButtonDefaults.buttonColors,
    elevation: ExposedButtonElevation? = ExposedButtonDefaults.buttonElevation,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit = {},
    onClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    onDoubleClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val containerColor by rememberUpdatedState(if (enabled) colors.container else colors.disabledContainer)
    val contentColor by rememberUpdatedState(if (enabled) colors.content else colors.disabledContent)
    val tonalElevation = elevation?.tonalElevation(enabled, interactionSource)?.value ?: 0.dp
    val shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp

    Surface(
        modifier = modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = rememberRipple(),
            role = Role.Button,
            enabled = enabled,
            onClick = onClick,
            onClickLabel = onClickLabel,
            onLongClick = onLongClick,
            onLongClickLabel = onLongClickLabel,
            onDoubleClick = onDoubleClick,
        ),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Row(
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content,
                )
            }
        }
    }
}
