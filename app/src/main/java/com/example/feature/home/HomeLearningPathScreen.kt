package com.example.feature.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.timer.SessionTimerUiState
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearningMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.example.domain.model.SatpinGroup
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.EmptyStar
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SoftSand
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.toColorSet

@Composable
fun HomeLearningPathScreen(
  uiState: HomeUiState,
  timerState: SessionTimerUiState,
  onSelectMode: (LearningMode) -> Unit,
  onToggleLanguage: () -> Unit,
  onSelectGroup: (Int?) -> Unit,
  onToggleFreeExplore: () -> Unit,
  onUpdateChildName: (String) -> Unit,
  onLetterClick: (PhonicsLetter) -> Unit,
  onSpeakLetter: (PhonicsLetter, LanguageMode) -> Unit,
  onOpenWordBlender: () -> Unit,
  onOpenParentZone: () -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showEditNameDialog by remember { mutableStateOf(false) }

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
          .statusBarsPadding()
    ) {
      when (uiState) {
        is HomeUiState.Loading -> {
          Column(
            modifier = Modifier.align(Alignment.Center).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            CircularProgressIndicator(color = CoralPrimary)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Preparing your phonics garden...",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        is HomeUiState.Error -> {
          Column(
            modifier = Modifier.align(Alignment.Center).padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = "Oops! Could not load progress",
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.error,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = uiState.message,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
            )
          }
        }

        is HomeUiState.Success -> {
          val maxUnlockedOrder = maxOf(6, uiState.profile.unlockedLevelOrder.toInt())

          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
          ) {
            LazyVerticalGrid(
              columns = GridCells.Adaptive(minSize = 152.dp),
              contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier =
                Modifier
                  .fillMaxSize()
                  .widthIn(max = 880.dp)
                  .windowInsetsPadding(WindowInsets.navigationBars)
                  .testTag("learning_path_grid"),
            ) {
              // 1. Top Bar Header
              item(span = { GridItemSpan(maxLineSpan) }) {
                TopLearnerHeader(
                  childName = uiState.profile.childName,
                  totalStars = uiState.profile.totalStars,
                  languageMode = uiState.languageMode,
                  onEditNameClick = { showEditNameDialog = true },
                  onToggleLanguage = onToggleLanguage,
                  onOpenParentZone = onOpenParentZone,
                  onSignOut = onSignOut,
                )
              }

              if (timerState.showGentleHeadsUp) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                  Surface(
                    color = SunshineAmber.copy(alpha = 0.24f),
                    shape = RoundedCornerShape(16.dp),
                    modifier =
                      Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, SunshineAmber, RoundedCornerShape(16.dp)),
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                      Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = InkNavy,
                      )
                      Text(
                        text =
                          "Eye-rest break coming up in ${timerState.remainingSeconds}s — finish your sound!",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkNavy,
                      )
                    }
                  }
                }
              }

              // 2. Hero Learning Path Banner
              item(span = { GridItemSpan(maxLineSpan) }) {
                HeroLearningBanner(
                  nextLetter = uiState.nextRecommendedLetter,
                  masteredCount = uiState.masteredCount,
                  languageMode = uiState.languageMode,
                  onContinueClick = { onLetterClick(uiState.nextRecommendedLetter) },
                  onOpenWordBlender = onOpenWordBlender,
                )
              }

              // 3. Sequence Switcher (SATPIN vs A-Z) & Explore All Toggle
              item(span = { GridItemSpan(maxLineSpan) }) {
                SequenceControlSection(
                  learningMode = uiState.learningMode,
                  selectedGroup = uiState.selectedGroup,
                  freeExplore = uiState.freeExplore,
                  languageMode = uiState.languageMode,
                  onSelectMode = onSelectMode,
                  onSelectGroup = onSelectGroup,
                  onToggleFreeExplore = onToggleFreeExplore,
                )
              }

              // 4. Phonics Letter Tiles
              if (uiState.learningMode == LearningMode.SATPIN && uiState.selectedGroup == null) {
                val grouped = uiState.letters.groupBy { it.group }
                SatpinGroup.entries.forEach { group ->
                  val groupLetters = grouped[group].orEmpty()
                  if (groupLetters.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                      GroupMilestoneBanner(
                        group = group,
                        languageMode = uiState.languageMode,
                        masteredInGroup =
                          groupLetters.count { uiState.progressByLetter[it.letter]?.mastered == true },
                        totalInGroup = groupLetters.size,
                      )
                    }
                    items(items = groupLetters, key = { it.letter }) { letter ->
                      val isUnlocked =
                        uiState.freeExplore || letter.satpinOrder <= maxUnlockedOrder
                      val isNextUp = letter.letter == uiState.nextRecommendedLetter.letter
                      PhonicsLetterTile(
                        letter = letter,
                        orderNumber = letter.satpinOrder,
                        languageMode = uiState.languageMode,
                        progress = uiState.progressByLetter[letter.letter],
                        isUnlocked = isUnlocked,
                        isNextUp = isNextUp,
                        onTileClick = {
                          if (isUnlocked) {
                            onLetterClick(letter)
                          } else {
                            onSpeakLetter(letter, uiState.languageMode)
                          }
                        },
                        onSpeakClick = { onSpeakLetter(letter, uiState.languageMode) },
                      )
                    }
                  }
                }
              } else {
                items(items = uiState.letters, key = { it.letter }) { letter ->
                  val isUnlocked =
                    uiState.learningMode == LearningMode.AZ ||
                      uiState.freeExplore ||
                      letter.satpinOrder <= maxUnlockedOrder
                  val isNextUp = letter.letter == uiState.nextRecommendedLetter.letter
                  val orderNum =
                    if (uiState.learningMode == LearningMode.SATPIN) letter.satpinOrder
                    else letter.azOrder

                  PhonicsLetterTile(
                    letter = letter,
                    orderNumber = orderNum,
                    languageMode = uiState.languageMode,
                    progress = uiState.progressByLetter[letter.letter],
                    isUnlocked = isUnlocked,
                    isNextUp = isNextUp,
                    onTileClick = {
                      if (isUnlocked) {
                        onLetterClick(letter)
                      } else {
                        onSpeakLetter(letter, uiState.languageMode)
                      }
                    },
                    onSpeakClick = { onSpeakLetter(letter, uiState.languageMode) },
                  )
                }
              }
            }
          }

          if (showEditNameDialog) {
            EditChildNameDialog(
              currentName = uiState.profile.childName,
              onDismiss = { showEditNameDialog = false },
              onSave = { updated ->
                onUpdateChildName(updated)
                showEditNameDialog = false
              },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TopLearnerHeader(
  childName: String,
  totalStars: Long,
  languageMode: LanguageMode,
  onEditNameClick: () -> Unit,
  onToggleLanguage: () -> Unit,
  onOpenParentZone: () -> Unit,
  onSignOut: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f),
    ) {
      Box(
        modifier =
          Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(SoftSand)
            .border(2.dp, SunshineAmber, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_mascot_welcome),
          contentDescription = "Learner avatar",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (languageMode == LanguageMode.HINGLISH) "Namaste!" else "Welcome back!",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = childName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
          )
          IconButton(
            onClick = onEditNameClick,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("edit_name_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Edit,
              contentDescription = "Edit child name",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      // Total Stars Badge
      Surface(
        color = SunshineAmber.copy(alpha = 0.24f),
        shape = RoundedCornerShape(50),
        modifier = Modifier.border(1.5.dp, SunshineAmber, RoundedCornerShape(50)),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Total stars",
            tint = GoldStar,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = totalStars.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = InkNavy,
            fontWeight = FontWeight.Bold,
          )
        }
      }

      // Hinglish / English Toggle Pill
      Surface(
        onClick = onToggleLanguage,
        color = SkyTeal.copy(alpha = 0.14f),
        shape = RoundedCornerShape(50),
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .border(1.5.dp, SkyTeal, RoundedCornerShape(50))
            .testTag("language_toggle_button"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.Translate,
            contentDescription = "Toggle Hinglish or English",
            tint = SkyTeal,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = languageMode.label,
            style = MaterialTheme.typography.labelLarge,
            color = SkyTeal,
          )
        }
      }

      // Secret Parent Zone Lock Button
      IconButton(
        onClick = onOpenParentZone,
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .background(PlumPurple.copy(alpha = 0.12f))
            .testTag("open_parent_zone_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.AdminPanelSettings,
          contentDescription = "Open Parent Zone",
          tint = PlumPurple,
        )
      }

      IconButton(
        onClick = onSignOut,
        modifier = Modifier.minimumInteractiveComponentSize().testTag("sign_out_button"),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.Logout,
          contentDescription = "Sign out",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun HeroLearningBanner(
  nextLetter: PhonicsLetter,
  masteredCount: Int,
  languageMode: LanguageMode,
  onContinueClick: () -> Unit,
  onOpenWordBlender: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(28.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxWidth()
          .height(206.dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_hero_phonics_park),
        contentDescription = "Phonics adventure park banner",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )

      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0x331E293B),
                  Color(0xBF1E293B),
                )
              )
            )
      )

      // Top right Word Blender CTA pill
      Surface(
        onClick = onOpenWordBlender,
        color = PlumPurple,
        shape = RoundedCornerShape(50),
        shadowElevation = 4.dp,
        modifier =
          Modifier
            .align(Alignment.TopEnd)
            .padding(14.dp)
            .minimumInteractiveComponentSize()
            .testTag("open_word_blender_button"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Word Blender",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
          )
        }
      }

      Column(
        modifier =
          Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .padding(18.dp),
      ) {
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) {
              "Aaj ki Awaaz: Letter ${nextLetter.letter} (${nextLetter.phonemeSymbol})"
            } else {
              "Up Next: Sound ${nextLetter.letter} (${nextLetter.phonemeSymbol})"
            },
          style = MaterialTheme.typography.headlineMedium,
          color = Color.White,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.weight(1f)) {
            LinearProgressIndicator(
              progress = { (masteredCount / 26f).coerceIn(0.04f, 1f) },
              color = SunshineAmber,
              trackColor = Color.White.copy(alpha = 0.3f),
              modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "$masteredCount of 26 sounds mastered",
              style = MaterialTheme.typography.labelMedium,
              color = Color.White.copy(alpha = 0.92f),
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = onContinueClick,
            shape = RoundedCornerShape(18.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = MintGreen,
                contentColor = InkNavy,
              ),
            modifier = Modifier.height(48.dp).testTag("continue_next_letter_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Play ${nextLetter.letter}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SequenceControlSection(
  learningMode: LearningMode,
  selectedGroup: Int?,
  freeExplore: Boolean,
  languageMode: LanguageMode,
  onSelectMode: (LearningMode) -> Unit,
  onSelectGroup: (Int?) -> Unit,
  onToggleFreeExplore: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      // SATPIN vs A-Z Segmented Pill
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.weight(1f),
      ) {
        Row(modifier = Modifier.padding(4.dp)) {
          LearningMode.entries.forEach { mode ->
            val selected = learningMode == mode
            val bg by
              animateColorAsState(
                targetValue = if (selected) CoralPrimary else Color.Transparent,
                label = "modeTabBg",
              )
            val textColor by
              animateColorAsState(
                targetValue =
                  if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "modeTabText",
              )
            Box(
              modifier =
                Modifier
                  .weight(1f)
                  .height(48.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(bg)
                  .clickable { onSelectMode(mode) }
                  .testTag("mode_tab_${mode.storageValue.lowercase()}"),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = mode.label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
              )
            }
          }
        }
      }

      // Free Explore Toggle
      FilterChip(
        selected = freeExplore,
        onClick = onToggleFreeExplore,
        label = {
          Text(
            text = if (freeExplore) "All Open" else "Guided",
            style = MaterialTheme.typography.labelLarge,
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Rounded.Explore,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
        },
        colors =
          FilterChipDefaults.filterChipColors(
            selectedContainerColor = MintGreen.copy(alpha = 0.25f),
            selectedLabelColor = InkNavy,
          ),
        modifier = Modifier.height(48.dp).testTag("free_explore_chip"),
      )
    }

    if (learningMode == LearningMode.SATPIN) {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        item {
          FilterChip(
            selected = selectedGroup == null,
            onClick = { onSelectGroup(null) },
            label = { Text("All 26") },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("group_chip_all"),
          )
        }
        items(SatpinGroup.entries) { group ->
          val chipText =
            when (group) {
              SatpinGroup.GROUP_1 -> "1: SATPIN"
              SatpinGroup.GROUP_2 -> "2: CKEHRMD"
              SatpinGroup.GROUP_3 -> "3: GOULFB"
              SatpinGroup.GROUP_4 -> "4: JZWVYXQ"
            }
          FilterChip(
            selected = selectedGroup == group.groupNumber,
            onClick = { onSelectGroup(group.groupNumber) },
            label = { Text(chipText) },
            modifier =
              Modifier
                .minimumInteractiveComponentSize()
                .testTag("group_chip_${group.groupNumber}"),
          )
        }
      }
      if (selectedGroup != null) {
        val groupObj = SatpinGroup.entries.first { it.groupNumber == selectedGroup }
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) groupObj.titleHinglish
            else groupObj.titleEn,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
      }
    }
  }
}

