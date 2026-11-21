package com.example.feature.stages

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.AutoAwesome
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
import com.example.domain.curriculum.EarlyCurriculumCatalog
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DigraphsTab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
) {
  val digraphs = EarlyCurriculumCatalog.digraphs
  var selectedIdx by remember { mutableIntStateOf(0) }
  val item = digraphs[selectedIdx]

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "Digraph Sounds",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Surface(color = SkyTeal.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Digraph ${selectedIdx + 1} of ${digraphs.size}",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = SkyTeal,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 2.dp),
    ) {
      itemsIndexed(digraphs) { idx, dg ->
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
      text = "Tap more '${item.digraph}' words (${item.moreWords.size} examples):",
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

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedButton(
        onClick = { selectedIdx = (selectedIdx - 1 + digraphs.size) % digraphs.size },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Prev Digraph", fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = { selectedIdx = (selectedIdx + 1) % digraphs.size },
        colors = ButtonDefaults.buttonColors(containerColor = SkyTeal),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Text("Next Digraph", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }
    }
  }
}

@Composable
internal fun MagicETab(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onAwardStars: (Int) -> Unit = {},
) {
  val pairs = EarlyCurriculumCatalog.magicEWords
  var pairIdx by remember { mutableIntStateOf(0) }
  var magicEActive by remember(pairIdx) { mutableStateOf(false) }
  val rewardedPairs = remember { androidx.compose.runtime.mutableStateListOf<String>() }
  val pair = pairs[pairIdx]

  val cardBg by animateColorAsState(
    targetValue = if (magicEActive) PlumPurple else CoralPrimary,
    label = "magicEBg",
  )

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Surface(color = PlumPurple.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
        Text(
          text = pair.vowelRuleTag,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = PlumPurple,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
      Surface(color = SunshineAmber.copy(alpha = 0.25f), shape = RoundedCornerShape(50)) {
        Text(
          text = "Pair ${pairIdx + 1} of ${pairs.size}",
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
      itemsIndexed(pairs) { idx, item ->
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
        Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50)) {
          Text(
            text = pair.vowelRuleTag,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
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
              if (pair.magicWord !in rewardedPairs) {
                rewardedPairs.add(pair.magicWord)
                onAwardStars(1)
              }
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

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedButton(
        onClick = { pairIdx = (pairIdx - 1 + pairs.size) % pairs.size },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Prev Pair", fontWeight = FontWeight.Bold)
      }
      Button(
        onClick = { pairIdx = (pairIdx + 1) % pairs.size },
        colors = ButtonDefaults.buttonColors(containerColor = PlumPurple),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).height(48.dp),
      ) {
        Text("Next Pair", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }
    }
  }
}
