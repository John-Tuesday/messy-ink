package org.calamarfederal.messyink.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val primary: Color = Color(148, 226, 213)
private val secondary: Color = Color(137, 220, 235)
private val tertiary: Color = Color(249, 226, 175)
private val errorColor: Color = Color(243, 139, 168)
private val neutral: Color = Color(49, 50, 68)
private val neutralVariant: Color = Color(108, 112, 134)

private val DarkColorScheme: ColorScheme = buildM3ColorScheme(
    primary = primary,
    secondary = secondary,
    tertiary = tertiary,
    error = errorColor,
    neutral = neutral,
    neutralVariant = neutralVariant,
    isLight = false,
)
private val LightColorScheme: ColorScheme = buildM3ColorScheme(
    primary = primary,
    secondary = secondary,
    tertiary = tertiary,
    error = errorColor,
    neutral = neutral,
    neutralVariant = neutralVariant,
    isLight = true,
)

/**
 * Base theme for the app
 */
@Composable
fun MessyInkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme                                                      -> DarkColorScheme
        else                                                           -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}
