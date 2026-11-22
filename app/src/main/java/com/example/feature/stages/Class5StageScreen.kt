package com.example.feature.stages

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
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
import com.example.domain.curriculum.Class5CurriculumCatalog
import com.example.domain.curriculum.GrammarRulesCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber

@Composable
fun Class5StageScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Idioms & Proverbs", "Voice & Figures", "Critical Reading (5)")

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
          levelLabel = "Level 8 · Class 5 (Age 9-10)",
          accentColor = InkNavy,
          backButtonTag = "class5_back_button",
          onBack = onBack,
        )

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
          tabs.forEachIndexed { idx, title ->
            Tab(
              selected = selectedTab == idx,
              onClick = { selectedTab = idx },
              text = { Text(title, fontWeight = FontWeight.Bold) },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("class5_tab_$idx"),
            )
          }
        }

        when (selectedTab) {
          0 ->
            IdiomsAndProverbsTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
          1 ->
            VoiceAndFiguresTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
          else ->
            ReadingPassagesBoard(
              passages = Class5CurriculumCatalog.criticalPassages,
              accentColor = InkNavy,
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
private fun IdiomsAndProverbsTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val items = Class5CurriculumCatalog.idiomsAndProverbs
  var idx by remember { mutableIntStateOf(0) }
  val item = items[idx]
  var picked by remember(idx) { mutableStateOf<Int?>(null) }
  var solved by remember(idx) { mutableStateOf(false) }
  val solvedSet = remember { mutableStateListOf<Int>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    StageCounterAndScoreRow(
      counterText = "Expression ${idx + 1} of ${items.size}",
      scoreText = "Score: ${solvedSet.size} / ${items.size}",
      accentColor = InkNavy,
    )

    GrammarRulesCatalog.class5IdiomRulesByType[item.typeTag]?.let { idiomRule ->
      GrammarTopicRuleCard(
        rule = idiomRule,
        languageMode = languageMode,
        audioPlayer = audioPlayer,
        accentColor = InkNavy,
      )
    }

    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = InkNavy),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Surface(color = SunshineAmber, shape = RoundedCornerShape(50)) {
          Text(
            text = item.typeTag,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = InkNavy,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          )
        }
        Text(item.emoji, fontSize = 48.sp)
        Text(
          text = "\"${item.expression}\"",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
          textAlign = TextAlign.Center,
        )
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) item.hinglishExplanation
            else item.simpleMeaningEn,
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White.copy(alpha = 0.92f),
          textAlign = TextAlign.Center,
        )
        Button(
          onClick = {
            audioPlayer.speakBilingualScript(
              hindiDevanagariScript = "${item.hindiVoiceScript} उदाहरण: ${item.exampleSentence}",
              englishScript = "${item.expression}. It means ${item.simpleMeaningEn} Example: ${item.exampleSentence}",
              languageMode = languageMode,
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Hear Meaning", fontWeight = FontWeight.Bold)
        }
      }
    }

    Surface(
      color = SkyTeal.copy(alpha = 0.14f),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text = "Example: \"${item.exampleSentence}\"",
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
        Text(item.quizQuestion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
            Text("Bahut badhiya! Idiom star (+1 ⭐)", style = MaterialTheme.typography.labelLarge, color = InkNavy)
          }
        }
      }
    }

    PrevNextNavRow(
      prevLabel = "Prev Idiom",
      nextLabel = "Next Idiom",
      accentColor = InkNavy,
      onPrev = { idx = (idx - 1 + items.size) % items.size },
      onNext = { idx = (idx + 1) % items.size },
    )

    ExpandableRuleBookCard(
      title = "Idioms, Proverbs & Similes Rule Book",
      rules = GrammarRulesCatalog.class5IdiomRulesByType.values.toList(),
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = InkNavy,
    )
  }
}

@Composable
private fun VoiceAndFiguresTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val items = Class5CurriculumCatalog.voiceAndFigures
  var idx by remember { mutableIntStateOf(0) }
  val item = items[idx]
  var picked by remember(idx) { mutableStateOf<Int?>(null) }
  var solved by remember(idx) { mutableStateOf(false) }
  val solvedSet = remember { mutableStateListOf<Int>() }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    StageCounterAndScoreRow(
      counterText = "Challenge ${idx + 1} of ${items.size}",
      scoreText = "Score: ${solvedSet.size} / ${items.size}",
      accentColor = PlumPurple,
    )

    GrammarRulesCatalog.class5VoiceAndFigureRulesByTag[item.conceptTag]?.let { voiceRule ->
      GrammarTopicRuleCard(
        rule = voiceRule,
        languageMode = languageMode,
        audioPlayer = audioPlayer,
        accentColor = PlumPurple,
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
        Surface(color = PlumPurple.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
          Text(
            text = item.conceptTag,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = PlumPurple,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          )
        }
        Text(item.emoji, fontSize = 44.sp)
        Text(
          text = "\"${item.baseSentence}\"",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
        )
        Text(
          text = item.promptQuestion,
          style = MaterialTheme.typography.bodyLarge,
          color = PlumPurple,
          fontWeight = FontWeight.SemiBold,
          textAlign = TextAlign.Center,
        )
        Button(
          onClick = { audioPlayer.speakText("${item.baseSentence}. ${item.promptQuestion}") },
          colors = ButtonDefaults.buttonColors(containerColor = PlumPurple, contentColor = Color.White),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Hear Sentence", fontWeight = FontWeight.Bold)
        }
      }
    }

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
              audioPlayer.speakText(item.answerSentence)
            } else {
              audioPlayer.playGentleTryAgain()
            }
          }
        },
        color = bg,
        shape = RoundedCornerShape(16.dp),
        modifier =
          Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
      ) {
        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
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
      Surface(
        color = MintGreen.copy(alpha = 0.22f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) "${item.hinglishRuleTip} (+1 ⭐)"
            else "${item.answerSentence} (+1 ⭐)",
          style = MaterialTheme.typography.bodyMedium,
          color = InkNavy,
          modifier = Modifier.padding(14.dp),
        )
      }
    }

    PrevNextNavRow(
      prevLabel = "Prev Topic",
      nextLabel = "Next Topic",
      accentColor = PlumPurple,
      onPrev = { idx = (idx - 1 + items.size) % items.size },
      onNext = { idx = (idx + 1) % items.size },
    )

    ExpandableRuleBookCard(
      title = "Class 5 Voice, Speech & Figures Rule Book",
      rules = GrammarRulesCatalog.class5VoiceAndFigureRulesByTag.values.toList(),
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = PlumPurple,
    )
  }
}
