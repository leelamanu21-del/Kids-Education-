package com.example.feature.stages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.model.LanguageMode
import com.example.domain.model.ReadingPassageItem
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber

@Composable
internal fun StageTopBar(
  levelLabel: String,
  accentColor: Color,
  backButtonTag: String,
  onBack: () -> Unit,
  darkPillText: Boolean = false,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    IconButton(
      onClick = onBack,
      modifier =
        Modifier
          .minimumInteractiveComponentSize()
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface)
          .testTag(backButtonTag),
    ) {
      Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
    }

    Surface(
      color = accentColor.copy(alpha = 0.16f),
      shape = RoundedCornerShape(50),
    ) {
      Text(
        text = levelLabel,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = if (darkPillText) InkNavy else accentColor,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
      )
    }

    Spacer(modifier = Modifier.size(48.dp))
  }
}

@Composable
internal fun StageCounterAndScoreRow(
  counterText: String,
  scoreText: String,
  accentColor: Color,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Surface(color = accentColor.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
      Text(
        text = counterText,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = accentColor,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      )
    }
    Surface(color = MintGreen.copy(alpha = 0.25f), shape = RoundedCornerShape(50)) {
      Text(
        text = scoreText,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = InkNavy,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      )
    }
  }
}

@Composable
internal fun PrevNextNavRow(
  prevLabel: String,
  nextLabel: String,
  accentColor: Color,
  onPrev: () -> Unit,
  onNext: () -> Unit,
) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    OutlinedButton(
      onClick = onPrev,
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.weight(1f).height(48.dp),
    ) {
      Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(prevLabel, fontWeight = FontWeight.Bold)
    }
    Button(
      onClick = onNext,
      colors = ButtonDefaults.buttonColors(containerColor = accentColor),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.weight(1f).height(48.dp),
    ) {
      Text(nextLabel, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.width(6.dp))
      Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ReadingPassagesBoard(
  passages: List<ReadingPassageItem>,
  accentColor: Color,
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  var passageIdx by remember { mutableIntStateOf(0) }
  val passage = passages[passageIdx]

  val q1ChoiceMap = remember { mutableStateMapOf<String, Int>() }
  val q1SolvedMap = remember { mutableStateMapOf<String, Boolean>() }
  val q2ChoiceMap = remember { mutableStateMapOf<String, Int>() }
  val q2SolvedMap = remember { mutableStateMapOf<String, Boolean>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    StageCounterAndScoreRow(
      counterText = "Passage ${passageIdx + 1} of ${passages.size}",
      scoreText = "Solved: ${q1SolvedMap.size + q2SolvedMap.size} / ${passages.size * 2}",
      accentColor = accentColor,
    )

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 2.dp),
    ) {
      itemsIndexed(passages) { idx, p ->
        FilterChip(
          selected = passageIdx == idx,
          onClick = { passageIdx = idx },
          label = { Text("${idx + 1}. ${p.title}", fontWeight = FontWeight.Bold) },
          modifier = Modifier.minimumInteractiveComponentSize(),
        )
      }
    }

    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = passage.emojiBanner,
          fontSize = 42.sp,
          modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Surface(color = accentColor.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
          Text(
            text = passage.levelLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
        Text(
          text = passage.title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold,
          color = accentColor,
        )
        passage.paragraphs.forEach { para ->
          Text(
            text = para,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Surface(
          color = SkyTeal.copy(alpha = 0.12f),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
            text =
              if (languageMode == LanguageMode.HINGLISH) "Saar (Summary): ${passage.hinglishTakeaway}"
              else "Summary: ${passage.hinglishTakeaway}",
            style = MaterialTheme.typography.bodySmall,
            color = InkNavy,
            modifier = Modifier.padding(10.dp),
          )
        }
        Button(
          onClick = { audioPlayer.speakText("${passage.title}. ${passage.paragraphs.joinToString(" ")}") },
          colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Listen Passage", fontWeight = FontWeight.Bold)
        }
      }
    }

    // Vocabulary Glossary
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = SunshineAmber.copy(alpha = 0.18f)),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Word Glossary · Tap to Hear",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = InkNavy,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          passage.vocabulary.forEach { entry ->
            Surface(
              onClick = { audioPlayer.speakText("${entry.word}. ${entry.meaningEn}") },
              color = Color.White,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                  text = entry.word,
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  color = PlumPurple,
                )
                Text(
                  text =
                    if (languageMode == LanguageMode.HINGLISH) "${entry.meaningEn} (${entry.hinglishCue})"
                    else entry.meaningEn,
                  style = MaterialTheme.typography.bodySmall,
                  color = InkNavy,
                )
              }
            }
          }
        }
      }
    }

    PassageQuizCard(
      label = "Question 1 (+1 ⭐)",
      question = passage.question1,
      options = passage.options1,
      correctIndex = passage.correctIndex1,
      pickedIndex = q1ChoiceMap[passage.id],
      isSolved = q1SolvedMap[passage.id] == true,
      accentColor = PlumPurple,
      onPick = { idx ->
        if (q1SolvedMap[passage.id] != true) {
          q1ChoiceMap[passage.id] = idx
          if (idx == passage.correctIndex1) {
            q1SolvedMap[passage.id] = true
            onAwardStars(1)
            audioPlayer.playCelebrationChime()
          } else {
            audioPlayer.playGentleTryAgain()
          }
        }
      },
    )

    PassageQuizCard(
      label = "Question 2 (+2 ⭐)",
      question = passage.question2,
      options = passage.options2,
      correctIndex = passage.correctIndex2,
      pickedIndex = q2ChoiceMap[passage.id],
      isSolved = q2SolvedMap[passage.id] == true,
      accentColor = CoralPrimary,
      onPick = { idx ->
        if (q2SolvedMap[passage.id] != true) {
          q2ChoiceMap[passage.id] = idx
          if (idx == passage.correctIndex2) {
            q2SolvedMap[passage.id] = true
            onAwardStars(2)
            audioPlayer.playCelebrationChime()
          } else {
            audioPlayer.playGentleTryAgain()
          }
        }
      },
    )

    PrevNextNavRow(
      prevLabel = "Prev Passage",
      nextLabel = "Next Passage",
      accentColor = accentColor,
      onPrev = { passageIdx = (passageIdx - 1 + passages.size) % passages.size },
      onNext = { passageIdx = (passageIdx + 1) % passages.size },
    )
  }
}

