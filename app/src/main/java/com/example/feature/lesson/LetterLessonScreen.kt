package com.example.feature.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
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
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.core.ui.HinglishMicroTipCard
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.domain.model.PhonicsLetter
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.toColorSet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LetterLessonScreen(
  letter: PhonicsLetter,
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onCompleteLesson: (earnedStars: Int, quizScore: Int, mastered: Boolean) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  val palette = letter.palette.toColorSet()
  var bounceTrigger by remember { mutableStateOf(false) }
  val letterScale by
    animateFloatAsState(
      targetValue = if (bounceTrigger) 1.09f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      finishedListener = { bounceTrigger = false },
      label = "letterCardBounce",
    )

  // Trace Canvas strokes
  val tracePaths = remember(letter.letter) { mutableStateListOf<List<Offset>>() }
  var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
  var bubblePopCount by remember(letter.letter) { mutableIntStateOf(0) }

  // Quiz state
  val quizOptions = remember(letter.letter) { PhonicsCurriculum.quizOptionsFor(letter) }
  var selectedQuizLetter by remember(letter.letter) { mutableStateOf<String?>(null) }
  var quizCompleted by remember(letter.letter) { mutableStateOf(false) }
  var quizAttempts by remember(letter.letter) { mutableIntStateOf(0) }

  LaunchedEffect(letter.letter) {
    audioPlayer.speakLetterPhoneme(letter, languageMode)
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
            .widthIn(max = 680.dp)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        // Top Nav Row
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
                .testTag("lesson_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "Back to learning path",
            )
          }

          Surface(
            color = palette.fill.copy(alpha = 0.16f),
            shape = RoundedCornerShape(50),
          ) {
            Text(
              text = "SATPIN Step #${letter.satpinOrder} • ${letter.phonemeSymbol}",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            )
          }

          Spacer(modifier = Modifier.size(48.dp))
        }

        // 1. Hero Sound Card
        Card(
          shape = RoundedCornerShape(30.dp),
          colors = CardDefaults.cardColors(containerColor = palette.fill),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier =
            Modifier
              .fillMaxWidth()
              .scale(letterScale)
              .clickable {
                bounceTrigger = true
                audioPlayer.speakLetterPhoneme(letter, languageMode)
              },
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Text(
                text = "${letter.letter} ${letter.lowercase}",
                fontSize = 72.sp,
                style = MaterialTheme.typography.displayLarge,
                color = palette.content,
              )
              Spacer(modifier = Modifier.width(20.dp))
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = letter.exampleEmoji, fontSize = 54.sp)
                Text(
                  text = "${letter.exampleWord} (${letter.hindiScriptWord})",
                  style = MaterialTheme.typography.titleMedium,
                  color = palette.content,
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text =
                if (languageMode == LanguageMode.HINGLISH) letter.hinglishCue
                else letter.englishCue,
              style = MaterialTheme.typography.bodyLarge,
              color = palette.content,
              textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Button(
                onClick = {
                  bounceTrigger = true
                  audioPlayer.speakPhonemeOnly(letter)
                },
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = InkNavy,
                  ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f).height(50.dp).testTag("play_phoneme_button"),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sound ${letter.phonemeSymbol}", fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  bounceTrigger = true
                  audioPlayer.speakLetterPhoneme(letter, languageMode)
                },
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.18f),
                    contentColor = palette.content,
                  ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f).height(50.dp).testTag("play_story_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.AutoAwesome,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text =
                    if (languageMode == LanguageMode.HINGLISH) "Hinglish Story"
                    else "Listen Story",
                  fontWeight = FontWeight.Bold,
                )
              }
            }
          }
        }

        // Reusable Hinglish Micro-Tip & Audio Hint
        HinglishMicroTipCard(
          letter = letter,
          languageMode = languageMode,
          audioPlayer = audioPlayer,
        )

        // Sample Words Chips
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Tap Words to Hear ${letter.phonemeSymbol}",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              letter.sampleWords.forEach { word ->
                Surface(
                  onClick = {
                    audioPlayer.playPopSound()
                    audioPlayer.speakWord(word, languageMode)
                  },
                  color = palette.fill.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(16.dp),
                  modifier =
                    Modifier
                      .minimumInteractiveComponentSize()
                      .border(1.dp, palette.fill.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                      .testTag("sample_word_$word"),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                      contentDescription = null,
                      tint = palette.fill,
                      modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = word,
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                  }
                }
              }
            }
          }
        }

        // 2. Finger Trace Pad
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Rounded.Gesture,
                  contentDescription = null,
                  tint = SkyTeal,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Trace '${letter.letter}' with your finger",
                  style = MaterialTheme.typography.titleMedium,
                )
              }
              OutlinedButton(
                onClick = {
                  tracePaths.clear()
                  currentStroke = emptyList()
                },
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Refresh,
                  contentDescription = "Clear trace pad",
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear")
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
              modifier =
                Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .clip(RoundedCornerShape(20.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                  .border(
                    2.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(20.dp),
                  ),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = "${letter.letter} ${letter.lowercase}",
                fontSize = 112.sp,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.16f),
              )

              Canvas(
                modifier =
                  Modifier
                    .fillMaxSize()
                    .pointerInput(letter.letter) {
                      detectDragGestures(
                        onDragStart = { offset ->
                          currentStroke = listOf(offset)
                          bubblePopCount++
                        },
                        onDrag = { change, _ ->
                          change.consume()
                          currentStroke = currentStroke + change.position
                        },
                        onDragEnd = {
                          if (currentStroke.isNotEmpty()) {
                            tracePaths.add(currentStroke)
                            currentStroke = emptyList()
                            audioPlayer.playPopSound()
                          }
                        },
                      )
                    }
              ) {
                val drawStroke = { points: List<Offset> ->
                  if (points.size >= 2) {
                    val path = Path().apply {
                      moveTo(points.first().x, points.first().y)
                      for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                      }
                    }
                    drawPath(
                      path = path,
                      color = palette.fill,
                      style =
                        Stroke(
                          width = 16.dp.toPx(),
                          cap = StrokeCap.Round,
                          join = StrokeJoin.Round,
                        ),
                    )
                  }
                }
                tracePaths.forEach(drawStroke)
                drawStroke(currentStroke)
              }
            }
          }
        }

        // 3. Mini Sound Quiz & Star Reward
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text =
                  if (languageMode == LanguageMode.HINGLISH) {
                    "Quiz: Kaunsa letter '${letter.phonemeSpeech}' bolta hai?"
                  } else {
                    "Mini Quiz: Which letter says '${letter.phonemeSpeech}'?"
                  },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
              )
              IconButton(
                onClick = { audioPlayer.speakPhonemeOnly(letter) },
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                  contentDescription = "Repeat quiz sound",
                  tint = CoralPrimary,
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              quizOptions.forEach { option ->
                val isSelected = selectedQuizLetter == option.letter
                val isCorrect = option.letter == letter.letter
                val bgColor =
                  when {
                    isSelected && isCorrect -> MintGreen
                    isSelected && !isCorrect -> CoralPrimary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                  }
                val fgColor =
                  when {
                    isSelected && isCorrect -> InkNavy
                    isSelected && !isCorrect -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                  }

                Surface(
                  onClick = {
                    if (!quizCompleted) {
                      selectedQuizLetter = option.letter
                      quizAttempts++
                      if (isCorrect) {
                        quizCompleted = true
                        val stars = if (quizAttempts <= 1) 3 else 2
                        val score = if (quizAttempts <= 1) 100 else 80
                        audioPlayer.playCelebrationChime()
                        onCompleteLesson(stars, score, true)
                      } else {
                        audioPlayer.playGentleTryAgain()
                        audioPlayer.speakPhonemeOnly(letter)
                      }
                    }
                  },
                  color = bgColor,
                  shape = RoundedCornerShape(20.dp),
                  modifier =
                    Modifier
                      .weight(1f)
                      .height(82.dp)
                      .testTag("quiz_option_${option.letter}"),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = option.letter,
                      fontSize = 36.sp,
                      style = MaterialTheme.typography.displayMedium,
                      color = fgColor,
                    )
                  }
                }
              }
            }

            AnimatedVisibility(visible = quizCompleted) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MintGreen,
                  )
                  Text(
                    text =
                      if (languageMode == LanguageMode.HINGLISH) "Shabaash! You earned stars!"
                      else "Awesome job! Stars saved!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  val earned = if (quizAttempts <= 1) 3 else 2
                  repeat(earned) {
                    Icon(
                      imageVector = Icons.Rounded.Star,
                      contentDescription = null,
                      tint = GoldStar,
                      modifier = Modifier.size(30.dp),
                    )
                  }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = onBack,
                  shape = RoundedCornerShape(16.dp),
                  colors =
                    ButtonDefaults.buttonColors(
                      containerColor = SunshineAmber,
                      contentColor = InkNavy,
                    ),
                  modifier = Modifier.fillMaxWidth().height(50.dp).testTag("lesson_done_button"),
                ) {
                  Text("Back to Learning Path", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
