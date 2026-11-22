package com.example.feature.stages

import androidx.activity.compose.BackHandler
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
import com.example.domain.curriculum.GrammarRulesCatalog
import com.example.domain.curriculum.UpperCurriculumCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.InkNavy
import com.example.ui.theme.TangerineOrange

@Composable
fun Class1FluencyScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Blends (12)", "Sentence Reader (10)")

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
          SentenceReaderTab(
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
private fun BlendsTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val blends = UpperCurriculumCatalog.blends
  var selectedIdx by remember { mutableIntStateOf(0) }
  val item = blends[selectedIdx]

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "Consonant Blends",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Surface(color = TangerineOrange.copy(alpha = 0.18f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Blend ${selectedIdx + 1} of ${blends.size}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = InkNavy,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 2.dp),
    ) {
      itemsIndexed(blends) { idx, cluster ->
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

    GrammarTopicRuleCard(
      rule = GrammarRulesCatalog.class1Rules[0],
      languageMode = languageMode,
      audioPlayer = audioPlayer,
      accentColor = TangerineOrange,
    )

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

    Text("Tap words starting with '${item.blend}' (${item.sampleWords.size} examples):", style = MaterialTheme.typography.titleMedium)
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

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedButton(
        onClick = { selectedIdx = (selectedIdx - 1 + blends.size) % blends.size },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Prev Blend", fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = { selectedIdx = (selectedIdx + 1) % blends.size },
        colors = ButtonDefaults.buttonColors(containerColor = TangerineOrange, contentColor = InkNavy),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Text("Next Blend", fontWeight = FontWeight.Bold)
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
