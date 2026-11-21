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
import com.example.domain.curriculum.EarlyCurriculumCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import kotlin.random.Random

@Composable
fun UkgStageScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
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
          0 ->
            WordFamiliesTab(
              languageMode = languageMode,
              audioPlayer = audioPlayer,
              onAwardStars = onAwardStars,
            )
          1 -> DigraphsTab(languageMode = languageMode, audioPlayer = audioPlayer)
          else ->
            MagicETab(
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
private fun WordFamiliesTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit,
) {
  val families = EarlyCurriculumCatalog.wordFamilies
  var familyIdx by remember { mutableIntStateOf(0) }
  val group = families[familyIdx]

  val quizTarget = remember(familyIdx) { group.words[familyIdx % group.words.size] }
  val quizOptions =
    remember(familyIdx) {
      val others = group.words.filter { it.word != quizTarget.word }.shuffled(Random(familyIdx + 9)).take(2)
      (listOf(quizTarget) + others).shuffled(Random(familyIdx + 3))
    }
  var pickedQuizWord by remember(familyIdx) { mutableStateOf<String?>(null) }
  var quizSolved by remember(familyIdx) { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "8 Word Families",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Surface(color = SunshineAmber.copy(alpha = 0.25f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Family ${familyIdx + 1} of ${families.size}",
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
      itemsIndexed(families) { idx, item ->
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
          label = { Text("${item.ending} (${item.words.size})", fontWeight = FontWeight.Bold) },
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

    // Quick Family Challenge
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text =
              if (languageMode == LanguageMode.HINGLISH)
                "Quick Quiz: '${quizTarget.emoji} ${quizTarget.hinglishMeaning}' kaun sa shabd hai?"
              else "Quick Check: Tap the word for ${quizTarget.emoji} (${quizTarget.hinglishMeaning}):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
          )
          IconButton(
            onClick = { audioPlayer.speakWord(quizTarget.word, languageMode) },
            modifier = Modifier.minimumInteractiveComponentSize(),
          ) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "Hear word", tint = SkyTeal)
          }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          quizOptions.forEach { opt ->
            val picked = pickedQuizWord == opt.word
            val isRight = opt.word == quizTarget.word
            val bg =
              when {
                picked && isRight -> MintGreen
                picked && !isRight -> CoralPrimary
                else -> MaterialTheme.colorScheme.surfaceVariant
              }
            Surface(
              onClick = {
                if (!quizSolved) {
                  pickedQuizWord = opt.word
                  if (isRight) {
                    quizSolved = true
                    onAwardStars(2)
                    audioPlayer.playCelebrationChime()
                    audioPlayer.speakWord(opt.word, languageMode)
                  } else {
                    audioPlayer.playGentleTryAgain()
                  }
                }
              },
              color = bg,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.weight(1f).height(52.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = opt.word,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (picked && !isRight) Color.White else InkNavy,
                )
              }
            }
          }
        }
        if (quizSolved) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MintGreen)
            Text("Shabaash! Sahi shabd chuna! (+2 ⭐)", style = MaterialTheme.typography.labelLarge, color = InkNavy)
          }
        }
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedButton(
        onClick = { familyIdx = (familyIdx - 1 + families.size) % families.size },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Prev Family", fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = { familyIdx = (familyIdx + 1) % families.size },
        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Text("Next Family", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }
    }
  }
}
