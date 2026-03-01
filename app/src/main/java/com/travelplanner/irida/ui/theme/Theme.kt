package com.travelplanner.irida.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val IridaDarkColorScheme = darkColorScheme(
    primary = TurquoisePrimary,
    onPrimary = NavyDeep,
    primaryContainer = TurquoiseDark,
    onPrimaryContainer = White,
    secondary = GoldAccent,
    onSecondary = NavyDeep,
    background = NavyDeep,
    onBackground = WhiteSoft,
    surface = NavyMid,
    onSurface = WhiteSoft,
    surfaceVariant = NavyLight,
    onSurfaceVariant = GrayMid,
    error = ErrorRed,
    outline = GrayDark
)

@Composable
fun IridaTheme(content: @Composable () -> Unit) {
    val colorScheme = IridaDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NavyDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}