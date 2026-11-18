package com.example.feature.stages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.AutoAwesome
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
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UkgStageScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Word Families", "Digraphs", "Magic 'E'")

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
                .testTag("ukg_back_button"),
          ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
          }

          Surface(
            color = SkyTeal.copy(alpha = 0.16f),
            shape = RoundedCornerShape(50),
          ) {
            Text(
              text = "Level 3 · UKG (Age 4-5)",
              style = MaterialTheme.typography.labelLarge,
              color = SkyTeal,
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
              modifier = Modifier.minimumInteractiveComponentSize().testTag("ukg_tab_$idx"),
            )
          }
        }

        when (selectedTab) {
          0 -> WordFamiliesTab(languageMode = languageMode, audioPlayer = audioPlayer)
          1 -> DigraphsTab(languageMode = languageMode, audioPlayer = audioPlayer)
          else -> MagicETab(languageMode = languageMode, audioPlayer = audioPlayer)
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordFamiliesTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val families = EarlyCurriculumCatalog.wordFamilies
  var familyIdx by remember { mutableIntStateOf(0) }
  val group = families[familyIdx]

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      families.forEachIndexed { idx, item ->
        FilterChip(
          selected = familyIdx == idx,
          onClick = {
            familyIdx = idx
            audioPlayer.speakBilingualScript(
              hindiDevanagariScript = item.hindiVoiceTip,
              englishScript = "Let us learn the ${item.ending} word family!",
              languageMode = languageMode,
            )
          },
          label = { Text("${item.ending} family", fontWeight = FontWeight.Bold) },
          modifier = Modifier.minimumInteractiveComponentSize(),
        )
      }
    }

    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = SunshineAmber.copy(alpha = 0.22f)),
      modifier = Modifier.fillMaxWidth().border(1.5.dp, SunshineAmber, RoundedCornerShape(22.dp)),
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Word Family: ${group.ending}",
          style = MaterialTheme.typography.titleLarge,
          color = InkNavy,
        )
        Text(
          text = group.hinglishTip,
          style = MaterialTheme.typography.bodyMedium,
          color = InkNavy.copy(alpha = 0.9f),
        )
      }
    }

    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      group.words.forEach { entry ->
        Card(
          onClick = { audioPlayer.speakWord(entry.word, languageMode) },
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.width(156.dp).minimumInteractiveComponentSize(),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(text = entry.emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = entry.onset,
                style = MaterialTheme.typography.headlineMedium,
                color = CoralPrimary,
                fontWeight = FontWeight.ExtraBold,
              )
              Text(
                text = group.ending.removePrefix("-"),
                style = MaterialTheme.typography.headlineMedium,
                color = SkyTeal,
                fontWeight = FontWeight.ExtraBold,
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = entry.hinglishMeaning,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DigraphsTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val digraphs = EarlyCurriculumCatalog.digraphs
  var selectedIdx by remember { mutableIntStateOf(0) }
  val item = digraphs[selectedIdx]

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      digraphs.forEachIndexed { idx, dg ->
        FilterChip(
          selected = selectedIdx == idx,
          onClick = {
            selectedIdx = idx
            audioPlayer.speakSoundToken(dg.soundSyllable, dg.hindiSound, languageMode)
          },
          label = { Text("${dg.digraph} (${dg.hindiSound})", fontWeight = FontWeight.Bold) },
          modifier = Modifier.minimumInteractiveComponentSize(),
        )
      }
    }

    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = SkyTeal),
      modifier =
        Modifier
          .fillMaxWidth()
          .clickable {
            audioPlayer.speakBilingualScript(
              hindiDevanagariScript = item.hindiVoiceScript,
              englishScript = "${item.digraph} makes ${item.soundSyllable}, as in ${item.exampleWord}.",
              languageMode = languageMode,
            )
          },
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
              text = item.digraph,
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.ExtraBold,
              color = CoralPrimary,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
          }
          Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
          Surface(color = Color.White.copy(alpha = 0.22f), shape = RoundedCornerShape(14.dp)) {
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
            color = SunshineAmber,
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
              englishScript = "${item.soundSyllable}! ${item.exampleWord}.",
              languageMode = languageMode,
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Listen Digraph", fontWeight = FontWeight.Bold)
        }
      }
    }

    Text(
      text = "Tap more '${item.digraph}' words:",
      style = MaterialTheme.typography.titleMedium,
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      item.moreWords.forEach { word ->
        Surface(
          onClick = { audioPlayer.speakWord(word, languageMode) },
          color = MaterialTheme.colorScheme.surface,
          shape = RoundedCornerShape(16.dp),
          modifier =
            Modifier
              .minimumInteractiveComponentSize()
              .border(1.5.dp, SkyTeal.copy(alpha = 0.45f), RoundedCornerShape(16.dp)),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = SkyTeal, modifier = Modifier.size(18.dp))
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
private fun MagicETab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val pairs = EarlyCurriculumCatalog.magicEWords
  var pairIdx by remember { mutableIntStateOf(0) }
  var magicEActive by remember(pairIdx) { mutableStateOf(false) }
  val pair = pairs[pairIdx]

  val cardBg by animateColorAsState(
    targetValue = if (magicEActive) PlumPurple else CoralPrimary,
    label = "magicEBg",
  )

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      pairs.forEachIndexed { idx, item ->
        FilterChip(
          selected = pairIdx == idx,
          onClick = { pairIdx = idx },
          label = { Text("${item.shortWord} → ${item.magicWord}", fontWeight = FontWeight.Bold) },
          modifier = Modifier.minimumInteractiveComponentSize(),
        )
      }
    }

    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = cardBg),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = if (magicEActive) pair.magicEmoji else pair.shortEmoji,
          fontSize = 56.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = pair.shortWord,
            fontSize = 52.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
          )
          if (magicEActive) {
            Text(
              text = "e",
              fontSize = 52.sp,
              fontWeight = FontWeight.ExtraBold,
              color = SunshineAmber,
            )
          }
        }
        Text(
          text = if (magicEActive) pair.magicMeaning else pair.shortMeaning,
          style = MaterialTheme.typography.titleMedium,
          color = Color.White.copy(alpha = 0.94f),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = pair.hinglishRuleTip,
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White.copy(alpha = 0.9f),
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            magicEActive = !magicEActive
            if (magicEActive) {
              audioPlayer.playCelebrationChime()
              audioPlayer.speakWord(pair.magicWord, languageMode)
            } else {
              audioPlayer.playPopSound()
              audioPlayer.speakWord(pair.shortWord, languageMode)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SunshineAmber, contentColor = InkNavy),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier.fillMaxWidth().height(52.dp).testTag("toggle_magic_e_button"),
        ) {
          Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (magicEActive) "Remove Magic 'e' (${pair.shortWord})" else "Add Magic 'e' ✨ (${pair.magicWord})",
            fontWeight = FontWeight.Bold,
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = {
            audioPlayer.speakBilingualScript(
              hindiDevanagariScript = pair.hindiVoiceScript,
              englishScript = "${pair.shortWord} becomes ${pair.magicWord} with Magic E!",
              languageMode = languageMode,
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Hear Magic Rule", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
