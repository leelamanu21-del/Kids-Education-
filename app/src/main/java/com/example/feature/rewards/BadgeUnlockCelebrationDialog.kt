package com.example.feature.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.model.DigitalBadge
import com.example.domain.model.LanguageMode
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.toColorSet

@Composable
fun BadgeUnlockCelebrationDialog(
  badge: DigitalBadge,
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onViewAllBadges: () -> Unit,
  onDismiss: () -> Unit,
) {
  val paletteColors = badge.palette.toColorSet()

  LaunchedEffect(badge.id) {
    audioPlayer.playCelebrationChime()
    audioPlayer.speakBilingualScript(
      hindiDevanagariScript = "बधाई हो! आपने नया बैज ${badge.titleEn} जीत लिया है!",
      englishScript = "Hooray! You unlocked the ${badge.titleEn} badge!",
      languageMode = languageMode,
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Surface(
          color = SunshineAmber.copy(alpha = 0.25f),
          shape = RoundedCornerShape(50),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.AutoAwesome,
              contentDescription = null,
              tint = PlumPurple,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text =
                if (languageMode == LanguageMode.HINGLISH) "Naya Badge Khul Gaya!"
                else "New Badge Unlocked!",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = InkNavy,
            )
          }
        }

        Box(
          modifier =
            Modifier
              .size(96.dp)
              .clip(CircleShape)
              .background(paletteColors.fill.copy(alpha = 0.2f))
              .border(3.dp, GoldStar, CircleShape),
          contentAlignment = Alignment.Center,
        ) {
          Text(text = badge.emoji, fontSize = 50.sp)
        }

        Text(
          text = if (languageMode == LanguageMode.HINGLISH) badge.titleHinglish else badge.titleEn,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold,
          color = InkNavy,
          textAlign = TextAlign.Center,
        )
      }
    },
    text = {
      Text(
        text =
          if (languageMode == LanguageMode.HINGLISH) badge.descriptionHinglish
          else badge.descriptionEn,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )
    },
    confirmButton = {
      Button(
        onClick = onViewAllBadges,
        colors = ButtonDefaults.buttonColors(containerColor = PlumPurple, contentColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.height(48.dp).testTag("celebration_view_badges_button"),
      ) {
        Icon(Icons.Rounded.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("View Badges", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.height(48.dp).testTag("celebration_dismiss_button"),
      ) {
        Text("Yay! Continue", fontWeight = FontWeight.Bold)
      }
    },
  )
}
