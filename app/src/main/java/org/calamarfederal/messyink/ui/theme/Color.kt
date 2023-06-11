package org.calamarfederal.messyink.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces

fun Color.withLabL(percent: Int): Color {
    return convert(ColorSpaces.CieLab).copy(red = percent.toFloat()).convert(colorSpace)
}

fun buildM3ColorScheme(
    primary: Color,
    secondary: Color,
    tertiary: Color,
    neutral: Color,
    neutralVariant: Color,
    error: Color,
    isLight: Boolean,
): ColorScheme {
    return ColorScheme(
        primary = primary.withLabL(if (isLight) 40 else 80),
        primaryContainer = primary.withLabL(if (isLight) 90 else 30),
        onPrimary = primary.withLabL(if (isLight) 100 else 20),
        onPrimaryContainer = primary.withLabL(if (isLight) 10 else 90),
        inversePrimary = primary.withLabL(if (isLight) 80 else 40),
        secondary = secondary.withLabL(if (isLight) 90 else 30),
        secondaryContainer = secondary.withLabL(if (isLight) 90 else 30),
        onSecondary = secondary.withLabL(if (isLight) 100 else 20),
        onSecondaryContainer = secondary.withLabL(if (isLight) 100 else 10),
        tertiary = tertiary.withLabL(if (isLight) 40 else 80),
        tertiaryContainer = tertiary.withLabL(if (isLight) 90 else 30),
        onTertiary = tertiary.withLabL(if (isLight) 100 else 20),
        onTertiaryContainer = tertiary.withLabL(10),
        surface = neutral.withLabL(if (isLight) 98 else 6),
        surfaceVariant = neutralVariant.withLabL(if (isLight) 90 else 30),
        onSurface = neutral.withLabL(if (isLight) 10 else 90),
        onSurfaceVariant = neutralVariant.withLabL(if (isLight) 30 else 80),
        inverseSurface = neutral.withLabL(if (isLight) 20 else 90),
        inverseOnSurface = neutral.withLabL(if (isLight) 95 else 20),
        background = neutral.withLabL(if (isLight) 98 else 6),
        onBackground = neutral.withLabL(if (isLight) 10 else 90),
        error = error.withLabL(if (isLight) 40 else 80),
        errorContainer = error.withLabL(if (isLight) 90 else 30),
        onError = error.withLabL(if (isLight) 100 else 20),
        onErrorContainer = error.withLabL(if (isLight) 10 else 90),
        outline = neutralVariant.withLabL(if (isLight) 50 else 60),
        outlineVariant = neutralVariant.withLabL(if (isLight) 80 else 30),
        surfaceTint = primary,
        scrim = neutral.withLabL(if (isLight) 0 else 0),
    )
}
