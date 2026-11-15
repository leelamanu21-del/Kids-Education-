package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.domain.model.TilePalette

val CreamCanvas = Color(0xFFFFFBF2)
val WarmSurface = Color(0xFFFFFFFF)
val SoftSand = Color(0xFFFFF2D6)
val SoftSkyTint = Color(0xFFEAF7FC)

val InkNavy = Color(0xFF1E293B)
val MutedSlate = Color(0xFF526075)
val OutlineWarm = Color(0xFFE9DEC9)

val CoralPrimary = Color(0xFFFF5A5F)
val CoralShadow = Color(0xFFD63B40)
val CoralContainer = Color(0xFFFFE5E6)

val SunshineAmber = Color(0xFFFFB703)
val SunshineShadow = Color(0xFFD48D00)
val SunshineContainer = Color(0xFFFFF4D1)

val SkyTeal = Color(0xFF118AB2)
val SkyTealShadow = Color(0xFF0B6482)
val SkyTealContainer = Color(0xFFDDF3FA)

val MintGreen = Color(0xFF06D6A0)
val MintShadow = Color(0xFF04A67A)
val MintContainer = Color(0xFFD9FAF0)

val PlumPurple = Color(0xFF8338EC)
val PlumShadow = Color(0xFF5F1EBF)
val PlumContainer = Color(0xFFEDE2FE)

val TangerineOrange = Color(0xFFFB8500)
val TangerineShadow = Color(0xFFC96700)
val TangerineContainer = Color(0xFFFFECD6)

val GoldStar = Color(0xFFFFC300)
val EmptyStar = Color(0xFFD8D0C0)

val DarkCanvas = Color(0xFF141B29)
val DarkSurface = Color(0xFF1E283A)
val DarkSurfaceVariant = Color(0xFF28344B)

data class TileColorSet(
  val fill: Color,
  val bevel: Color,
  val badge: Color,
  val content: Color,
)

fun TilePalette.toColorSet(): TileColorSet =
  when (this) {
    TilePalette.CORAL ->
      TileColorSet(
        fill = CoralPrimary,
        bevel = CoralShadow,
        badge = Color(0xFFFF8589),
        content = Color.White,
      )
    TilePalette.SUNSHINE ->
      TileColorSet(
        fill = SunshineAmber,
        bevel = SunshineShadow,
        badge = Color(0xFFFFD15C),
        content = InkNavy,
      )
    TilePalette.SKY_TEAL ->
      TileColorSet(
        fill = SkyTeal,
        bevel = SkyTealShadow,
        badge = Color(0xFF42ADD0),
        content = Color.White,
      )
    TilePalette.MINT ->
      TileColorSet(
        fill = MintGreen,
        bevel = MintShadow,
        badge = Color(0xFF4BE8BE),
        content = InkNavy,
      )
    TilePalette.PLUM ->
      TileColorSet(
        fill = PlumPurple,
        bevel = PlumShadow,
        badge = Color(0xFFA56BF5),
        content = Color.White,
      )
    TilePalette.TANGERINE ->
      TileColorSet(
        fill = TangerineOrange,
        bevel = TangerineShadow,
        badge = Color(0xFFFFA947),
        content = Color.White,
      )
  }
