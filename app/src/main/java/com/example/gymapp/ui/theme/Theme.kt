package com.example.gymapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BloomLightScheme = lightColorScheme(
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
    onTertiaryContainer = IronOnSurface,
    background = IronSurface,
    onBackground = IronOnSurface,
    surface = IronSurfaceLowest,
    onSurface = IronOnSurface,
    surfaceVariant = IronSurfaceHigh,
    onSurfaceVariant = IronOnSurfaceVariant,
    surfaceContainerLowest = IronSurfaceLowest,
    surfaceContainerLow = IronSurfaceLow,
    surfaceContainer = IronSurfaceContainer,
    surfaceContainerHigh = IronSurfaceHigh,
    surfaceContainerHighest = IronSurfaceHighest,
    surfaceTint = Color.Transparent,
    error = CoralError,
    onError = OnCoralError,
    errorContainer = CoralErrorContainer,
    onErrorContainer = OnCoralErrorContainer,
    outline = IronOutline,
    outlineVariant = IronOutlineVariant,
)

private val BloomShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun GymappTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = BloomLightScheme,
        typography = Typography,
        shapes = BloomShapes,
        content = content,
    )
}
