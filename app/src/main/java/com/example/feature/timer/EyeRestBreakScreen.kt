package com.example.feature.timer

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.model.LanguageMode
import com.example.feature.parent.ParentMathGateDialog
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber

@Composable
fun EyeRestBreakScreen(
  childName: String,
  languageMode: LanguageMode,
  elapsedLabel: String,
  totalStars: Long,
  wordsBlendedToday: Int,
  audioPlayer: PhonicsAudioPlayer,
  onGrantBonusMinutes: () -> Unit,
  onRestartFreshSession: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showParentUnlockGate by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "eyeRestPulse")
  val moonScale by
    infiniteTransition.animateFloat(
      initialValue = 0.96f,
      targetValue = 1.06f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 1600),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "moonPulse",
    )

  LaunchedEffect(Unit) {
    val spoken =
      if (languageMode == LanguageMode.HINGLISH) {
        "Shabaash $childName! Great job today! Ab apni aankhon ko thoda aaram dein."
      } else {
        "Great job today, $childName! Time to rest your eyes and play offline."
      }
    audioPlayer.playCelebrationChime()
    audioPlayer.speakText(spoken)
  }

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF1A2440),
              Color(0xFF28365A),
              Color(0xFF1E293B),
            )
          )
        )
        .safeDrawingPadding()
        .padding(24.dp)
        .testTag("eye_rest_break_screen"),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(136.dp)
            .scale(moonScale)
            .clip(CircleShape)
            .background(SunshineAmber.copy(alpha = 0.18f))
            .border(3.dp, SunshineAmber.copy(alpha = 0.7f), CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Rounded.Bedtime,
          contentDescription = "Eye rest moon icon",
          tint = SunshineAmber,
          modifier = Modifier.size(68.dp),
        )
      }

      Text(
        text = "Great job today, $childName!",
        fontSize = 30.sp,
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White,
        textAlign = TextAlign.Center,
      )

      Text(
        text =
          if (languageMode == LanguageMode.HINGLISH) {
            "Time to rest your eyes! Aankhon ko thoda aaram dein aur paani piyein."
          } else {
            "Time to rest your eyes! Blink softly, drink some water, and look far away."
          },
        style = MaterialTheme.typography.bodyLarge,
        color = Color(0xFFDDE6F5),
        textAlign = TextAlign.Center,
      )

      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(18.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
          BreakStatItem(
            icon = Icons.Rounded.Visibility,
            value = elapsedLabel,
            label = "Screen Time",
            tint = SkyTeal,
          )
          BreakStatItem(
            icon = Icons.Rounded.Star,
            value = "$totalStars",
            label = "Total Stars",
            tint = GoldStar,
          )
          BreakStatItem(
            icon = Icons.Rounded.Bedtime,
            value = "$wordsBlendedToday",
            label = "Words Blended",
            tint = MintGreen,
          )
        }
      }

      Surface(
        color = MintGreen.copy(alpha = 0.16f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = "20-20-20 Eye Care Tip: Look at something 20 feet away for 20 seconds!",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFFBFFAE7),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(14.dp),
        )
      }

      Button(
        onClick = onRestartFreshSession,
        shape = RoundedCornerShape(18.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MintGreen,
            contentColor = InkNavy,
          ),
        modifier = Modifier.fillMaxWidth().height(54.dp).testTag("restart_session_button"),
      ) {
        Text("I Rested My Eyes — Start Fresh", fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = { showParentUnlockGate = true },
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("parent_extend_time_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.LockOpen,
          contentDescription = null,
          tint = SunshineAmber,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Parent: Unlock 5 More Minutes")
      }
    }
  }

  if (showParentUnlockGate) {
    ParentMathGateDialog(
      title = "Parent Approval Required",
      subtitle = "Solve to grant 5 extra learning minutes.",
      onDismiss = { showParentUnlockGate = false },
      onSolved = {
        showParentUnlockGate = false
        onGrantBonusMinutes()
      },
    )
  }
}

@Composable
private fun BreakStatItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  value: String,
  label: String,
  tint: Color,
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.titleLarge,
      color = Color.White,
      fontWeight = FontWeight.Bold,
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = Color(0xFFB8C4DC),
    )
  }
}
