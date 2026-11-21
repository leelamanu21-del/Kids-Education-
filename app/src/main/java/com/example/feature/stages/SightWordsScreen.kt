package com.example.feature.stages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.domain.curriculum.EarlyCurriculumCatalog
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
fun SightWordsScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  val words = EarlyCurriculumCatalog.sightWords
  val setLabels = listOf("Set 1 (1-8)", "Set 2 (9-16)", "Set 3 (17-24)")
  var selectedSet by remember { mutableIntStateOf(0) }
  var selectedIndex by remember { mutableIntStateOf(0) }
  val current = words[selectedIndex]

  val setStart = selectedSet * 8
  val setEnd = minOf(setStart + 8, words.size)
  val currentSetWords = words.subList(setStart, setEnd)

  var spotTargetIndex by remember(selectedIndex) { mutableIntStateOf(selectedIndex) }
  var quizPicked by remember(selectedIndex) { mutableStateOf<String?>(null) }
  var quizSolved by remember(selectedIndex) { mutableStateOf(false) }

  val quizChoices =
    remember(spotTargetIndex) {
      val target = words[spotTargetIndex]
      val distractors =
        words
          .filter { it.word != target.word }
          .shuffled(Random(spotTargetIndex + 17))
          .take(2)
      (listOf(target) + distractors).sortedBy { it.word }
    }

  LaunchedEffect(selectedIndex) {
    audioPlayer.speakWord(current.word, languageMode)
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding).statusBarsPadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier
            .fillMaxSize()
            .widthIn(max = 680.dp)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
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
                .testTag("sight_words_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "Back",
            )
          }

          Surface(
            color = SunshineAmber.copy(alpha = 0.28f),
            shape = RoundedCornerShape(50),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.Visibility,
                contentDescription = null,
                tint = InkNavy,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "LKG · 24 Sight Words",
                style = MaterialTheme.typography.labelLarge,
                color = InkNavy,
              )
            }
          }

          Surface(
            color = SkyTeal.copy(alpha = 0.14f),
            shape = RoundedCornerShape(50),
          ) {
            Text(
              text = "${selectedIndex + 1} / ${words.size}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = SkyTeal,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
          }
        }

        // 3 Structured Sets Filter Row
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 2.dp),
        ) {
          itemsIndexed(setLabels) { setIdx, label ->
            FilterChip(
              selected = selectedSet == setIdx,
              onClick = {
                selectedSet = setIdx
                selectedIndex = setIdx * 8
              },
              label = { Text(label, fontWeight = FontWeight.Bold) },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("sight_set_$setIdx"),
            )
          }
        }

        // Active Set Sight Word Selector Strip (8 words per set)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          currentSetWords.forEachIndexed { localIdx, item ->
            val globalIdx = setStart + localIdx
            val selected = globalIdx == selectedIndex
            Surface(
              onClick = { selectedIndex = globalIdx },
              color = if (selected) CoralPrimary else MaterialTheme.colorScheme.surface,
              shape = RoundedCornerShape(16.dp),
              modifier =
                Modifier
                  .minimumInteractiveComponentSize()
                  .border(
                    1.5.dp,
                    if (selected) CoralPrimary else MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(16.dp),
                  )
                  .testTag("sight_chip_${item.word}"),
            ) {
              Text(
                text = item.word,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              )
            }
          }
        }

        // Main Flashcard
        Card(
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(containerColor = PlumPurple),
          elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
          modifier =
            Modifier
              .fillMaxWidth()
              .clickable { audioPlayer.speakWord(current.word, languageMode) },
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(text = current.emoji, fontSize = 52.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = current.word,
              fontSize = 60.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              color = Color.White.copy(alpha = 0.18f),
              shape = RoundedCornerShape(14.dp),
            ) {
              Text(
                text = "\"${current.exampleSentence}\"",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = current.hinglishTip,
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.94f),
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Button(
                onClick = { audioPlayer.speakWord(current.word, languageMode) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f).height(48.dp),
              ) {
                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Word", fontWeight = FontWeight.Bold)
              }
              OutlinedButton(
                onClick = {
                  audioPlayer.speakBilingualScript(
                    hindiDevanagariScript = current.hindiDevanagariCue,
                    englishScript = current.exampleSentence,
                    languageMode = languageMode,
                  )
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f).height(48.dp),
              ) {
                Text("Sentence / Tip", fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Spot the Sight Word Mini Quiz
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text =
                  if (languageMode == LanguageMode.HINGLISH)
                    "Spot the Word: '${words[spotTargetIndex].word}' kahan likha hai?"
                  else "Spot the Sight Word: Find '${words[spotTargetIndex].word}'",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
              )
              IconButton(
                onClick = { audioPlayer.speakWord(words[spotTargetIndex].word, languageMode) },
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                  contentDescription = "Hear target sight word",
                  tint = SkyTeal,
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              quizChoices.forEach { option ->
                val isSelected = quizPicked == option.word
                val isCorrect = option.word == words[spotTargetIndex].word
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
                    if (!quizSolved) {
                      quizPicked = option.word
                      if (isCorrect) {
                        quizSolved = true
                        onAwardStars(1)
                        audioPlayer.playCelebrationChime()
                        audioPlayer.speakWord(option.word, languageMode)
                      } else {
                        audioPlayer.playGentleTryAgain()
                      }
                    }
                  },
                  color = bg,
                  shape = RoundedCornerShape(18.dp),
                  modifier = Modifier.weight(1f).height(64.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = option.word,
                      style = MaterialTheme.typography.headlineSmall,
                      fontWeight = FontWeight.Bold,
                      color = fg,
                    )
                  }
                }
              }
            }

            if (quizSolved) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MintGreen)
                Text(
                  text =
                    if (languageMode == LanguageMode.HINGLISH) "Shabaash! Sahi pehchana! (+1 ⭐)"
                    else "Awesome! You spotted it! (+1 ⭐)",
                  style = MaterialTheme.typography.titleSmall,
                  color = InkNavy,
                )
              }
            }
          }
        }

        // Prev / Next Word Course Navigation
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          OutlinedButton(
            onClick = {
              val prev = (selectedIndex - 1 + words.size) % words.size
              selectedIndex = prev
              selectedSet = prev / 8
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f).height(50.dp),
          ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Prev Word", fontWeight = FontWeight.Bold)
          }
          Button(
            onClick = {
              val next = (selectedIndex + 1) % words.size
              selectedIndex = next
              selectedSet = next / 8
            },
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f).height(50.dp).testTag("next_sight_word_button"),
          ) {
            Text("Next Word", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}
