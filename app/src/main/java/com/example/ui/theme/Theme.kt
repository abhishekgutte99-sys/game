package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SaffronSecondary,
    onPrimary = Color.White,
    primaryContainer = SaffronPrimary,
    onPrimaryContainer = SandalwoodCream,
    secondary = DivineGold,
    onSecondary = TempleDark,
    secondaryContainer = TempleCardBorder,
    onSecondaryContainer = RadiantGold,
    tertiary = LotusPink,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = SandalwoodCream,
    surface = SurfaceDark,
    onSurface = SandalwoodCream,
    surfaceVariant = TempleCard,
    onSurfaceVariant = GoldenText,
    outline = TempleCardBorder
)

private val LightColorScheme = darkColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.White,
    secondary = DivineGold,
    tertiary = LotusPink,
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = SandalwoodCream
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

