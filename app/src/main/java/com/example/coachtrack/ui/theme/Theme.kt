package com.example.coachtrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = TextDark,

    secondary = AccentBlue,
    onSecondary = White,

    background = BackgroundLight,
    onBackground = TextDark,

    surface = SurfaceLight,
    onSurface = TextDark,

    error = ErrorRed,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = TextDark,
    primaryContainer = DarkBlue,
    onPrimaryContainer = White,

    secondary = AccentBlue,
    onSecondary = White,

    background = BackgroundDark,
    onBackground = White,

    surface = SurfaceDark,
    onSurface = White,

    error = ErrorRed,
    onError = White
)

@Composable
fun CoachTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // ← Esto usa el Typography de Type.kt
        content = content
    )
}