package org.calamarfederal.messyink.common.presentation.compose.material3.exposed

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.Dp

/**
 * exposed facsimile of [ButtonElevation]
 *
 * @property[default]
 * @property[pressed]
 * @property[focused]
 * @property[hovered]
 * @property[disabled]
 */
@Stable
data class ExposedButtonElevation(
    val default: Dp,
    val pressed: Dp,
    val focused: Dp,
    val hovered: Dp,
    val disabled: Dp,
)

/**
 * facsimile of internal [ButtonElevation] implementation.
 *
 * continuously animate between [Interaction] events. The same as [shadowElevation]
 */
@Composable
fun ExposedButtonElevation.tonalElevation(
    enabled: Boolean,
    interactionSource: InteractionSource,
): State<Dp> = animateElevation(enabled = enabled, interactionSource = interactionSource)

/**
 * facsimile of internal [ButtonElevation] implementation
 *
 * continuously animate between [Interaction] events. The same as [tonalElevation]
 */
@Composable
fun ExposedButtonElevation.shadowElevation(
    enabled: Boolean,
    interactionSource: InteractionSource,
): State<Dp> = animateElevation(enabled = enabled, interactionSource = interactionSource)
