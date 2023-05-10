package org.calamarfederal.messyink.common.compose.exposed

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.calamarfederal.messyink.ui.theme.TonalElevation

/**
 * Expose the private values for elevation and color in [ButtonDefaults]
 */
object ExposedButtonDefaults {

    /**
     * Ripped from the internal FilledButtonTokens
     */
    const val DisabledContainerOpacity: Float = 0.12f

    /**
     * facsimile of all button's default implementation
     *
     * [onSurface][MaterialTheme.colorScheme.onSurface] but with alpha = [DisabledContainerOpacity]
     */
    val DisabledContainerColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContainerOpacity)

    /**
     * Ripped from the internal FilledButtonTokens
     */
    const val DisabledContentOpacity: Float = 0.38f

    /**
     * facsimile of all button's default implementation
     *
     * [onSurface][MaterialTheme.colorScheme.onSurface] but with alpha = [DisabledContentOpacity]
     */
    val DisabledContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContentOpacity)

    /**
     * exposed facsimile of [ButtonDefaults.buttonColors]
     */
    val buttonColors: ExposedButtonColors
        @Composable get() = ExposedButtonColors(
            container = MaterialTheme.colorScheme.primary,
            content = MaterialTheme.colorScheme.onPrimary,
            disabledContainer = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContainerOpacity),
            disabledContent = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContentOpacity),
        )

    /**
     * exposed facsimile of [ButtonDefaults.elevatedButtonColors]
     */
    val elevatedButtonColors: ExposedButtonColors
        @Composable get() = ExposedButtonColors(
            container = MaterialTheme.colorScheme.surface,
            content = MaterialTheme.colorScheme.primary,
            disabledContainer = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContainerOpacity),
            disabledContent = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContentOpacity),
        )

    /**
     * exposed facsimile of [ButtonDefaults.filledTonalButtonColors]
     */
    val filledTonalButtonColors: ExposedButtonColors
        @Composable get() = ExposedButtonColors(
            container = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainer = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContainerOpacity),
            disabledContent = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContentOpacity),
        )

    val outlinedButtonColors: ExposedButtonColors
        @Composable get() = ExposedButtonColors(
            container = Color.Transparent,
            content = MaterialTheme.colorScheme.primary,
            disabledContainer = Color.Transparent,
            disabledContent = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledContentOpacity),
        )

    /**
     * facsimile of internal implementation
     */
    val buttonElevation: ExposedButtonElevation = ExposedButtonElevation(
        default = TonalElevation.layers[0],
        pressed = TonalElevation.layers[0],
        focused = TonalElevation.layers[0],
        hovered = TonalElevation.layers[1],
        disabled = TonalElevation.layers[0]
    )

    /**
     * facsimile of internal implementation
     */
    val elevatedButtonElevation: ExposedButtonElevation = ExposedButtonElevation(
        default = TonalElevation.layers[1],
        pressed = TonalElevation.layers[1],
        focused = TonalElevation.layers[1],
        hovered = TonalElevation.layers[2],
        disabled = TonalElevation.layers[0],
    )

    /**
     * facsimile of internal implementation; just a alias for [buttonElevation]
     */
    val filledTonalButtonElevation: ExposedButtonElevation = buttonElevation
}
