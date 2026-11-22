package com.example.feature.stages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.curriculum.GrammarTopicRule
import com.example.domain.model.LanguageMode
import com.example.ui.theme.InkNavy
import com.example.ui.theme.SunshineAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GrammarTopicRuleCard(
  rule: GrammarTopicRule,
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  accentColor: Color,
  modifier: Modifier = Modifier,
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.08f)),
    modifier =
      modifier
        .fillMaxWidth()
        .border(1.5.dp, accentColor.copy(alpha = 0.42f), RoundedCornerShape(20.dp))
        .testTag("grammar_rule_card_${rule.topicTag}"),
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.weight(1f),
        ) {
          Surface(color = accentColor, shape = RoundedCornerShape(50)) {
            Text(
              text = "RULE & EXAMPLES",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            )
          }
          Text(
            text = rule.topicTag,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
          )
        }
        IconButton(
          onClick = {
            audioPlayer.speakText(
              "${rule.titleEn}. ${rule.ruleEn} Examples: ${rule.examples.joinToString(", ")}"
            )
          },
          modifier =
            Modifier
              .minimumInteractiveComponentSize()
              .clip(CircleShape)
              .background(Color.White)
              .testTag("hear_rule_button_${rule.topicTag}"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
            contentDescription = "Hear rule and examples",
            tint = accentColor,
          )
        }
      }

      Text(
        text = if (languageMode == LanguageMode.HINGLISH) rule.titleHinglish else rule.titleEn,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = InkNavy,
      )

      Text(
        text = if (languageMode == LanguageMode.HINGLISH) rule.ruleHinglish else rule.ruleEn,
        style = MaterialTheme.typography.bodyMedium,
        color = InkNavy,
      )
      if (languageMode == LanguageMode.HINGLISH) {
        Text(
          text = "English Rule: ${rule.ruleEn}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier =
          Modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
      ) {
        Text(
          text = "Pattern: ${rule.formulaOrPattern}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = accentColor,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
      }

      Text(
        text = "Examples (Tap to hear):",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = InkNavy,
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        rule.examples.forEach { example ->
          Surface(
            onClick = { audioPlayer.speakText(example.replace("→", " becomes ").replace("↔", " opposite ")) },
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            modifier =
              Modifier
                .minimumInteractiveComponentSize()
                .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
          ) {
            Text(
              text = "• $example",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.SemiBold,
              color = InkNavy,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
internal fun ExpandableRuleBookCard(
  title: String,
  rules: List<GrammarTopicRule>,
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  accentColor: Color,
  initiallyExpanded: Boolean = false,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(initiallyExpanded) }

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Surface(
      onClick = { expanded = !expanded },
      color = SunshineAmber.copy(alpha = 0.22f),
      shape = RoundedCornerShape(16.dp),
      modifier =
        Modifier
          .fillMaxWidth()
          .minimumInteractiveComponentSize()
          .border(1.dp, SunshineAmber, RoundedCornerShape(16.dp))
          .testTag("rule_book_toggle"),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f),
        ) {
          Icon(
            imageVector = Icons.Rounded.MenuBook,
            contentDescription = null,
            tint = InkNavy,
            modifier = Modifier.size(20.dp),
          )
          Text(
            text = "$title (${rules.size} Rules)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = InkNavy,
          )
        }
        Icon(
          imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
          contentDescription = if (expanded) "Hide rules" else "Show all rules",
          tint = InkNavy,
        )
      }
    }

    AnimatedVisibility(visible = expanded) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rules.forEach { rule ->
          GrammarTopicRuleCard(
            rule = rule,
            languageMode = languageMode,
            audioPlayer = audioPlayer,
            accentColor = accentColor,
          )
        }
      }
    }
  }
}
