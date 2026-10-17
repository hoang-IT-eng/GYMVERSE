package com.example.gymapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IronLogDarkScheme = darkColorScheme(
    primary = NeonPrimary,
    onPrimary = OnNeonPrimary,
    primaryContainer = NeonPrimaryContainer,
    onPrimaryContainer = OnNeonPrimaryContainer,
    secondary = BurntSecondary,
    onSecondary = OnBurntSecondary,
    secondaryContainer = BurntSecondaryContainer,
    onSecondaryContainer = OnBurntSecondaryContainer,
    tertiary = EmeraldTertiary,
    onTertiary = OnEmeraldTertiary,
    tertiaryContainer = EmeraldTertiaryContainer,
    onTertiaryContainer = OnEmeraldTertiary,
    background = IronSurface,
    onBackground = IronOnSurface,
    surface = IronSurface,
    onSurface = IronOnSurface,
    surfaceVariant = IronSurfaceHighest,
    onSurfaceVariant = IronOnSurfaceVariant,
    surfaceContainerLowest = IronSurfaceLowest,
    surfaceContainerLow = IronSurfaceLow,
    surfaceContainer = IronSurfaceContainer,
    surfaceContainerHigh = IronSurfaceHigh,
    surfaceContainerHighest = IronSurfaceHighest,
    error = CoralError,
    onError = OnCoralError,
    errorContainer = CoralErrorContainer,
    onErrorContainer = OnCoralErrorContainer,
    outline = IronOutline,
    outlineVariant = IronOutlineVariant,
)

@Composable
fun GymappTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = IronLogDarkScheme,
        typography = Typography,
        content = content,
    )
}
