package org.calamarfederal.messyink.ui.theme.catppuccin

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

val wip: Color get() = Color.Unspecified

val Flavor.PRIMARY: Color get() = pink
val Flavor.SECONDARY: Color get() = flamingo
val Flavor.TERTIARY: Color get() = lavender

fun Color.withLuminance(percent: Int): Color {
    val hsl = floatArrayOf(0f, 0f, 0f)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = (percent / 100f).coerceIn(0f..1f)
    return Color(ColorUtils.HSLToColor(hsl))
}

fun Color.inverse(): Color {
    val argb = toArgb()
    return Color(255 - argb.red, 255 - argb.green, 255 - argb.blue, alpha = 0xFF)
}

val Flavor.colorScheme: ColorScheme
    get() = ColorScheme(
        primary = PRIMARY.withLuminance(40),
        onPrimary = PRIMARY.withLuminance(100),
        primaryContainer = PRIMARY.withLuminance(90),
        onPrimaryContainer = PRIMARY.withLuminance(10),
        inversePrimary = PRIMARY.inverse(),
        secondary = SECONDARY.withLuminance(40),
        onSecondary = SECONDARY.withLuminance(100),
        secondaryContainer = SECONDARY.withLuminance(90),
        onSecondaryContainer = SECONDARY.withLuminance(10),
        tertiary = TERTIARY.withLuminance(40),
        onTertiary = TERTIARY.withLuminance(100),
        tertiaryContainer = TERTIARY.withLuminance(90),
        onTertiaryContainer = TERTIARY.withLuminance(10),
        background = base.withLuminance(98),
        onBackground = text.withLuminance(10),
        surface = surface0.withLuminance(98),
        onSurface = surface0.withLuminance(10),
        surfaceVariant = surface2.withLuminance(90),
        onSurfaceVariant = surface2.withLuminance(30),
        surfaceTint = PRIMARY,
        inverseSurface = surface0.inverse().withLuminance(10),
        inverseOnSurface = surface0.inverse().withLuminance(98),
        error = red.withLuminance(40),
        onError = red.withLuminance(100),//base,
        errorContainer = red.withLuminance(90),
        onErrorContainer = red.withLuminance(10),
        outline = overlay2.withLuminance(50),
        outlineVariant = overlay0.withLuminance(80),
        scrim = overlay2.withLuminance(1),
    )
