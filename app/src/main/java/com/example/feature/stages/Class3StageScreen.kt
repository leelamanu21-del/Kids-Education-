package com.example.feature.stages

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.curriculum.Class3CurriculumCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal

@Composable
fun Class3StageScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Vowel Teams (10)", "Prefixes & Plurals", "Tenses & Articles")

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
        StageTopBar(
          levelLabel = "Level 6 · Class 3 (Age 7-8)",
          accentColor = PlumPurple,
          backButtonTag = "class3_back_button",
          onBack = onBack,
        )

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
          tabs.forEachIndexed { idx, title ->
            Tab(
              selected = selectedTab == idx,
              onClick = { selectedTab = idx },
              text = { Text(title, fontWeight = FontWeight.Bold) },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("class3_tab_$idx"),
            )
          }
        }

        when (selectedTab) {
          0 ->
            VowelTeamsTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
          1 ->
            AffixBuilderTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
          else ->
            Class3GrammarQuizTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VowelTeamsTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val items = Class3CurriculumCatalog.vowelTeams
  var idx by remember { mutableIntStateOf(0) }
  val item = items[idx]
  var picked by remember(idx) { mutableStateOf<Int?>(null) }
  var solved by remember(idx) { mutableStateOf(false) }
  val solvedSet = remember { mutableStateListOf<Int>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    StageCounterAndScoreRow(
      counterText = "Team ${idx + 1} of ${items.size}",
      scoreText = "Score: ${solvedSet.size} / ${items.size}",
      accentColor = PlumPurple,
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
        Text(item.emoji, fontSize = 48.sp)
        Text(
          text = "${item.team}  (${item.hindiSound})",
          style = MaterialTheme.typography.displaySmall,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
        )
        Text(
          text = "Anchor Word: ${item.exampleWord}",
          style = MaterialTheme.typography.titleMedium,
          color = Color.White.copy(alpha = 0.94f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Button(
            onClick = { audioPlayer.speakSoundToken(item.spokenPhoneme, item.hindiSound, languageMode) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(48.dp),
          ) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Hear Sound", fontWeight = FontWeight.Bold)
          }
          Button(
            onClick = { audioPlayer.speakBilingualScript(item.hindiVoiceScript, item.hinglishTip, languageMode) },
            colors = ButtonDefaults.buttonColors(containerColor = SkyTeal, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(48.dp),
          ) {
            Text("Teacher Tip", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Surface(
      color = MaterialTheme.colorScheme.surface,
      shape = RoundedCornerShape(18.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = if (languageMode == LanguageMode.HINGLISH) "Hinglish Rule: ${item.hinglishTip}" else item.hinglishTip,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text("Practice Words · Tap to Hear:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          item.sampleWords.forEach { w ->
            Surface(
              onClick = { audioPlayer.speakWord(w, languageMode) },
              color = PlumPurple.copy(alpha = 0.12f),
              shape = RoundedCornerShape(50),
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Text(
                text = w,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PlumPurple,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              )
            }
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
        Text(item.quizQuestion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
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
              modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)) {
                Text(
                  text = opt,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  color = if (isPicked && !isCorrect) Color.White else InkNavy,
                )
              }
            }
          }
        }
        if (solved) {
          Text("Shabaash! Correct vowel team match! (+1 ⭐)", style = MaterialTheme.typography.labelLarge, color = InkNavy)
        }
      }
    }

    PrevNextNavRow(
      prevLabel = "Prev Team",
      nextLabel = "Next Team",
      accentColor = PlumPurple,
      onPrev = { idx = (idx - 1 + items.size) % items.size },
      onNext = { idx = (idx + 1) % items.size },
    )
  }
}
