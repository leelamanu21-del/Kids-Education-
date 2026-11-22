package com.example.feature.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BadgeProgress
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerRank
import com.example.ui.theme.InkNavy
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SunshineAmber

@Composable
internal fun NextBadgeGoalStrip(
  learnerRank: LearnerRank,
  badgeProgress: List<BadgeProgress>,
  unlockedCount: Int,
  languageMode: LanguageMode,
  onOpenRewards: () -> Unit,
) {
  val nextGoal = badgeProgress.filter { !it.unlocked }.maxByOrNull { it.fraction }

  Card(
    onClick = onOpenRewards,
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier =
      Modifier
        .fillMaxWidth()
        .border(1.5.dp, PlumPurple.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
        .testTag("next_badge_goal_card"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Surface(
        color = SunshineAmber.copy(alpha = 0.26f),
        shape = CircleShape,
        modifier = Modifier.size(46.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = nextGoal?.badge?.emoji ?: learnerRank.emoji,
            fontSize = 24.sp,
          )
        }
      }
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val headline =
          if (nextGoal != null) {
            val title =
              if (languageMode == LanguageMode.HINGLISH) nextGoal.badge.titleHinglish
              else nextGoal.badge.titleEn
            if (languageMode == LanguageMode.HINGLISH) "Agla Badge: $title"
            else "Next Badge: $title"
          } else {
            if (languageMode == LanguageMode.HINGLISH) "Sabhi Badges Jeet Liye! ${learnerRank.titleHinglish}"
            else "All Badges Unlocked! ${learnerRank.titleEn}"
          }
        Text(
          text = headline,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = InkNavy,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        if (nextGoal != null) {
          LinearProgressIndicator(
            progress = { nextGoal.fraction.coerceAtLeast(0.05f) },
            color = PlumPurple,
            trackColor = PlumPurple.copy(alpha = 0.14f),
            modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(50)),
          )
          Text(
            text =
              "${nextGoal.currentProgress}/${nextGoal.badge.targetProgress} · " +
                if (languageMode == LanguageMode.HINGLISH) nextGoal.badge.descriptionHinglish
                else nextGoal.badge.descriptionEn,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
      Surface(
        color = PlumPurple.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
      ) {
        Text(
          text = "$unlockedCount/${badgeProgress.size}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = PlumPurple,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
      }
    }
  }
}

@Composable
internal fun EditChildNameDialog(
  currentName: String,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit,
) {
  var text by remember(currentName) { mutableStateOf(currentName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Child's Name") },
    text = {
      OutlinedTextField(
        value = text,
        onValueChange = { if (it.length <= 40) text = it },
        singleLine = true,
        label = { Text("Little learner's nickname") },
        modifier = Modifier.fillMaxWidth().testTag("child_name_input"),
      )
    },
    confirmButton = {
      Button(
        onClick = { onSave(text) },
        enabled = text.isNotBlank(),
        modifier = Modifier.testTag("save_name_button"),
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  )
}
