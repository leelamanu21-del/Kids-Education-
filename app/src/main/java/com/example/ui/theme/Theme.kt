package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PhonicsLightColorScheme =
  lightColorScheme(
    primary = CoralPrimary,
    onPrimary = Color.White,
    primaryContainer = CoralContainer,
    onPrimaryContainer = InkNavy,
    secondary = SkyTeal,
    onSecondary = Color.White,
    secondaryContainer = SkyTealContainer,
    onSecondaryContainer = InkNavy,
    tertiary = SunshineAmber,
    onTertiary = InkNavy,
    tertiaryContainer = SunshineContainer,
    onTertiaryContainer = InkNavy,
    background = CreamCanvas,
    onBackground = InkNavy,
    surface = WarmSurface,
    onSurface = InkNavy,
    surfaceVariant = SoftSand,
    onSurfaceVariant = MutedSlate,
    outline = OutlineWarm,
  )

private val PhonicsDarkColorScheme =
  darkColorScheme(
    primary = CoralPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5D1D22),
    onPrimaryContainer = Color(0xFFFFDADB),
    secondary = MintGreen,
    onSecondary = InkNavy,
    secondaryContainer = Color(0xFF0A473B),
    onSecondaryContainer = Color(0xFFAFF7E0),
    tertiary = SunshineAmber,
    onTertiary = InkNavy,
    tertiaryContainer = Color(0xFF573E00),
    onTertiaryContainer = Color(0xFFFFECC2),
    background = DarkCanvas,
    onBackground = Color(0xFFF3F6FC),
    surface = DarkSurface,
    onSurface = Color(0xFFF3F6FC),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB7C4D8),
    outline = Color(0xFF394862),
  )

val PhonicsShapes =
  Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
  )

@Composable
fun PhonicsPathTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) PhonicsDarkColorScheme else PhonicsLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = PhonicsTypography,
    shapes = PhonicsShapes,
    content = content,
  )
}
