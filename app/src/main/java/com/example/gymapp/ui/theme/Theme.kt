package com.example.gymapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Lime,
    onPrimary = OnLime,
    primaryContainer = LimeContainer,
    onPrimaryContainer = Color(0xFFE9FFB0),
    secondary = Amber,
    onSecondary = Color(0xFF2A1D00),
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    background = Ink,
    onBackground = Paper,
    surface = Ink,
    onSurface = Paper,
    surfaceVariant = Color(0xFF2A3340),
    onSurfaceVariant = Mist,
    surfaceContainer = InkRaised,
    surfaceContainerHigh = InkCard,
    error = Coral,
    onError = Color(0xFF3B0A0A),
    outline = Line,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A6700),
    onPrimary = Color.White,
    primaryContainer = Lime,
    onPrimaryContainer = OnLime,
    secondary = Color(0xFF8A5A00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE08A),
    onSecondaryContainer = Color(0xFF2A1D00),
    background = Color(0xFFF4F6F1),
    onBackground = Color(0xFF161A12),
    surface = Color(0xFFF4F6F1),
    onSurface = Color(0xFF161A12),
    surfaceVariant = Color(0xFFE3E8DC),
    onSurfaceVariant = Color(0xFF43493A),
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFE8EEE0),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    outline = Color(0xFFC4CABB),
)

@Composable
fun GymappTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
