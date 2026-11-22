package com.example.feature.soundboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.core.ui.LanguageScriptBadge
import com.example.domain.curriculum.SoundboardCatalog
import com.example.domain.curriculum.SoundboardCategory
import com.example.domain.curriculum.SoundboardExampleWord
import com.example.domain.curriculum.SoundboardTile
import com.example.domain.model.LanguageMode
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.toColorSet

@Composable
fun SoundboardScreen(
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onToggleLanguage: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedCategory by remember { mutableStateOf(SoundboardCategory.ALL) }
  var tapFullExample by remember { mutableStateOf(false) }
  var selectedTile by remember {
    mutableStateOf(SoundboardCatalog.allTiles.first { it.id == "digraph_sh" })
  }

  val visibleTiles =
    remember(selectedCategory) {
      SoundboardCatalog.tilesFor(selectedCategory)
    }

  val playPureSound: (SoundboardTile) -> Unit = { tile ->
    if (tile.letterSource != null) {
      audioPlayer.speakPhonemeOnly(tile.letterSource, languageMode)
    } else {
      audioPlayer.speakSoundToken(tile.spokenToken, tile.hindiSound, languageMode)
    }
  }

  val playFullExample: (SoundboardTile) -> Unit = { tile ->
    if (tile.letterSource != null) {
      audioPlayer.speakLetterPhoneme(tile.letterSource, languageMode)
    } else {
      audioPlayer.speakBilingualScript(
        hindiDevanagariScript = tile.hindiVoiceScript,
        englishScript = tile.englishVoiceScript,
        languageMode = languageMode,
      )
    }
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
      BoxWithConstraints(
        modifier = Modifier.fillMaxSize().widthIn(max = 860.dp),
      ) {
        val pinSpotlight = maxHeight >= 560.dp

        Column(modifier = Modifier.fillMaxSize()) {
          // Header
          SoundboardHeader(
            languageMode = languageMode,
            tapFullExample = tapFullExample,
            onToggleTapMode = { tapFullExample = !tapFullExample },
            onToggleLanguage = onToggleLanguage,
            onBack = onBack,
          )

          if (pinSpotlight) {
            SoundboardSpotlightStage(
              tile = selectedTile,
              languageMode = languageMode,
              onHearSound = { playPureSound(selectedTile) },
              onHearExample = { playFullExample(selectedTile) },
              onTapExampleWord = { example -> audioPlayer.speakWord(example.word, languageMode) },
              modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          CategoryFilterRow(
            selectedCategory = selectedCategory,
            languageMode = languageMode,
            onSelectCategory = { cat ->
              selectedCategory = cat
              val nextList = SoundboardCatalog.tilesFor(cat)
              if (nextList.isNotEmpty() && selectedTile !in nextList) {
                selectedTile = nextList.first()
              }
            },
          )

          LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier =
              Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("soundboard_grid"),
          ) {
            if (!pinSpotlight) {
              item(span = { GridItemSpan(maxLineSpan) }) {
                SoundboardSpotlightStage(
                  tile = selectedTile,
                  languageMode = languageMode,
                  onHearSound = { playPureSound(selectedTile) },
                  onHearExample = { playFullExample(selectedTile) },
                  onTapExampleWord = { example -> audioPlayer.speakWord(example.word, languageMode) },
                )
              }
            }

            items(items = visibleTiles, key = { it.id }) { tile ->
              SoundboardTileCard(
                tile = tile,
                isSelected = tile.id == selectedTile.id,
                onClick = {
                  selectedTile = tile
                  if (tapFullExample) {
                    playFullExample(tile)
                  } else {
                    playPureSound(tile)
                  }
                },
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SoundboardHeader(
  languageMode: LanguageMode,
  tapFullExample: Boolean,
  onToggleTapMode: () -> Unit,
  onToggleLanguage: () -> Unit,
  onBack: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f),
    ) {
      IconButton(
        onClick = onBack,
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .testTag("soundboard_back_button"),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
          contentDescription = "Back to home",
        )
      }
      Column {
        Text(
          text = if (languageMode == LanguageMode.HINGLISH) "Awaaz Soundboard" else "Phonics Soundboard",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) "Akshar ya digraph dabakar aawaz sunein"
            else "Tap any letter or digraph to hear its sound",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
      Surface(
        onClick = onToggleTapMode,
        color = if (tapFullExample) PlumPurple.copy(alpha = 0.14f) else MintGreen.copy(alpha = 0.2f),
        shape = RoundedCornerShape(50),
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .border(
              1.dp,
              if (tapFullExample) PlumPurple else MintGreen,
              RoundedCornerShape(50),
            )
            .testTag("soundboard_mode_toggle"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.GraphicEq,
            contentDescription = "Toggle pure sound or full example mode",
            tint = if (tapFullExample) PlumPurple else InkNavy,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (tapFullExample) "Tip" else "Pure",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (tapFullExample) PlumPurple else InkNavy,
          )
        }
      }

      Surface(
        onClick = onToggleLanguage,
        color = SkyTeal.copy(alpha = 0.14f),
        shape = RoundedCornerShape(50),
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .border(1.dp, SkyTeal, RoundedCornerShape(50))
            .testTag("soundboard_language_toggle"),
      ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
          LanguageScriptBadge(tint = SkyTeal, fontSize = 13.sp, description = "Switch Hinglish or English audio")
        }
      }
    }
  }
}

@Composable
private fun SoundboardSpotlightStage(
  tile: SoundboardTile,
  languageMode: LanguageMode,
  onHearSound: () -> Unit,
  onHearExample: () -> Unit,
  onTapExampleWord: (SoundboardExampleWord) -> Unit,
  modifier: Modifier = Modifier,
) {
  AnimatedContent(
    targetState = tile,
    label = "soundboardSpotlight",
    modifier = modifier.fillMaxWidth(),
  ) { activeTile ->
    val colors = activeTile.palette.toColorSet()

    Card(
      shape = RoundedCornerShape(26.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth().testTag("soundboard_spotlight"),
    ) {
      Column(
        modifier =
          Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(colors.fill, colors.fill.copy(alpha = 0.86f))
              )
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          // Giant Symbol Block
          Surface(
            color = Color.White.copy(alpha = 0.22f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.size(76.dp),
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.fillMaxSize().padding(4.dp),
            ) {
              Text(
                text = activeTile.displaySymbol,
                fontSize = if (activeTile.displaySymbol.length > 3) 20.sp else 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.content,
                maxLines = 1,
              )
              Text(
                text = "${activeTile.hindiSound} · ${activeTile.phonemeBadge}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.content.copy(alpha = 0.92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }

          // Visual Example Hero
          Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(text = activeTile.primaryEmoji, fontSize = 32.sp)
              Column {
                Text(
                  text = activeTile.breakdownHint,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = colors.content,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text =
                    if (languageMode == LanguageMode.HINGLISH) activeTile.cueHinglish
                    else activeTile.cueEnglish,
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.content.copy(alpha = 0.92f),
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }
        }

        // Action Audio Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Button(
            onClick = onHearSound,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = InkNavy),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.weight(1f).height(48.dp).testTag("soundboard_hear_sound"),
          ) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Hear Sound", fontWeight = FontWeight.Bold, maxLines = 1)
          }

          Button(
            onClick = onHearExample,
            colors = ButtonDefaults.buttonColors(containerColor = SunshineAmber, contentColor = InkNavy),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.weight(1f).height(48.dp).testTag("soundboard_hear_example"),
          ) {
            Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Word & Tip", fontWeight = FontWeight.Bold, maxLines = 1)
          }
        }

        // Visual Example Words Strip
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 2.dp),
        ) {
          items(activeTile.examples) { example ->
            Surface(
              onClick = { onTapExampleWord(example) },
              color = Color.White.copy(alpha = 0.24f),
              shape = RoundedCornerShape(50),
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
              ) {
                Text(text = example.emoji, fontSize = 16.sp)
                Text(
                  text = example.word,
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  color = colors.content,
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryFilterRow(
  selectedCategory: SoundboardCategory,
  languageMode: LanguageMode,
  onSelectCategory: (SoundboardCategory) -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(SoundboardCategory.entries) { cat ->
      val count = SoundboardCatalog.tilesFor(cat).size
      val label =
        if (languageMode == LanguageMode.HINGLISH) "${cat.labelHinglish} ($count)"
        else "${cat.labelEn} ($count)"
      FilterChip(
        selected = selectedCategory == cat,
        onClick = { onSelectCategory(cat) },
        label = { Text(label, fontWeight = FontWeight.Bold) },
        colors =
          FilterChipDefaults.filterChipColors(
            selectedContainerColor = PlumPurple,
            selectedLabelColor = Color.White,
          ),
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .testTag("soundboard_filter_${cat.name.lowercase()}"),
      )
    }
  }
}

@Composable
private fun SoundboardTileCard(
  tile: SoundboardTile,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  val colors = tile.palette.toColorSet()
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by
    animateFloatAsState(
      targetValue =
        when {
          isPressed -> 0.93f
          isSelected -> 1.03f
          else -> 1f
        },
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "soundboardTileScale",
    )

  Box(
    modifier =
      Modifier
        .fillMaxWidth()
        .height(106.dp)
        .scale(scale)
        .testTag("soundboard_tile_${tile.id}"),
  ) {
    // 3D Bevel
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .offset(y = 5.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(colors.bevel)
    )

    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(20.dp))
          .background(colors.fill)
          .then(
            if (isSelected) Modifier.border(3.dp, Color.White, RoundedCornerShape(20.dp))
            else Modifier
          )
          .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onClick,
          )
          .padding(8.dp),
    ) {
      // Top mini emoji & Hindi tag
      Row(
        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = tile.primaryEmoji, fontSize = 18.sp)
        Surface(
          color = colors.badge,
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(
            text = tile.hindiSound,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.content,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
          )
        }
      }

      // Main Sound Symbol
      Text(
        text = tile.displaySymbol,
        fontSize = if (tile.displaySymbol.length > 3) 20.sp else 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colors.content,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.align(Alignment.Center).padding(top = 6.dp),
      )

      // Bottom Example Word
      Text(
        text = tile.primaryWord,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = colors.content.copy(alpha = 0.92f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.align(Alignment.BottomCenter),
      )
    }
  }
}
