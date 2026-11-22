package com.example.feature.stages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.curriculum.GrammarRulesCatalog
import com.example.domain.curriculum.UpperCurriculumCatalog
import com.example.domain.model.GrammarCategory
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple

@Composable
fun Class2GrammarStoryScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Grammar (18)", "Opposites & Rhymes", "Stories (5)")

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
          0 ->
            GrammarTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
          1 ->
            OppositesAndRhymesTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
          else ->
            PictureStoriesTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
        }
      }
    }
  }
}

@Composable
private fun GrammarTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val items = UpperCurriculumCatalog.grammarItems
  var questionIdx by remember { mutableIntStateOf(0) }
  val item = items[questionIdx]
  var selectedCategory by remember(questionIdx) { mutableStateOf<GrammarCategory?>(null) }
  var solved by remember(questionIdx) { mutableStateOf(false) }
  val solvedIndices = remember { mutableStateListOf<Int>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Surface(color = PlumPurple.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Word ${questionIdx + 1} of ${items.size}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = PlumPurple,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
      Surface(color = MintGreen.copy(alpha = 0.25f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Score: ${solvedIndices.size} / ${items.size}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = InkNavy,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }

    ExpandableRuleBookCard(
      title = "Class 2 Grammar Rules (Noun · Verb · Adjective)",
      rules = GrammarRulesCatalog.class2Rules.take(3),
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = PlumPurple,
      initiallyExpanded = true,
    )

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
                if (questionIdx !in solvedIndices) {
                  solvedIndices.add(questionIdx)
                  onAwardStars(1)
                }
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
          Text("${item.hinglishTip} (+1 ⭐)", style = MaterialTheme.typography.bodyMedium, color = InkNavy)
        }
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedButton(
        onClick = { questionIdx = (questionIdx - 1 + items.size) % items.size },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Prev Word", fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = { questionIdx = (questionIdx + 1) % items.size },
        colors = ButtonDefaults.buttonColors(containerColor = PlumPurple),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Text("Next Word", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }
    }
  }
}
