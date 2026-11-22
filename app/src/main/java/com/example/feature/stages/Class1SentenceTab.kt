package com.example.feature.stages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.curriculum.GrammarRulesCatalog
import com.example.domain.curriculum.UpperCurriculumCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.TangerineOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SentenceReaderTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val sentences = UpperCurriculumCatalog.fluencySentences
  var sentenceIdx by remember { mutableIntStateOf(0) }
  val item = sentences[sentenceIdx]
  var pickedOption by remember(sentenceIdx) { mutableStateOf<Int?>(null) }
  var solved by remember(sentenceIdx) { mutableStateOf(false) }

  val words = remember(sentenceIdx) { item.sentence.split(" ") }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "Guided Sentence Practice",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Surface(color = SkyTeal.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Sentence ${sentenceIdx + 1} of ${sentences.size}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = SkyTeal,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }

    GrammarTopicRuleCard(
      rule = GrammarRulesCatalog.class1Rules[1],
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = SkyTeal,
    )

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 2.dp),
    ) {
      itemsIndexed(sentences) { idx, s ->
        FilterChip(
          selected = sentenceIdx == idx,
          onClick = { sentenceIdx = idx },
          label = { Text("${s.emoji} #${idx + 1}", fontWeight = FontWeight.Bold) },
          modifier = Modifier.minimumInteractiveComponentSize(),
        )
      }
    }

    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(text = item.emoji, fontSize = 54.sp)
        Text(
          text = "Tap any word to hear it clearly:",
          style = MaterialTheme.typography.labelLarge,
          color = SkyTeal,
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          words.forEach { token ->
            val cleanForSpeech = token.trim('.', ',', '!', '?')
            Surface(
              onClick = { audioPlayer.speakWord(cleanForSpeech, languageMode) },
              color = SunshineAmber.copy(alpha = 0.22f),
              shape = RoundedCornerShape(14.dp),
              modifier =
                Modifier
                  .minimumInteractiveComponentSize()
                  .border(1.5.dp, SunshineAmber, RoundedCornerShape(14.dp)),
            ) {
              Text(
                text = token,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = InkNavy,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              )
            }
          }
        }

        Surface(
          color = SkyTeal.copy(alpha = 0.1f),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Hindi Arth: ${item.hinglishMeaning}",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = InkNavy,
            )
            Text(
              text = item.hinglishTip,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
          Button(
            onClick = { audioPlayer.speakText(item.sentence) },
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f).height(48.dp),
          ) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Read Full", fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = {
              audioPlayer.speakBilingualScript(
                hindiDevanagariScript = item.hindiVoiceExplain,
                englishScript = item.sentence,
                languageMode = languageMode,
              )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f).height(48.dp),
          ) {
            Text("Hinglish Meaning", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = item.quizQuestion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        item.quizOptions.forEachIndexed { idx, opt ->
          val isSelected = pickedOption == idx
          val isCorrect = idx == item.correctOptionIndex
          val bg =
            when {
              isSelected && isCorrect -> MintGreen
              isSelected && !isCorrect -> CoralPrimary
              else -> MaterialTheme.colorScheme.surfaceVariant
            }
          val fg =
            when {
              isSelected && isCorrect -> InkNavy
              isSelected && !isCorrect -> Color.White
              else -> MaterialTheme.colorScheme.onSurface
            }
          Surface(
            onClick = {
              if (!solved) {
                pickedOption = idx
                if (isCorrect) {
                  solved = true
                  onAwardStars(2)
                  audioPlayer.playCelebrationChime()
                } else {
                  audioPlayer.playGentleTryAgain()
                }
              }
            },
            color = bg,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize(),
          ) {
            Text(
              text = opt,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = fg,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            )
          }
        }
        if (solved) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MintGreen)
            Text("Shabaash! Great reading! (+2 ⭐)", style = MaterialTheme.typography.titleSmall, color = InkNavy)
          }
        }
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedButton(
        onClick = { sentenceIdx = (sentenceIdx - 1 + sentences.size) % sentences.size },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Prev Sentence", fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = { sentenceIdx = (sentenceIdx + 1) % sentences.size },
        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Text("Next Sentence", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }
    }

    ExpandableRuleBookCard(
      title = "Class 1 Grammar & Phonics Rule Book",
      rules = GrammarRulesCatalog.class1Rules,
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = TangerineOrange,
    )
  }
}
