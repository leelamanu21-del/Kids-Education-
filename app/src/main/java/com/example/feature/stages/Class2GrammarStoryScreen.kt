package com.example.feature.stages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.curriculum.EarlyCurriculumCatalog
import com.example.domain.model.GrammarCategory
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber

@Composable
fun Class2GrammarStoryScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Grammar", "Opposites & Rhymes", "Stories")

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
            .widthIn(max = 700.dp)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                .testTag("class2_back_button"),
          ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
          }

          Surface(
            color = MintGreen.copy(alpha = 0.24f),
            shape = RoundedCornerShape(50),
          ) {
            Text(
              text = "Level 5 · Class 2 (Age 6-7)",
              style = MaterialTheme.typography.labelLarge,
              color = InkNavy,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            )
          }

          Spacer(modifier = Modifier.size(48.dp))
        }

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
          tabs.forEachIndexed { idx, title ->
            Tab(
              selected = selectedTab == idx,
              onClick = { selectedTab = idx },
              text = { Text(title, fontWeight = FontWeight.Bold) },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("class2_tab_$idx"),
            )
          }
        }

        when (selectedTab) {
          0 -> GrammarTab(languageMode = languageMode, audioPlayer = audioPlayer)
          1 -> OppositesAndRhymesTab(languageMode = languageMode, audioPlayer = audioPlayer)
          else -> PictureStoriesTab(languageMode = languageMode, audioPlayer = audioPlayer)
        }
      }
    }
  }
}

@Composable
private fun GrammarTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val items = EarlyCurriculumCatalog.grammarItems
  var questionIdx by remember { mutableIntStateOf(0) }
  val item = items[questionIdx]
  var selectedCategory by remember(questionIdx) { mutableStateOf<GrammarCategory?>(null) }
  var solved by remember(questionIdx) { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Grammar Guide Cards
    GrammarCategory.entries.forEach { cat ->
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text(text = cat.emoji, fontSize = 28.sp)
          Column {
            Text(
              text = if (languageMode == LanguageMode.HINGLISH) cat.titleHinglish else cat.titleEn,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = cat.hintHinglish,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }

    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = PlumPurple),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Text(text = item.emoji, fontSize = 50.sp)
        Text(
          text = item.word,
          style = MaterialTheme.typography.displaySmall,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
        )
        Text(
          text = "\"${item.sentence}\"",
          style = MaterialTheme.typography.bodyLarge,
          color = Color.White.copy(alpha = 0.92f),
          textAlign = TextAlign.Center,
        )
        Button(
          onClick = { audioPlayer.speakText("${item.word}. ${item.sentence}") },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Hear Word", fontWeight = FontWeight.Bold)
        }
      }
    }

    Text(
      text = "Choose what '${item.word}' is:",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      GrammarCategory.entries.forEach { cat ->
        val isPicked = selectedCategory == cat
        val isCorrect = cat == item.category
        val bg =
          when {
            isPicked && isCorrect -> MintGreen
            isPicked && !isCorrect -> CoralPrimary
            else -> MaterialTheme.colorScheme.surface
          }
        val fg =
          when {
            isPicked && isCorrect -> InkNavy
            isPicked && !isCorrect -> Color.White
            else -> MaterialTheme.colorScheme.onSurface
          }
        Surface(
          onClick = {
            if (!solved) {
              selectedCategory = cat
              if (isCorrect) {
                solved = true
                audioPlayer.playCelebrationChime()
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
          Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(cat.emoji, fontSize = 22.sp)
            Text(
              text = cat.titleEn,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = fg,
            )
          }
        }
      }
    }

    if (solved) {
      Surface(
        color = MintGreen.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(item.hinglishTip, style = MaterialTheme.typography.bodyMedium, color = InkNavy)
          Button(
            onClick = { questionIdx = (questionIdx + 1) % items.size },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp),
          ) {
            Text("Next Word →", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OppositesAndRhymesTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Text("Opposites (Vilom Shabd) · Tap to Hear", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    EarlyCurriculumCatalog.opposites.forEach { pair ->
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
    Text("Rhyming Words (Ek Jaisi Aawaz)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    EarlyCurriculumCatalog.rhymeFamilies.forEach { rhyme ->
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineAmber.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "${rhyme.emoji} Rhymes with ${rhyme.anchorWord}:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = InkNavy,
          )
          FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rhyme.rhymingWords.forEach { word ->
              Surface(
                onClick = { audioPlayer.speakWord(word, languageMode) },
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Text(
                  text = word,
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  color = InkNavy,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PictureStoriesTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val stories = EarlyCurriculumCatalog.pictureStories
  var storyIdx by remember { mutableIntStateOf(0) }
  val story = stories[storyIdx]

  var blankChoice by remember(storyIdx) { mutableStateOf<Int?>(null) }
  var blankSolved by remember(storyIdx) { mutableStateOf(false) }
  var quizChoice by remember(storyIdx) { mutableStateOf<Int?>(null) }
  var quizSolved by remember(storyIdx) { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      stories.forEachIndexed { idx, s ->
        FilterChip(
          selected = storyIdx == idx,
          onClick = { storyIdx = idx },
          label = { Text(s.title, fontWeight = FontWeight.Bold) },
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
            Text("Correct spelling!", style = MaterialTheme.typography.labelLarge, color = InkNavy)
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
  }
}
