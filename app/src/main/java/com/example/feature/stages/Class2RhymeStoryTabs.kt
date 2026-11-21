package com.example.feature.stages

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
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.domain.curriculum.UpperCurriculumCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun OppositesAndRhymesTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
) {
  val oddPickedMap = remember { mutableStateMapOf<String, String>() }
  val oddSolvedMap = remember { mutableStateMapOf<String, Boolean>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        "Opposites (Vilom Shabd) · Tap to Hear",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Surface(color = CoralPrimary.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
        Text(
          text = "${UpperCurriculumCatalog.opposites.size} Pairs",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = CoralPrimary,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
      }
    }

    UpperCurriculumCatalog.opposites.forEach { pair ->
      Card(
        onClick = { audioPlayer.speakText("${pair.wordA}. Opposite is ${pair.wordB}.") },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("${pair.emojiA} ${pair.wordA}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CoralPrimary)
          Text(pair.hinglishPair, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text("${pair.wordB} ${pair.emojiB}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SkyTeal)
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        "Rhyming Words · Odd One Out!",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Surface(color = SunshineAmber.copy(alpha = 0.25f), shape = RoundedCornerShape(50)) {
        Text(
          text = "${UpperCurriculumCatalog.rhymeFamilies.size} Sets",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = InkNavy,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
      }
    }

    UpperCurriculumCatalog.rhymeFamilies.forEach { rhyme ->
      val mixedWords =
        remember(rhyme.anchorWord) {
          (rhyme.rhymingWords + rhyme.oddOneOut).shuffled(Random(rhyme.anchorWord.hashCode()))
        }
      val picked = oddPickedMap[rhyme.anchorWord]
      val solved = oddSolvedMap[rhyme.anchorWord] == true

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineAmber.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "${rhyme.emoji} Rhymes with ${rhyme.anchorWord} — tap the ODD word:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = InkNavy,
          )
          FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            mixedWords.forEach { word ->
              val isOdd = word == rhyme.oddOneOut
              val isPicked = picked == word
              val bg =
                when {
                  solved && isOdd -> MintGreen
                  isPicked && !isOdd -> CoralPrimary
                  else -> Color.White
                }
              val fg = if (isPicked && !isOdd) Color.White else InkNavy
              Surface(
                onClick = {
                  audioPlayer.speakWord(word, languageMode)
                  if (!solved) {
                    oddPickedMap[rhyme.anchorWord] = word
                    if (isOdd) {
                      oddSolvedMap[rhyme.anchorWord] = true
                      onAwardStars(1)
                      audioPlayer.playCelebrationChime()
                    } else {
                      audioPlayer.playGentleTryAgain()
                    }
                  }
                },
                color = bg,
                shape = RoundedCornerShape(12.dp),
                modifier =
                  Modifier
                    .minimumInteractiveComponentSize()
                    .border(
                      width = 1.dp,
                      color = if (solved && isOdd) MintGreen else SunshineAmber.copy(alpha = 0.6f),
                      shape = RoundedCornerShape(12.dp),
                    ),
              ) {
                Text(
                  text = word,
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  color = fg,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
              }
            }
          }
          if (solved) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MintGreen, modifier = Modifier.size(18.dp))
              Text(
                text = "Sahi! '${rhyme.oddOneOut}' ki awaaz alag hai! (+1 ⭐)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = InkNavy,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
internal fun PictureStoriesTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
) {
  val stories = UpperCurriculumCatalog.pictureStories
  var storyIdx by remember { mutableIntStateOf(0) }
  val story = stories[storyIdx]

  var blankChoice by remember(storyIdx) { mutableStateOf<Int?>(null) }
  var blankSolved by remember(storyIdx) { mutableStateOf(false) }
  var quizChoice by remember(storyIdx) { mutableStateOf<Int?>(null) }
  var quizSolved by remember(storyIdx) { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "Picture Stories",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Surface(color = CoralPrimary.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Story ${storyIdx + 1} of ${stories.size}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = CoralPrimary,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 2.dp),
    ) {
      itemsIndexed(stories) { idx, s ->
        FilterChip(
          selected = storyIdx == idx,
          onClick = { storyIdx = idx },
          label = { Text("${idx + 1}. ${s.title}", fontWeight = FontWeight.Bold) },
          modifier = Modifier.minimumInteractiveComponentSize(),
        )
      }
    }

    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = story.emojiBanner,
          fontSize = 44.sp,
          modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
          text = story.title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold,
          color = CoralPrimary,
        )
        story.lines.forEach { line ->
          Text(text = line, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        }
        Surface(
          color = SkyTeal.copy(alpha = 0.12f),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
            text = "Kahani: ${story.hinglishSummary}",
            style = MaterialTheme.typography.bodySmall,
            color = InkNavy,
            modifier = Modifier.padding(10.dp),
          )
        }
        Button(
          onClick = { audioPlayer.speakText(story.lines.joinToString(" ")) },
          colors = ButtonDefaults.buttonColors(containerColor = SkyTeal, contentColor = Color.White),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Listen Story", fontWeight = FontWeight.Bold)
        }
      }
    }

    // Spelling Fill in the blank
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Fill in the Blank:", style = MaterialTheme.typography.labelLarge, color = PlumPurple)
        Text(story.blankPrompt, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
          story.blankOptions.forEachIndexed { idx, opt ->
            val picked = blankChoice == idx
            val correct = idx == story.blankCorrectIndex
            val bg =
              when {
                picked && correct -> MintGreen
                picked && !correct -> CoralPrimary
                else -> MaterialTheme.colorScheme.surfaceVariant
              }
            Surface(
              onClick = {
                if (!blankSolved) {
                  blankChoice = idx
                  if (correct) {
                    blankSolved = true
                    onAwardStars(1)
                    audioPlayer.playCelebrationChime()
                    audioPlayer.speakWord(opt, languageMode)
                  } else {
                    audioPlayer.playGentleTryAgain()
                  }
                }
              },
              color = bg,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                Text(opt, fontWeight = FontWeight.Bold, color = if (picked && !correct) Color.White else InkNavy)
              }
            }
          }
        }
        if (blankSolved) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MintGreen)
            Text("Correct spelling! (+1 ⭐)", style = MaterialTheme.typography.labelLarge, color = InkNavy)
          }
        }
      }
    }

    // Comprehension Quiz
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Story Question:", style = MaterialTheme.typography.labelLarge, color = CoralPrimary)
        Text(story.comprehensionQuestion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        story.comprehensionOptions.forEachIndexed { idx, opt ->
          val picked = quizChoice == idx
          val correct = idx == story.comprehensionCorrectIndex
          val bg =
            when {
              picked && correct -> MintGreen
              picked && !correct -> CoralPrimary
              else -> MaterialTheme.colorScheme.surfaceVariant
            }
          Surface(
            onClick = {
              if (!quizSolved) {
                quizChoice = idx
                if (correct) {
                  quizSolved = true
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
              color = if (picked && !correct) Color.White else InkNavy,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            )
          }
        }
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedButton(
        onClick = { storyIdx = (storyIdx - 1 + stories.size) % stories.size },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Prev Story", fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = { storyIdx = (storyIdx + 1) % stories.size },
        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Text("Next Story", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }
    }
  }
}
