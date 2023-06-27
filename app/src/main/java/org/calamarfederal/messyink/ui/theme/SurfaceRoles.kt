package org.calamarfederal.messyink.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import org.calamarfederal.messyink.ui.theme.SurfaceRoles.*

/**
 * # Material 3 Surface Roles
 *
 * replaces surface at elevation
 */
enum class SurfaceRoles {
    /**
     * Lowest Emphasis
     *
     * New role, does not map directly to surface at elevation
     */
    SurfaceContainerLowest, // new

    /**
     * Lesser Emphasis
     *
     * replaces Surface at +1
     */
    SurfaceContainerLow,    // +1

    /**
     * New default, Medium Emphasis
     *
     * replaces Surface at +2
     */
    SurfaceContainer,       // +2

    /**
     * Greater Emphasis
     *
     * replaces Surface at +3 (optionally also +4)
     */
    SurfaceContainerHigh,   // +3

    /**
     * Greatest Emphasis
     *
     * replaces Surface Variant (optionally also +5)
     */
    SurfaceContainerHighest,// SurfaceVariant
}

/**
 * Convenience function to get the corresponding property from [ColorScheme]
 */
fun ColorScheme.surfaceRole(role: SurfaceRoles): Color = when (role) {
    SurfaceContainerLowest  -> surfaceContainerLowest
    SurfaceContainerLow     -> surfaceContainerLow
    SurfaceContainer        -> surfaceContainer
    SurfaceContainerHigh    -> surfaceContainerHigh
    SurfaceContainerHighest -> surfaceContainerHighest
}

/**
 * Convert from tonal elevation to the new Surface Role
 */
fun MaterialLevel.toSurfaceRole() = when (level) {
    1    -> SurfaceContainerLow
    2    -> SurfaceContainer
    3, 4 -> SurfaceContainerHigh
    5    -> SurfaceContainerHighest
    else -> null
}
