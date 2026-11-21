package com.example.feature.parent

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.timer.ScreenTimePreset
import com.example.core.timer.SessionTimerUiState
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerProfile
import com.example.domain.model.LearningMode
import com.example.domain.model.LetterProgress
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import kotlin.random.Random

data class MathChallenge(
  val prompt: String,
  val answer: Int,
)

fun generateParentMathChallenge(): MathChallenge {
  return when (Random.nextInt(3)) {
    0 -> {
      val a = Random.nextInt(6, 15)
      val b = Random.nextInt(4, 12)
      MathChallenge("What is $a + $b?", a + b)
    }
    1 -> {
      val a = Random.nextInt(12, 24)
      val b = Random.nextInt(4, 10)
      MathChallenge("What is $a - $b?", a - b)
    }
    else -> {
      val a = Random.nextInt(3, 9)
      val b = Random.nextInt(3, 8)
      MathChallenge("What is $a × $b?", a * b)
    }
  }
}

@Composable
fun ParentMathGateDialog(
  title: String = "Parent Security Gate",
  subtitle: String = "Ask a grown-up to solve this quick math check.",
  onDismiss: () -> Unit,
  onSolved: () -> Unit,
) {
  var challenge by remember { mutableStateOf(generateParentMathChallenge()) }
  var answerInput by remember { mutableStateOf("") }
  var showError by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Lock,
        contentDescription = null,
        tint = PlumPurple,
      )
    },
    title = { Text(title, textAlign = TextAlign.Center) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
        )
        Surface(
          color = PlumPurple.copy(alpha = 0.12f),
          shape = RoundedCornerShape(16.dp),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = challenge.prompt,
              style = MaterialTheme.typography.headlineMedium,
              color = PlumPurple,
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
              onClick = {
                challenge = generateParentMathChallenge()
                answerInput = ""
                showError = false
              },
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "New math question",
                tint = PlumPurple,
              )
            }
          }
        }
        OutlinedTextField(
          value = answerInput,
          onValueChange = { input ->
            if (input.length <= 4 && input.all { it.isDigit() }) {
              answerInput = input
              showError = false
            }
          },
          label = { Text("Enter answer") },
          singleLine = true,
          isError = showError,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth().testTag("math_gate_input"),
        )
        if (showError) {
          Text(
            text = "Not quite right. Please try again!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val parsed = answerInput.trim().toIntOrNull()
          if (parsed == challenge.answer) {
            onSolved()
          } else {
            showError = true
          }
        },
        modifier = Modifier.testTag("math_gate_submit"),
      ) {
        Text("Unlock")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParentZoneScreen(
  profile: LearnerProfile,
  progressByLetter: Map<String, LetterProgress>,
  timerState: SessionTimerUiState,
  unlockedBadgesCount: Int = 0,
  totalBadgesCount: Int = 13,
  onSelectTimerPreset: (ScreenTimePreset) -> Unit,
  onTriggerBreakPreview: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  val masteredLetters =
    remember(progressByLetter) {
      PhonicsCurriculum.letters.filter { progressByLetter[it.letter]?.mastered == true }
    }
  val practicedLetters =
    remember(progressByLetter) {
      PhonicsCurriculum.letters.filter { (progressByLetter[it.letter]?.practiceCount ?: 0L) > 0L }
    }
  val unlockedWords =
    remember(profile.unlockedLevelOrder) {
      val maxGroup =
        when {
          profile.unlockedLevelOrder >= 20L -> 4
          profile.unlockedLevelOrder >= 14L -> 3
          profile.unlockedLevelOrder >= 7L -> 2
          else -> 1
        }
      PhonicsCurriculum.cvcWords.filter { it.groupNumber <= maxGroup }
    }

  val reportText =
    remember(profile, masteredLetters, timerState, unlockedBadgesCount, totalBadgesCount) {
      buildWhatsAppReportMessage(
        profile = profile,
        masteredLetters = masteredLetters.map { it.letter },
        wordsBlendedToday = timerState.wordsBlendedToday,
        sessionDuration = timerState.formattedElapsedMinutes,
        unlockedBadgesCount = unlockedBadgesCount,
        totalBadgesCount = totalBadgesCount,
      )
    }

  var unlockedGateVerified by remember { mutableStateOf(false) }
  var gateKey by remember { mutableIntStateOf(0) }

  if (!unlockedGateVerified) {
    // Embedded inline Math Gate safeguard if navigated directly
    var challenge by remember(gateKey) { mutableStateOf(generateParentMathChallenge()) }
    var input by remember(gateKey) { mutableStateOf("") }
    var wrong by remember(gateKey) { mutableStateOf(false) }

    Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
      ) {
        Card(
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Box(
              modifier =
                Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(PlumPurple.copy(alpha = 0.14f)),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = PlumPurple,
                modifier = Modifier.size(32.dp),
              )
            }
            Text(
              text = "Secret Parent Zone",
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "Solve this quick math check to enter Parent Insights & Reports.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
            )
            Text(
              text = challenge.prompt,
              style = MaterialTheme.typography.headlineLarge,
              color = PlumPurple,
            )
            OutlinedTextField(
              value = input,
              onValueChange = { v ->
                if (v.length <= 4 && v.all { it.isDigit() }) {
                  input = v
                  wrong = false
                }
              },
              label = { Text("Answer") },
              singleLine = true,
              isError = wrong,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.fillMaxWidth().testTag("inline_math_gate_input"),
            )
            if (wrong) {
              Text(
                text = "Incorrect answer. Please try again!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp),
              ) {
                Text("Back")
              }
              Button(
                onClick = {
                  if (input.trim().toIntOrNull() == challenge.answer) {
                    unlockedGateVerified = true
                  } else {
                    wrong = true
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlumPurple),
                modifier = Modifier.weight(1f).height(50.dp).testTag("inline_math_gate_submit"),
              ) {
                Text("Enter")
              }
            }
          }
        }
      }
    }
    return
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
  ) { innerPadding ->
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .statusBarsPadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier
            .fillMaxSize()
            .widthIn(max = 700.dp)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("parent_zone_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        // Top Header
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
                .testTag("parent_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "Back to home",
            )
          }
          Text(
            text = "Parent Zone",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.size(48.dp))
        }

        // Child Summary KPI Card
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              text = "${profile.childName}'s Daily Learning Report",
              style = MaterialTheme.typography.titleLarge,
            )
            Text(
              text =
                "Path: ${LearningMode.fromStorage(profile.preferredMode).label} • Audio: ${LanguageMode.fromStorage(profile.preferredLanguage).label}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              ParentMetricTile(
                value = "${profile.totalStars}",
                label = "Total Stars",
                accent = SunshineAmber,
                modifier = Modifier.weight(1f),
              )
              ParentMetricTile(
                value = "$unlockedBadgesCount/$totalBadgesCount",
                label = "Badges",
                accent = PlumPurple,
                modifier = Modifier.weight(1f),
              )
              ParentMetricTile(
                value = "${masteredLetters.size}/26",
                label = "Mastered",
                accent = MintGreen,
                modifier = Modifier.weight(1f),
              )
            }
          }
        }

        // Learned Letters & Words Breakdown
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = "Mastered & Practiced Sounds",
              style = MaterialTheme.typography.titleMedium,
            )
            if (practicedLetters.isEmpty() && masteredLetters.isEmpty()) {
              Text(
                text = "No letter lessons completed yet today. Start with SATPIN Group 1 (S, A, T, P, I, N)!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            } else {
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                (masteredLetters + practicedLetters).distinctBy { it.letter }.forEach { letter ->
                  val stars = (progressByLetter[letter.letter]?.starsEarned ?: 0L).toInt()
                  Surface(
                    color = MintGreen.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(14.dp),
                    modifier =
                      Modifier.border(1.dp, MintGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(16.dp),
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "${letter.letter} (${letter.phonemeSymbol})",
                        style = MaterialTheme.typography.labelLarge,
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = GoldStar,
                        modifier = Modifier.size(15.dp),
                      )
                      Text(
                        text = "$stars",
                        style = MaterialTheme.typography.labelMedium,
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Unlocked Blending Vocabulary (${unlockedWords.size} words)",
              style = MaterialTheme.typography.titleMedium,
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              unlockedWords.take(10).forEach { cvc ->
                Surface(
                  color = SkyTeal.copy(alpha = 0.12f),
                  shape = RoundedCornerShape(50),
                ) {
                  Text(
                    text = "${cvc.emoji} ${cvc.word}",
                    style = MaterialTheme.typography.labelLarge,
                    color = InkNavy,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  )
                }
              }
            }
          }
        }

        // WhatsApp Progress Share Card
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F8EF)),
          modifier =
            Modifier
              .fillMaxWidth()
              .border(1.5.dp, Color(0xFF25D366).copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              text = "Family WhatsApp Report",
              style = MaterialTheme.typography.titleLarge,
              color = InkNavy,
            )
            Surface(
              color = Color.White,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = reportText,
                style = MaterialTheme.typography.bodyMedium,
                color = InkNavy,
                modifier = Modifier.padding(14.dp),
              )
            }
            Button(
              onClick = { shareReportOnWhatsApp(context, reportText) },
              shape = RoundedCornerShape(18.dp),
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF1FAF53),
                  contentColor = Color.White,
                ),
              modifier = Modifier.fillMaxWidth().height(54.dp).testTag("share_whatsapp_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Share Progress on WhatsApp", fontWeight = FontWeight.Bold)
            }
          }
        }

        // Guilt-Free Screen-Time Timer Settings
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                tint = CoralPrimary,
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Guilt-Free Eye-Rest Timer",
                style = MaterialTheme.typography.titleMedium,
              )
            }
            Text(
              text =
                "Current session: ${timerState.formattedElapsedMinutes} / ${timerState.preset.minutes} min. Automatically shows a calm eye-rest break screen when reached.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              ScreenTimePreset.entries.forEach { preset ->
                FilterChip(
                  selected = timerState.preset == preset,
                  onClick = { onSelectTimerPreset(preset) },
                  label = { Text(preset.label) },
                  modifier =
                    Modifier
                      .minimumInteractiveComponentSize()
                      .testTag("timer_preset_${preset.minutes}"),
                )
              }
            }
            OutlinedButton(
              onClick = onTriggerBreakPreview,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp).testTag("preview_eye_rest_button"),
            ) {
              Text("Start Eye-Rest Break Now")
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ParentMetricTile(
  value: String,
  label: String,
  accent: Color,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = accent.copy(alpha = 0.14f),
    shape = RoundedCornerShape(18.dp),
    modifier = modifier.border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium,
        color = InkNavy,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = InkNavy.copy(alpha = 0.78f),
        textAlign = TextAlign.Center,
      )
    }
  }
}

