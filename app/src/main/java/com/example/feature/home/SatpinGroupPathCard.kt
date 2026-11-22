package com.example.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.LanguageMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.example.domain.model.SatpinGroup
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.SunshineAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SatpinGroupPathCard(
  group: SatpinGroup,
  groupLetters: List<PhonicsLetter>,
  progressByLetter: Map<String, LetterProgress>,
  languageMode: LanguageMode,
  maxUnlockedOrder: Int,
  freeExplore: Boolean,
  onPracticeGroup: () -> Unit,
  onSelectLetter: (PhonicsLetter) -> Unit,
) {
  val accent = group.accentColor()
  val masteredInGroup = groupLetters.count { progressByLetter[it.letter]?.mastered == true }
  val totalInGroup = groupLetters.size.coerceAtLeast(1)
  val isGroupUnlocked = freeExplore || groupLetters.any { it.satpinOrder <= maxUnlockedOrder }

  Card(
    onClick = onPracticeGroup,
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
        .border(2.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
        .testTag("satpin_group_card_${group.groupNumber}"),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier =
          Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(accent.copy(alpha = 0.16f), accent.copy(alpha = 0.05f))
              )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f),
            ) {
              Surface(
                color = accent,
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = "${group.groupNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                  )
                }
              }
              Column {
                Text(
                  text =
                    if (languageMode == LanguageMode.HINGLISH) group.titleHinglish
                    else group.titleEn,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = InkNavy,
                )
                Text(
                  text = group.subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = MutedSlate,
                )
              }
            }

            Surface(
              color = SunshineAmber.copy(alpha = 0.25f),
              shape = RoundedCornerShape(12.dp),
            ) {
              Text(
                text = "$masteredInGroup/$totalInGroup",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = InkNavy,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              )
            }
          }

          LinearProgressIndicator(
            progress = { (masteredInGroup.toFloat() / totalInGroup.toFloat()).coerceIn(0.04f, 1f) },
            color = accent,
            trackColor = accent.copy(alpha = 0.16f),
            modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(50)),
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.weight(1f),
            ) {
              groupLetters.forEach { letter ->
                val done = progressByLetter[letter.letter]?.mastered == true
                val open = freeExplore || letter.satpinOrder <= maxUnlockedOrder
                Surface(
                  onClick = { onSelectLetter(letter) },
                  color =
                    when {
                      done -> MintGreen.copy(alpha = 0.24f)
                      open -> Color.White
                      else -> Color(0xFFECE8DF)
                    },
                  shape = CircleShape,
                  modifier =
                    Modifier
                      .minimumInteractiveComponentSize()
                      .border(
                        width = 1.dp,
                        color = if (done) MintGreen else accent.copy(alpha = 0.45f),
                        shape = CircleShape,
                      )
                      .testTag("group_${group.groupNumber}_letter_${letter.letter}"),
                ) {
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                  ) {
                    Text(
                      text = letter.letter,
                      style = MaterialTheme.typography.labelLarge,
                      fontWeight = FontWeight.ExtraBold,
                      color = if (open) InkNavy else MutedSlate,
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
              onClick = onPracticeGroup,
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = if (isGroupUnlocked) accent else Color(0xFF94A3B8),
                  contentColor = Color.White,
                ),
              shape = RoundedCornerShape(14.dp),
              modifier =
                Modifier
                  .height(48.dp)
                  .testTag("practice_group_${group.groupNumber}"),
            ) {
              Icon(
                imageVector = if (isGroupUnlocked) Icons.Rounded.PlayArrow else Icons.Rounded.Lock,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Practice", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
