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
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.TangerineOrange

@Composable
fun Class1FluencyScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Blends (bl, gr, st)", "Sentence Reader")

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
                .testTag("class1_back_button"),
          ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
          }

          Surface(
            color = TangerineOrange.copy(alpha = 0.18f),
            shape = RoundedCornerShape(50),
          ) {
            Text(
              text = "Level 4 · Class 1 (Age 5-6)",
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
              modifier = Modifier.minimumInteractiveComponentSize().testTag("class1_tab_$idx"),
            )
          }
        }

        if (selectedTab == 0) {
          BlendsTab(languageMode = languageMode, audioPlayer = audioPlayer)
        } else {
          SentenceReaderTab(languageMode = languageMode, audioPlayer = audioPlayer)
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BlendsTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val blends = EarlyCurriculumCatalog.blends
  var selectedIdx by remember { mutableIntStateOf(0) }
  val item = blends[selectedIdx]

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      blends.forEachIndexed { idx, cluster ->
        FilterChip(
          selected = selectedIdx == idx,
          onClick = {
            selectedIdx = idx
            audioPlayer.speakSoundToken(cluster.spokenBlend, null, languageMode)
          },
          label = { Text("${cluster.blend} → ${cluster.exampleWord}", fontWeight = FontWeight.Bold) },
          modifier = Modifier.minimumInteractiveComponentSize(),
        )
      }
    }

    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = TangerineOrange),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(text = item.emoji, fontSize = 52.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(color = Color.White, shape = RoundedCornerShape(14.dp)) {
            Text(
              text = item.blend,
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.ExtraBold,
              color = TangerineOrange,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
          }
          Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
          Surface(color = Color.White.copy(alpha = 0.24f), shape = RoundedCornerShape(14.dp)) {
            Text(
              text = item.remainingPart,
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
          }
          Text("=", style = MaterialTheme.typography.headlineMedium, color = Color.White)
          Text(
            text = item.exampleWord,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = InkNavy,
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = item.hinglishTip,
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Button(
          onClick = {
            audioPlayer.speakBilingualScript(
              hindiDevanagariScript = item.hindiVoiceScript,
              englishScript = "${item.spokenBlend}, ${item.exampleWord}.",
              languageMode = languageMode,
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Listen Blend", fontWeight = FontWeight.Bold)
        }
      }
    }

    Text("Tap words starting with '${item.blend}':", style = MaterialTheme.typography.titleMedium)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      item.sampleWords.forEach { word ->
        Surface(
          onClick = { audioPlayer.speakWord(word, languageMode) },
          color = MaterialTheme.colorScheme.surface,
          shape = RoundedCornerShape(16.dp),
          modifier =
            Modifier
              .minimumInteractiveComponentSize()
              .border(1.5.dp, TangerineOrange.copy(alpha = 0.45f), RoundedCornerShape(16.dp)),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              Icons.AutoMirrored.Rounded.VolumeUp,
              contentDescription = null,
              tint = TangerineOrange,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(word, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SentenceReaderTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val sentences = EarlyCurriculumCatalog.fluencySentences
  var sentenceIdx by remember { mutableIntStateOf(0) }
  val item = sentences[sentenceIdx]
  var pickedOption by remember(sentenceIdx) { mutableStateOf<Int?>(null) }
  var solved by remember(sentenceIdx) { mutableStateOf(false) }

  val words = remember(sentenceIdx) { item.sentence.split(" ") }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      sentences.forEachIndexed { idx, s ->
        FilterChip(
          selected = sentenceIdx == idx,
          onClick = { sentenceIdx = idx },
          label = { Text("${s.emoji} Story #${idx + 1}", fontWeight = FontWeight.Bold) },
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

    // Comprehension Mini Quiz
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
            Text("Shabaash! Great reading!", style = MaterialTheme.typography.titleSmall, color = InkNavy)
          }
        }
      }
    }
  }
}