fun buildWhatsAppReportMessage(
  profile: LearnerProfile,
  masteredLetters: List<String>,
  wordsBlendedToday: Int,
  sessionDuration: String,
  unlockedBadgesCount: Int = 0,
  totalBadgesCount: Int = 13,
): String {
  val lettersText =
    if (masteredLetters.isEmpty()) "Starting SATPIN Group 1 (S, A, T, P, I, N)"
    else masteredLetters.joinToString(", ")
  return buildString {
    append("🌟 BolBuddy Daily Report for ${profile.childName} 🌟\n")
    append("⭐ Total Stars: ${profile.totalStars} | 🏆 Badges: $unlockedBadgesCount/$totalBadgesCount\n")
    append("🔤 Mastered Sounds (${masteredLetters.size}/26): $lettersText\n")
    append("🪄 Words Blended Today: $wordsBlendedToday\n")
    append("⏱️ Active Learning Time: $sessionDuration\n")
    append("🗣️ Mode: ${LearningMode.fromStorage(profile.preferredMode).label} (${LanguageMode.fromStorage(profile.preferredLanguage).label})")
  }
}

private fun shareReportOnWhatsApp(context: Context, message: String) {
  val encoded = Uri.encode(message)
  val waUri = Uri.parse("https://wa.me/?text=$encoded")
  val waIntent = Intent(Intent.ACTION_VIEW, waUri).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }
  try {
    context.startActivity(waIntent)
  } catch (e: Exception) {
    val fallbackIntent =
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
      }
    runCatching {
      context.startActivity(Intent.createChooser(fallbackIntent, "Share Phonics Progress"))
    }.onFailure {
      Toast.makeText(context, "No sharing app found on this device.", Toast.LENGTH_SHORT).show()
    }
  }
}