@Composable
private fun GroupMilestoneBanner(
  group: SatpinGroup,
  languageMode: LanguageMode,
  masteredInGroup: Int,
  totalInGroup: Int,
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(18.dp),
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(top = 6.dp)
        .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) group.titleHinglish else group.titleEn,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = group.subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Surface(
        color = SunshineAmber.copy(alpha = 0.25f),
        shape = RoundedCornerShape(12.dp),
      ) {
        Text(
          text = "$masteredInGroup/$totalInGroup",
          style = MaterialTheme.typography.labelLarge,
          color = InkNavy,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
      }
    }
  }
}

@Composable
private fun PhonicsLetterTile(
  letter: PhonicsLetter,
  orderNumber: Int,
  languageMode: LanguageMode,
  progress: LetterProgress?,
  isUnlocked: Boolean,
  isNextUp: Boolean,
  onTileClick: () -> Unit,
  onSpeakClick: () -> Unit,
) {
  val paletteColors = letter.palette.toColorSet()
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val pressScale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.95f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "tilePressScale",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "nextPulse")
  val pulseScale by
    infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = if (isNextUp) 1.03f else 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 850),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "pulseScale",
    )

  val fillColor = if (isUnlocked) paletteColors.fill else Color(0xFFE6E1D6)
  val bevelColor = if (isUnlocked) paletteColors.bevel else Color(0xFFC7C0B2)
  val contentColor = if (isUnlocked) paletteColors.content else Color(0xFF6B7280)
  val stars = (progress?.starsEarned ?: 0L).toInt().coerceIn(0, 3)

  Box(
    modifier =
      Modifier
        .fillMaxWidth()
        .height(186.dp)
        .scale(pressScale * pulseScale)
        .testTag("letter_tile_${letter.letter}"),
  ) {
    // 3D Tactile Bottom Bevel Shadow
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .offset(y = 6.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(bevelColor)
    )

    // Main Tile Face
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(24.dp))
          .background(fillColor)
          .then(
            if (isNextUp) {
              Modifier.border(3.dp, Color.White, RoundedCornerShape(24.dp))
            } else {
              Modifier
            }
          )
          .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onTileClick,
          )
          .padding(12.dp),
    ) {
      // Top row: Order badge + Next Up / Lock + Quick Sound Button
      Row(
        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Surface(
          color = if (isUnlocked) paletteColors.badge else Color(0xFFD5CFC2),
          shape = RoundedCornerShape(10.dp),
        ) {
          Text(
            text = if (isNextUp) "NEXT #$orderNumber" else "#$orderNumber",
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }

        if (isUnlocked) {
          IconButton(
            onClick = onSpeakClick,
            modifier =
              Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
                .testTag("speak_letter_${letter.letter}"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
              contentDescription = "Hear sound ${letter.letter}",
              tint = contentColor,
              modifier = Modifier.size(22.dp),
            )
          }
        } else {
          Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Rounded.Lock,
              contentDescription = "Locked letter ${letter.letter}",
              tint = contentColor,
              modifier = Modifier.size(20.dp),
            )
          }
        }
      }

      // Center: Chunky Letter + Phoneme + Anchor Word
      Column(
        modifier = Modifier.align(Alignment.Center).padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = letter.letter,
            fontSize = 44.sp,
            style = MaterialTheme.typography.displayLarge,
            color = contentColor,
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = letter.lowercase,
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineLarge,
            color = contentColor.copy(alpha = 0.9f),
            modifier = Modifier.padding(bottom = 4.dp),
          )
        }

        Surface(
          color = Color.Black.copy(alpha = 0.12f),
          shape = RoundedCornerShape(50),
        ) {
          Text(
            text =
              if (languageMode == LanguageMode.HINGLISH) {
                "${letter.exampleEmoji} ${letter.exampleWord} • ${letter.hindiWord}"
              } else {
                "${letter.exampleEmoji} ${letter.phonemeSymbol} ${letter.exampleWord}"
              },
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
      }

      // Bottom: 3 Stars Row
      Row(
        modifier = Modifier.align(Alignment.BottomCenter),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        repeat(3) { index ->
          val earned = index < stars
          Icon(
            imageVector = if (earned) Icons.Rounded.Star else Icons.Rounded.StarOutline,
            contentDescription = null,
            tint = if (earned) GoldStar else contentColor.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun EditChildNameDialog(
  currentName: String,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit,
) {
  var text by remember(currentName) { mutableStateOf(currentName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Child's Name") },
    text = {
      OutlinedTextField(
        value = text,
        onValueChange = { if (it.length <= 40) text = it },
        singleLine = true,
        label = { Text("Little learner's nickname") },
        modifier = Modifier.fillMaxWidth().testTag("child_name_input"),
      )
    },
    confirmButton = {
      Button(
        onClick = { onSave(text) },
        enabled = text.isNotBlank(),
        modifier = Modifier.testTag("save_name_button"),
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  )
}
