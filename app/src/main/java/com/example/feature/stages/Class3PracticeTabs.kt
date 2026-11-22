package com.example.feature.stages

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.curriculum.Class3CurriculumCatalog
import com.example.domain.curriculum.GrammarRulesCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.TangerineOrange

@Composable
internal fun AffixBuilderTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val items = Class3CurriculumCatalog.affixItems
  var idx by remember { mutableIntStateOf(0) }
  val item = items[idx]
  var picked by remember(idx) { mutableStateOf<Int?>(null) }
  var solved by remember(idx) { mutableStateOf(false) }
  val solvedSet = remember { mutableStateListOf<Int>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    StageCounterAndScoreRow(
      counterText = "Word ${idx + 1} of ${items.size}",
      scoreText = "Score: ${solvedSet.size} / ${items.size}",
      accentColor = SkyTeal,
    )

    GrammarTopicRuleCard(
      rule = GrammarRulesCatalog.ruleForAffixType(item.ruleType),
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = SkyTeal,
    )

    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = SkyTeal),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Surface(color = Color.White.copy(alpha = 0.22f), shape = RoundedCornerShape(50)) {
          Text(
            text = "${item.ruleType} · ${item.affix}",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          )
        }
        Text(item.emoji, fontSize = 48.sp)
        Text(
          text = "${item.rootWord}  →  ${item.formedWord}",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
          textAlign = TextAlign.Center,
        )
        Text(
          text = item.hinglishMeaning,
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White.copy(alpha = 0.94f),
          textAlign = TextAlign.Center,
        )
        Button(
          onClick = { audioPlayer.speakText("${item.rootWord} becomes ${item.formedWord}. ${item.grammarTip}") },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Hear Word Pair", fontWeight = FontWeight.Bold)
        }
      }
    }

    Surface(
      color = SunshineAmber.copy(alpha = 0.2f),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text = "More Examples: ${item.grammarTip}",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = InkNavy,
        modifier = Modifier.padding(14.dp),
      )
    }

    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(item.quizPrompt, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        item.quizOptions.forEachIndexed { optIdx, opt ->
          val isPicked = picked == optIdx
          val isCorrect = optIdx == item.correctIndex
          val bg =
            when {
              isPicked && isCorrect -> MintGreen
              isPicked && !isCorrect -> CoralPrimary
              else -> MaterialTheme.colorScheme.surfaceVariant
            }
          Surface(
            onClick = {
              if (!solved) {
                picked = optIdx
                if (isCorrect) {
                  solved = true
                  if (idx !in solvedSet) {
                    solvedSet.add(idx)
                    onAwardStars(1)
                  }
                  audioPlayer.playCelebrationChime()
                  audioPlayer.speakWord(opt, languageMode)
                } else {
                  audioPlayer.playGentleTryAgain()
                }
              }
            },
            color = bg,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize(),
          ) {
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
              Text(
                text = opt,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isPicked && !isCorrect) Color.White else InkNavy,
              )
            }
          }
        }
        if (solved) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MintGreen, modifier = Modifier.size(18.dp))
            Text("Bilkul sahi! Word builder star (+1 ⭐)", style = MaterialTheme.typography.labelLarge, color = InkNavy)
          }
        }
      }
    }

    PrevNextNavRow(
      prevLabel = "Prev Rule",
      nextLabel = "Next Rule",
      accentColor = SkyTeal,
      onPrev = { idx = (idx - 1 + items.size) % items.size },
      onNext = { idx = (idx + 1) % items.size },
    )

    ExpandableRuleBookCard(
      title = "Prefix, Suffix & Plural Rule Book",
      rules = GrammarRulesCatalog.class3AffixRules,
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = SkyTeal,
    )
  }
}

@Composable
internal fun Class3GrammarQuizTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val items = Class3CurriculumCatalog.grammarQuizzes
  var idx by remember { mutableIntStateOf(0) }
  val item = items[idx]
  var picked by remember(idx) { mutableStateOf<Int?>(null) }
  var solved by remember(idx) { mutableStateOf(false) }
  val solvedSet = remember { mutableStateListOf<Int>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    StageCounterAndScoreRow(
      counterText = "Question ${idx + 1} of ${items.size}",
      scoreText = "Score: ${solvedSet.size} / ${items.size}",
      accentColor = TangerineOrange,
    )

    GrammarRulesCatalog.class3GrammarRulesByTopic[item.topicTag]?.let { topicRule ->
      GrammarTopicRuleCard(
        rule = topicRule,
        languageMode = languageMode,
        audioPlayer = audioPlayer,
        accentColor = TangerineOrange,
      )
    }

    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Surface(color = TangerineOrange.copy(alpha = 0.16f), shape = RoundedCornerShape(50)) {
          Text(
            text = item.topicTag,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = TangerineOrange,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          )
        }
        Text(item.emoji, fontSize = 44.sp)
        Text(
          text = if (solved) item.completedSentence else item.sentenceWithBlank,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Button(
          onClick = {
            audioPlayer.speakText(if (solved) item.completedSentence else item.sentenceWithBlank.replace("___", "blank"))
          },
          colors = ButtonDefaults.buttonColors(containerColor = TangerineOrange, contentColor = Color.White),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Hear Sentence", fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      item.options.forEachIndexed { optIdx, opt ->
        val isPicked = picked == optIdx
        val isCorrect = optIdx == item.correctIndex
        val bg =
          when {
            isPicked && isCorrect -> MintGreen
            isPicked && !isCorrect -> CoralPrimary
            else -> MaterialTheme.colorScheme.surface
          }
        Surface(
          onClick = {
            if (!solved) {
              picked = optIdx
              if (isCorrect) {
                solved = true
                if (idx !in solvedSet) {
                  solvedSet.add(idx)
                  onAwardStars(1)
                }
                audioPlayer.playCelebrationChime()
                audioPlayer.speakText(item.completedSentence)
              } else {
                audioPlayer.playGentleTryAgain()
              }
            }
          },
          color = bg,
          shape = RoundedCornerShape(16.dp),
          modifier =
            Modifier
              .weight(1f)
              .minimumInteractiveComponentSize()
              .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        ) {
          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)) {
            Text(
              text = opt,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = if (isPicked && !isCorrect) Color.White else InkNavy,
            )
          }
        }
      }
    }

    if (solved) {
      Surface(
        color = MintGreen.copy(alpha = 0.22f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) "${item.hinglishExplanation} (+1 ⭐)"
            else "${item.completedSentence} (+1 ⭐)",
          style = MaterialTheme.typography.bodyMedium,
          color = InkNavy,
          modifier = Modifier.padding(14.dp),
        )
      }
    }

    PrevNextNavRow(
      prevLabel = "Prev Question",
      nextLabel = "Next Question",
      accentColor = TangerineOrange,
      onPrev = { idx = (idx - 1 + items.size) % items.size },
      onNext = { idx = (idx + 1) % items.size },
    )

    ExpandableRuleBookCard(
      title = "Class 3 Full Grammar Rule Book",
      rules = GrammarRulesCatalog.class3GrammarRulesByTopic.values.toList(),
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = TangerineOrange,
    )
  }
}
