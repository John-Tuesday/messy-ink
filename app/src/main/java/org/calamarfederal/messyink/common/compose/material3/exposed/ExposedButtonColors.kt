package org.calamarfederal.messyink.common.compose.material3.exposed

import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * functional equivalent to [ButtonColors], but with exposed values
 *
 * @property[container]
 * @property[content]
 * @property[disabledContainer]
 * @property[disabledContent]
 */
@Immutable
data class ExposedButtonColors(
    val container: Color,
    val content: Color,
    val disabledContainer: Color,
    val disabledContent: Color,
)
