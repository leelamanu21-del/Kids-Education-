package com.example.feature.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.LanguageMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OutlineWarm
import com.example.ui.theme.toColorSet

internal fun satpinBiasForIndex(indexInGroup: Int): Float =
  when (indexInGroup % 6) {
    0 -> 0.04f
    1 -> 0.56f
    2 -> 0.96f
    3 -> 0.52f
    4 -> 0.04f
    else -> 0.48f
  }

@Composable
internal fun SatpinPathStepRow(
  letter: PhonicsLetter,
  indexInGroup: Int,
  showConnectorAbove: Boolean,
  prevLetterMastered: Boolean,
  languageMode: LanguageMode,
  progress: LetterProgress?,
  isUnlocked: Boolean,
  isNextUp: Boolean,
  onTileClick: () -> Unit,
  onSpeakClick: () -> Unit,
) {
  val accent = letter.group.accentColor()
  val currBias = satpinBiasForIndex(indexInGroup)
  val prevBias = satpinBiasForIndex((indexInGroup - 1).coerceAtLeast(0))

  BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    val availableWidth = maxWidth
    val cardWidth: Dp = (availableWidth * 0.74f).coerceIn(220.dp, 332.dp)
    val maxHorizontalShift = (availableWidth - cardWidth).coerceAtLeast(0.dp)
    val currStartOffset = maxHorizontalShift * currBias
    val prevStartOffset = maxHorizontalShift * prevBias

    Column(modifier = Modifier.fillMaxWidth()) {
      if (showConnectorAbove) {
        val connectorColor =
          when {
            prevLetterMastered -> MintGreen
            isNextUp -> CoralPrimary
            isUnlocked -> accent.copy(alpha = 0.65f)
            else -> OutlineWarm
          }
        Canvas(
          modifier =
            Modifier
              .fillMaxWidth()
              .height(30.dp),
        ) {
          val cardWidthPx = cardWidth.toPx()
          val startX = prevStartOffset.toPx() + cardWidthPx * 0.5f
          val endX = currStartOffset.toPx() + cardWidthPx * 0.5f
          val h = size.height
          val path =
            Path().apply {
              moveTo(startX, 0f)
              cubicTo(
                startX,
                h * 0.55f,
                endX,
                h * 0.45f,
                endX,
                h,
              )
            }
          drawPath(
            path = path,
            color = connectorColor,
            style =
              Stroke(
                width = 6f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f), 0f),
              ),
          )
          drawCircle(
            color = connectorColor,
            radius = 7f,
            center = Offset(endX, h - 3f),
          )
        }
      }

      Box(
        modifier = Modifier.fillMaxWidth().padding(start = currStartOffset),
      ) {
        SatpinPathSoundCard(
          letter = letter,
          languageMode = languageMode,
          progress = progress,
          isUnlocked = isUnlocked,
          isNextUp = isNextUp,
          cardWidth = cardWidth,
          onTileClick = onTileClick,
          onSpeakClick = onSpeakClick,
        )
      }
    }
  }
}

@Composable
private fun SatpinPathSoundCard(
  letter: PhonicsLetter,
  languageMode: LanguageMode,
  progress: LetterProgress?,
  isUnlocked: Boolean,
  isNextUp: Boolean,
  cardWidth: Dp,
  onTileClick: () -> Unit,
  onSpeakClick: () -> Unit,
) {
  val paletteColors = letter.palette.toColorSet()
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val pressScale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.96f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "pathTilePress",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "pathNextPulse")
  val pulseScale by
    infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = if (isNextUp) 1.025f else 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 850),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "pathPulseScale",
    )

  val fillColor = if (isUnlocked) paletteColors.fill else Color(0xFFE6E1D6)
  val bevelColor = if (isUnlocked) paletteColors.bevel else Color(0xFFC7C0B2)
  val contentColor = if (isUnlocked) paletteColors.content else Color(0xFF6B7280)
  val isMastered = progress?.mastered == true
  val stars = (progress?.starsEarned ?: 0L).toInt().coerceIn(0, 3)

  Box(
    modifier =
      Modifier
        .width(cardWidth)
        .height(112.dp)
        .scale(pressScale * pulseScale)
        .testTag("letter_tile_${letter.letter}"),
  ) {
    // 3D Bottom Shadow Bevel
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .offset(y = 5.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(bevelColor),
    )

    Row(
      modifier =
        Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(24.dp))
          .background(fillColor)
          .then(
            if (isNextUp) {
              Modifier.border(3.dp, Color.White, RoundedCornerShape(24.dp))
            } else {
              Modifier
            }
          )
          .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onTileClick,
          )
          .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      // Left Stepping-Stone Node Badge
      Surface(
        color = if (isUnlocked) paletteColors.badge else Color(0xFFD5CFC2),
        shape = CircleShape,
        modifier = Modifier.size(56.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${letter.letter}${letter.lowercase}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = contentColor,
            )
            Text(
              text = "#${letter.satpinOrder}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = contentColor.copy(alpha = 0.85f),
            )
          }
        }
      }

      // Middle: Phoneme, Hindi cue, Stars
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Surface(
            color = Color.Black.copy(alpha = 0.14f),
            shape = RoundedCornerShape(50),
          ) {
            Text(
              text = "${letter.phonemeSymbol} · ${letter.exampleEmoji} ${letter.exampleWord}",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = contentColor,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            )
          }
          if (isMastered) {
            Icon(
              imageVector = Icons.Rounded.CheckCircle,
              contentDescription = "Mastered",
              tint = GoldStar,
              modifier = Modifier.size(16.dp),
            )
          }
        }

        Text(
          text =
            if (isNextUp) "NEXT SOUND · Tap to practice!"
            else if (languageMode == LanguageMode.HINGLISH) "${letter.hindiWord} • ${letter.hindiScriptWord}"
            else letter.englishCue,
          style = MaterialTheme.typography.labelMedium,
          color = contentColor.copy(alpha = 0.92f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
          repeat(3) { index ->
            val earned = index < stars
            Icon(
              imageVector = if (earned) Icons.Rounded.Star else Icons.Rounded.StarOutline,
              contentDescription = null,
              tint = if (earned) GoldStar else contentColor.copy(alpha = 0.45f),
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }

      // Right Speaker / Lock Control
      if (isUnlocked) {
        IconButton(
          onClick = onSpeakClick,
          modifier =
            Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.25f))
              .testTag("speak_letter_${letter.letter}"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
            contentDescription = "Hear sound ${letter.letter}",
            tint = contentColor,
            modifier = Modifier.size(22.dp),
          )
        }
      } else {
        Box(
          modifier = Modifier.size(48.dp),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = "Locked letter ${letter.letter}",
            tint = contentColor,
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}