@Composable
private fun PassageQuizCard(
  label: String,
  question: String,
  options: List<String>,
  correctIndex: Int,
  pickedIndex: Int?,
  isSolved: Boolean,
  accentColor: Color,
  onPick: (Int) -> Unit,
) {
  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = accentColor)
      Text(question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      options.forEachIndexed { idx, opt ->
        val picked = pickedIndex == idx
        val correct = idx == correctIndex
        val bg =
          when {
            (picked && correct) || (isSolved && correct) -> MintGreen
            picked && !correct -> CoralPrimary
            else -> MaterialTheme.colorScheme.surfaceVariant
          }
        Surface(
          onClick = { onPick(idx) },
          color = bg,
          shape = RoundedCornerShape(14.dp),
          modifier =
            Modifier
              .fillMaxWidth()
              .minimumInteractiveComponentSize()
              .border(
                width = 1.dp,
                color = if (picked && correct) MintGreen else Color.Transparent,
                shape = RoundedCornerShape(14.dp),
              ),
        ) {
          Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
              text = opt,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = if (picked && !correct) Color.White else InkNavy,
            )
          }
        }
      }
      if (isSolved) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MintGreen, modifier = Modifier.size(18.dp))
          Text("Great reading comprehension!", style = MaterialTheme.typography.labelLarge, color = InkNavy)
        }
      }
    }
  }
}
