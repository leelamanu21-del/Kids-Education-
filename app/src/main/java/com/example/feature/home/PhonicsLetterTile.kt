package com.example.feature.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguageMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.example.ui.theme.GoldStar
import com.example.ui.theme.toColorSet

@Composable
internal fun PhonicsLetterTile(
  letter: PhonicsLetter,
  orderNumber: Int,
  languageMode: LanguageMode,
  progress: LetterProgress?,
  isUnlocked: Boolean,
  isNextUp: Boolean,
  onTileClick: () -> Unit,
  onSpeakClick: () -> Unit,
) {
  val paletteColors = letter.palette.toColorSet()
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val pressScale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.95f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "tilePressScale",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "nextPulse")
  val pulseScale by
    infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = if (isNextUp) 1.03f else 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 850),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "pulseScale",
    )

  val fillColor = if (isUnlocked) paletteColors.fill else Color(0xFFE6E1D6)
  val bevelColor = if (isUnlocked) paletteColors.bevel else Color(0xFFC7C0B2)
  val contentColor = if (isUnlocked) paletteColors.content else Color(0xFF6B7280)
  val stars = (progress?.starsEarned ?: 0L).toInt().coerceIn(0, 3)

  Box(
    modifier =
      Modifier
        .fillMaxWidth()
        .height(186.dp)
        .scale(pressScale * pulseScale)
        .testTag("letter_tile_${letter.letter}"),
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .offset(y = 6.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(bevelColor)
    )

    Box(
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
          .padding(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Surface(
          color = if (isUnlocked) paletteColors.badge else Color(0xFFD5CFC2),
          shape = RoundedCornerShape(10.dp),
        ) {
          Text(
            text = if (isNextUp) "NEXT #$orderNumber" else "#$orderNumber",
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }

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

      Column(
        modifier = Modifier.align(Alignment.Center).padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = letter.letter,
            fontSize = 44.sp,
            style = MaterialTheme.typography.displayLarge,
            color = contentColor,
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = letter.lowercase,
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineLarge,
            color = contentColor.copy(alpha = 0.9f),
            modifier = Modifier.padding(bottom = 4.dp),
          )
        }

        Surface(
          color = Color.Black.copy(alpha = 0.12f),
          shape = RoundedCornerShape(50),
        ) {
          Text(
            text =
              if (languageMode == LanguageMode.HINGLISH) {
                "${letter.exampleEmoji} ${letter.exampleWord} • ${letter.hindiWord}"
              } else {
                "${letter.exampleEmoji} ${letter.phonemeSymbol} ${letter.exampleWord}"
              },
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
      }

      Row(
        modifier = Modifier.align(Alignment.BottomCenter),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        repeat(3) { index ->
          val earned = index < stars
          Icon(
            imageVector = if (earned) Icons.Rounded.Star else Icons.Rounded.StarOutline,
            contentDescription = null,
            tint = if (earned) GoldStar else contentColor.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}
