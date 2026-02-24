package com.example.networktool.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.networktool.settings.AppSettings

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF005AC1),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD8E2FF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF001A41),
    secondary = androidx.compose.ui.graphics.Color(0xFF575E71),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFDBE2F9),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF141B2C),
    tertiary = androidx.compose.ui.graphics.Color(0xFF715573),
    onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFDD7FB),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF29132D),
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFAEC6FF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF002D6E),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF00429A),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD8E2FF),
    secondary = androidx.compose.ui.graphics.Color(0xFFBFC6DC),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF293041),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF3F4759),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFDBE2F9),
    tertiary = androidx.compose.ui.graphics.Color(0xFFE0BBDE),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF402843),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF583E5B),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFDD7FB),
)

@Composable
fun NetworkToolTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = AppSettings.darkTheme || isSystemInDarkTheme()
    val dynamicColour = AppSettings.dynamicColour && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        dynamicColour && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColour && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
