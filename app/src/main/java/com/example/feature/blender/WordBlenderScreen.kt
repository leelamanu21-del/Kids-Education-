package com.example.feature.blender

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
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
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.core.ui.HinglishMicroTipCard
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.CoralShadow
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.TangerineOrange
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class DraggableTile(
  val id: Int,
  val letter: String,
)

private data class ConfettiParticle(
  val angleRad: Float,
  val speed: Float,
  val color: Color,
  val radius: Float,
)

@Composable
fun WordBlenderScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onWordBlended: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  val words = PhonicsCurriculum.cvcWords
  var selectedIndex by remember { mutableIntStateOf(0) }
  val currentWord = words[selectedIndex]
  val scope = rememberCoroutineScope()

  // Slots hold the placed DraggableTile ID (or null if empty)
  val slots = remember(selectedIndex) { mutableStateListOf<DraggableTile?>(null, null, null) }
  var highlightedPhonemeIdx by remember(selectedIndex) { mutableIntStateOf(-1) }
  var activeTipLetter by remember(selectedIndex) {
    mutableStateOf(PhonicsCurriculum.findByLetter(currentWord.phonemes.first()))
  }
  var roundSuccess by remember(selectedIndex) { mutableStateOf(false) }
  var roundError by remember(selectedIndex) { mutableStateOf(false) }
  var scoreCount by remember { mutableIntStateOf(0) }

  // Build scrambled tile tray (3 target letters + 2 distractors)
  val trayTiles =
    remember(selectedIndex) {
      val distractors =
        listOf("M", "P", "S", "R", "B", "L", "T")
          .filter { it !in currentWord.phonemes }
          .take(2)
      val allLetters = (currentWord.phonemes + distractors).shuffled(Random(selectedIndex + 42))
      allLetters.mapIndexed { idx, ch -> DraggableTile(id = idx, letter = ch) }
    }

  val slotBounds = remember(selectedIndex) { mutableStateMapOf<Int, Rect>() }
  val shakeOffset = remember { Animatable(0f) }
  val confettiProgress = remember { Animatable(0f) }
  val confettiParticles =
    remember(selectedIndex) {
      val colors = listOf(CoralPrimary, SunshineAmber, MintGreen, SkyTeal, PlumPurple, TangerineOrange)
      List(28) { i ->
        ConfettiParticle(
          angleRad = (i * (360f / 28f)) * (Math.PI.toFloat() / 180f),
          speed = 110f + (i % 5) * 45f,
          color = colors[i % colors.size],
          radius = 6f + (i % 3) * 3f,
        )
      }
    }

  fun evaluateWordIfReady() {
    if (slots.any { it == null }) return
    val formed = slots.joinToString("") { it?.letter.orEmpty() }
    if (formed == currentWord.word) {
      if (!roundSuccess) {
        roundSuccess = true
        roundError = false
        scoreCount++
        onWordBlended()
        scope.launch {
          confettiProgress.snapTo(0f)
          confettiProgress.animateTo(1f, tween(durationMillis = 900, easing = LinearEasing))
        }
        scope.launch {
          currentWord.phonemes.forEachIndexed { idx, ch ->
            highlightedPhonemeIdx = idx
            PhonicsCurriculum.findByLetter(ch)?.let { audioPlayer.speakPhonemeOnly(it) }
            delay(500L)
          }
          highlightedPhonemeIdx = -1
          audioPlayer.playCelebrationChime()
          audioPlayer.speakWord(currentWord.word, languageMode)
        }
      }
    } else {
      roundError = true
      audioPlayer.playGentleTryAgain()
      scope.launch {
        repeat(3) {
          shakeOffset.animateTo(16f, tween(55))
          shakeOffset.animateTo(-16f, tween(55))
        }
        shakeOffset.animateTo(0f, tween(50))
      }
    }
  }

  fun placeTileInSlot(tile: DraggableTile, targetSlotIdx: Int? = null) {
    if (roundSuccess) return
    val placedIds = slots.mapNotNull { it?.id }
    if (tile.id in placedIds) return

    val idx = targetSlotIdx ?: slots.indexOfFirst { it == null }
    if (idx !in 0..2) return
    if (targetSlotIdx != null && slots[idx] != null) return

    slots[idx] = tile
    roundError = false
    PhonicsCurriculum.findByLetter(tile.letter)?.let {
      activeTipLetter = it
      audioPlayer.speakPhonemeOnly(it)
    }
    evaluateWordIfReady()
  }

  LaunchedEffect(selectedIndex) {
    audioPlayer.speakWord(currentWord.word, languageMode)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        // Top Header + Score
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
                .testTag("blender_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "Back to home",
            )
          }

          Text(
            text = "CVC Word Blending",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )

          Surface(
            color = SunshineAmber.copy(alpha = 0.25f),
            shape = RoundedCornerShape(50),
            modifier = Modifier.border(1.5.dp, SunshineAmber, RoundedCornerShape(50)),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = GoldStar,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$scoreCount",
                style = MaterialTheme.typography.titleMedium,
                color = InkNavy,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }

        // Word Selector Strip
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 2.dp),
        ) {
          itemsIndexed(words) { idx, word ->
            FilterChip(
              selected = idx == selectedIndex,
              onClick = { selectedIndex = idx },
              label = { Text("${word.emoji} ${word.word}") },
              modifier = Modifier.minimumInteractiveComponentSize(),
            )
          }
        }

        // Main Interactive Blending Board + Confetti Overlay
        Card(
          shape = RoundedCornerShape(30.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Box(modifier = Modifier.fillMaxWidth()) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Text(text = currentWord.emoji, fontSize = 62.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Text(
                  text =
                    if (languageMode == LanguageMode.HINGLISH) currentWord.hinglishMeaning
                    else "Build: ${currentWord.word}",
                  style = MaterialTheme.typography.titleLarge,
                  color = SkyTeal,
                )
                IconButton(
                  onClick = { audioPlayer.speakWord(currentWord.word, languageMode) },
                  modifier =
                    Modifier
                      .minimumInteractiveComponentSize()
                      .clip(CircleShape)
                      .background(SkyTeal.copy(alpha = 0.12f))
                      .testTag("hear_target_word_button"),
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = "Hear target word",
                    tint = SkyTeal,
                  )
                }
              }

              Text(
                text = "Drag or tap letters below into the 3 boxes to blend the word!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
              )

              Spacer(modifier = Modifier.height(18.dp))

              // 3 Target Blending Slots
              val slotColors = listOf(CoralPrimary, SunshineAmber, MintGreen)
              Row(
                modifier =
                  Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                for (slotIdx in 0..2) {
                  val placedTile = slots[slotIdx]
                  val expectedLetter = currentWord.phonemes[slotIdx]
                  val isHighlighted = highlightedPhonemeIdx == slotIdx
                  val isSlotCorrect = roundSuccess || (placedTile?.letter == expectedLetter && roundError)
                  val borderColor =
                    when {
                      roundSuccess -> MintGreen
                      roundError && placedTile != null && placedTile.letter != expectedLetter -> CoralPrimary
                      isHighlighted -> slotColors[slotIdx]
                      placedTile != null -> slotColors[slotIdx]
                      else -> MaterialTheme.colorScheme.outline
                    }
                  val bgColor by
                    animateColorAsState(
                      targetValue =
                        when {
                          isHighlighted -> slotColors[slotIdx]
                          roundSuccess -> MintGreen.copy(alpha = 0.2f)
                          placedTile != null -> slotColors[slotIdx].copy(alpha = 0.16f)
                          else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                      label = "slotBg_$slotIdx",
                    )

                  Box(
                    modifier =
                      Modifier
                        .weight(1f)
                        .height(98.dp)
                        .onGloballyPositioned { coords ->
                          slotBounds[slotIdx] = coords.boundsInRoot()
                        }
                        .clip(RoundedCornerShape(22.dp))
                        .background(bgColor)
                        .border(2.5.dp, borderColor, RoundedCornerShape(22.dp))
                        .clickable {
                          if (placedTile != null && !roundSuccess) {
                            slots[slotIdx] = null
                            roundError = false
                          }
                        }
                        .testTag("blend_slot_$slotIdx"),
                    contentAlignment = Alignment.Center,
                  ) {
                    if (placedTile != null) {
                      Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                          text = placedTile.letter,
                          fontSize = 36.sp,
                          style = MaterialTheme.typography.displayMedium,
                          color = if (isHighlighted) Color.White else MaterialTheme.colorScheme.onSurface,
                        )
                        PhonicsCurriculum.findByLetter(placedTile.letter)?.let { ph ->
                          Text(
                            text = ph.phonemeSymbol,
                            style = MaterialTheme.typography.labelMedium,
                            color =
                              if (isHighlighted) Color.White.copy(alpha = 0.9f)
                              else MaterialTheme.colorScheme.onSurfaceVariant,
                          )
                        }
                      }
                    } else {
                      Text(
                        text = "${slotIdx + 1}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              if (roundError) {
                Text(
                  text = "Almost! Tap a wrong letter box to swap it and try again.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = CoralPrimary,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(bottom = 8.dp),
                )
              }

              // Draggable & Tappable Letter Tile Tray
              val placedIds = slots.mapNotNull { it?.id }.toSet()
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
              ) {
                trayTiles.forEach { tile ->
                  val isUsed = tile.id in placedIds
                  var dragOffset by remember(selectedIndex, tile.id) { mutableStateOf(Offset.Zero) }
                  var tileOrigin by remember(selectedIndex, tile.id) { mutableStateOf(Offset.Zero) }
                  val tileScale by
                    animateFloatAsState(
                      targetValue = if (isUsed) 0.88f else 1f,
                      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                      label = "trayTileScale",
                    )

                  Box(
                    modifier =
                      Modifier
                        .weight(1f)
                        .height(72.dp)
                        .scale(tileScale)
                        .offset {
                          IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                        }
                        .onGloballyPositioned { coords ->
                          val b = coords.boundsInRoot()
                          tileOrigin = Offset(b.center.x, b.center.y)
                        }
                        .pointerInput(selectedIndex, tile.id, isUsed, roundSuccess) {
                          if (!isUsed && !roundSuccess) {
                            detectDragGestures(
                              onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount
                              },
                              onDragEnd = {
                                val dropPoint = tileOrigin + dragOffset
                                val matchedSlot =
                                  slotBounds.entries.firstOrNull { (_, rect) ->
                                    rect.contains(dropPoint)
                                  }?.key
                                if (matchedSlot != null && slots[matchedSlot] == null) {
                                  placeTileInSlot(tile, matchedSlot)
                                }
                                dragOffset = Offset.Zero
                              },
                              onDragCancel = { dragOffset = Offset.Zero },
                            )
                          }
                        }
                        .testTag("tray_tile_${tile.letter}_${tile.id}"),
                    contentAlignment = Alignment.Center,
                  ) {
                    if (!isUsed) {
                      Box(
                        modifier =
                          Modifier
                            .fillMaxSize()
                            .offset(y = 4.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(CoralShadow)
                      )
                      Surface(
                        onClick = { placeTileInSlot(tile) },
                        color = CoralPrimary,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxSize(),
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(
                            text = tile.letter,
                            fontSize = 28.sp,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                          )
                        }
                      }
                    } else {
                      Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxSize(),
                      ) {}
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              AnimatedVisibility(visible = roundSuccess) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.fillMaxWidth().testTag("cvc_success_banner"),
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
                        if (languageMode == LanguageMode.HINGLISH) {
                          "Wah! ${currentWord.word} bilkul sahi banaya!"
                        } else {
                          "Awesome! You blended ${currentWord.word}!"
                        },
                      style = MaterialTheme.typography.titleMedium,
                      color = InkNavy,
                    )
                  }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                  ) {
                    OutlinedButton(
                      onClick = {
                        scope.launch {
                          currentWord.phonemes.forEachIndexed { idx, ch ->
                            highlightedPhonemeIdx = idx
                            PhonicsCurriculum.findByLetter(ch)?.let { audioPlayer.speakPhonemeOnly(it) }
                            delay(500L)
                          }
                          highlightedPhonemeIdx = -1
                          audioPlayer.speakWord(currentWord.word, languageMode)
                        }
                      },
                      shape = RoundedCornerShape(16.dp),
                      modifier = Modifier.weight(1f).height(50.dp),
                    ) {
                      Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Replay")
                    }
                    Button(
                      onClick = { selectedIndex = (selectedIndex + 1) % words.size },
                      shape = RoundedCornerShape(16.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = PlumPurple),
                      modifier = Modifier.weight(1f).height(50.dp).testTag("next_cvc_word_button"),
                    ) {
                      Text("Next Word")
                      Spacer(modifier = Modifier.width(6.dp))
                      Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }

              if (!roundSuccess) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  OutlinedButton(
                    onClick = {
                      for (i in 0..2) slots[i] = null
                      roundError = false
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(50.dp),
                  ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset")
                  }
                  Button(
                    onClick = {
                      scope.launch {
                        currentWord.phonemes.forEachIndexed { idx, ch ->
                          highlightedPhonemeIdx = idx
                          PhonicsCurriculum.findByLetter(ch)?.let { audioPlayer.speakPhonemeOnly(it) }
                          delay(520L)
                        }
                        highlightedPhonemeIdx = -1
                        audioPlayer.speakWord(currentWord.word, languageMode)
                      }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors =
                      ButtonDefaults.buttonColors(
                        containerColor = SunshineAmber,
                        contentColor = InkNavy,
                      ),
                    modifier = Modifier.weight(1f).height(50.dp).testTag("blend_word_button"),
                  ) {
                    Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sound Hint", fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            // Confetti Burst Canvas Overlay
            if (confettiProgress.value > 0f && confettiProgress.value < 1f) {
              Canvas(modifier = Modifier.matchParentSize()) {
                val origin = Offset(size.width / 2f, size.height * 0.42f)
                val p = confettiProgress.value
                confettiParticles.forEach { particle ->
                  val distance = particle.speed * p * 2.2f
                  val x = origin.x + cos(particle.angleRad) * distance
                  val y = origin.y + sin(particle.angleRad) * distance + (p * p * 90f)
                  drawCircle(
                    color = particle.color.copy(alpha = (1f - p).coerceIn(0f, 1f)),
                    radius = particle.radius,
                    center = Offset(x, y),
                  )
                }
              }
            }
          }
        }

        // Reusable Hinglish Micro-Tip & Audio Hint Card
        activeTipLetter?.let { tipLetter ->
          HinglishMicroTipCard(
            letter = tipLetter,
            languageMode = languageMode,
            audioPlayer = audioPlayer,
            compact = true,
          )
        }
      }
    }
  }
}
