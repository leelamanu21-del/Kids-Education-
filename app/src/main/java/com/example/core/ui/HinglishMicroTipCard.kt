package com.example.core.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.domain.model.PhonicsLetter
import com.example.ui.theme.InkNavy
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SoftSkyTint
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.SunshineContainer

/**
 * Reusable Hinglish micro-tip banner that pairs English phonetics with a friendly
 * Hindi/Hinglish sound bridge (e.g., "A says /æ/ as in Ant -> 'ऐ' जैसी आवाज़")
 * and triggers audio playback when tapped.
 */
@Composable
fun HinglishMicroTipCard(
  letter: PhonicsLetter,
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  modifier: Modifier = Modifier,
  compact: Boolean = false,
) {
  var tapBounce by remember { mutableStateOf(false) }
  val scale by
    animateFloatAsState(
      targetValue = if (tapBounce) 0.97f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      finishedListener = { tapBounce = false },
      label = "hinglishTipBounce",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "speakerPulse")
  val speakerScale by
    infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = 1.1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 900),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "speakerIconPulse",
    )

  val hindiSound = PhonicsCurriculum.hindiSoundTag(letter.letter)
  val englishLine = "${letter.letter} says ${letter.phonemeSymbol} as in ${letter.exampleWord}"
  val hinglishLine = "→ '$hindiSound' जैसी आवाज़ (${letter.hindiWord} • ${letter.hindiScriptWord})"

  Surface(
    shape = RoundedCornerShape(22.dp),
    color = Color.Transparent,
    shadowElevation = 2.dp,
    modifier =
      modifier
        .fillMaxWidth()
        .scale(scale)
        .clip(RoundedCornerShape(22.dp))
        .background(
          Brush.horizontalGradient(
            listOf(
              SunshineContainer,
              SoftSkyTint,
            )
          )
        )
        .border(1.5.dp, SunshineAmber.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
        .clickable {
          tapBounce = true
          audioPlayer.speakLetterPhoneme(letter, languageMode)
        }
        .testTag("hinglish_micro_tip_${letter.letter}"),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = if (compact) 10.dp else 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(SkyTeal),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
          contentDescription = "Tap to hear Hinglish phonics hint for ${letter.letter}",
          tint = Color.White,
          modifier = Modifier.size(24.dp).scale(speakerScale),
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.Translate,
            contentDescription = null,
            tint = SkyTeal,
            modifier = Modifier.size(14.dp),
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text =
              if (languageMode == LanguageMode.HINGLISH) "Hinglish Sound Tip • Tap to Hear"
              else "Phonics Sound Tip • Tap to Hear",
            style = MaterialTheme.typography.labelMedium,
            color = SkyTeal,
          )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = englishLine,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = InkNavy,
        )
        Text(
          text = hinglishLine,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155),
        )
      }
    }
  }
}
