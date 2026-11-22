package com.example.feature.home

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.timer.SessionTimerUiState
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearningMode
import com.example.domain.model.PhonicsLetter
import com.example.domain.model.SatpinGroup
import com.example.feature.stages.CourseRoadmapSection
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.SunshineAmber

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
  onOpenSoundboard: () -> Unit = {},
  onOpenSightWords: () -> Unit,
  onOpenUkgStage: () -> Unit,
  onOpenClass1Stage: () -> Unit,
  onOpenClass2Stage: () -> Unit,
  onOpenClass3Stage: () -> Unit = {},
  onOpenClass4Stage: () -> Unit = {},
  onOpenClass5Stage: () -> Unit = {},
  onOpenRewards: () -> Unit = {},
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
          .statusBarsPadding(),
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
          val allSatpinLetters = remember { PhonicsCurriculum.lettersForMode(LearningMode.SATPIN) }

          fun practiceGroup(group: SatpinGroup) {
            val groupLetters = allSatpinLetters.filter { it.group == group }
            val unlockedInGroup =
              groupLetters.filter { uiState.freeExplore || it.satpinOrder <= maxUnlockedOrder }
            val target =
              unlockedInGroup.firstOrNull { uiState.progressByLetter[it.letter]?.mastered != true }
                ?: unlockedInGroup.firstOrNull()
            if (target != null) {
              onLetterClick(target)
            } else {
              groupLetters.firstOrNull()?.let { onSpeakLetter(it, uiState.languageMode) }
            }
          }

          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
          ) {
            LazyVerticalGrid(
              columns = GridCells.Adaptive(minSize = 152.dp),
              contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
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
                  unlockedBadgeCount = uiState.unlockedBadgeCount,
                  totalBadgeCount = uiState.badgeProgress.size,
                  languageMode = uiState.languageMode,
                  onEditNameClick = { showEditNameDialog = true },
                  onToggleLanguage = onToggleLanguage,
                  onOpenRewards = onOpenRewards,
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
                  onOpenSoundboard = onOpenSoundboard,
                )
              }

              // 2b. Visual SATPIN Sequence Stage Cards
              item(span = { GridItemSpan(maxLineSpan) }) {
                SatpinJourneyOverviewStrip(
                  allSatpinLetters = allSatpinLetters,
                  progressByLetter = uiState.progressByLetter,
                  selectedGroup = uiState.selectedGroup,
                  freeExplore = uiState.freeExplore,
                  maxUnlockedOrder = maxUnlockedOrder,
                  languageMode = uiState.languageMode,
                  onPracticeGroup = ::practiceGroup,
                )
              }

              // 2c. LKG to Class 5 Course Path
              item(span = { GridItemSpan(maxLineSpan) }) {
                CourseRoadmapSection(
                  languageMode = uiState.languageMode,
                  masteredCount = uiState.masteredCount,
                  stageProgressById = uiState.stageProgressById,
                  onOpenSoundboard = onOpenSoundboard,
                  onOpenSightWords = onOpenSightWords,
                  onOpenWordBlender = onOpenWordBlender,
                  onOpenUkgStage = onOpenUkgStage,
                  onOpenClass1Stage = onOpenClass1Stage,
                  onOpenClass2Stage = onOpenClass2Stage,
                  onOpenClass3Stage = onOpenClass3Stage,
                  onOpenClass4Stage = onOpenClass4Stage,
                  onOpenClass5Stage = onOpenClass5Stage,
                )
              }

              // 2d. Digital Badges & Next Reward Strip
              item(span = { GridItemSpan(maxLineSpan) }) {
                NextBadgeGoalStrip(
                  learnerRank = uiState.learnerRank,
                  badgeProgress = uiState.badgeProgress,
                  unlockedCount = uiState.unlockedBadgeCount,
                  languageMode = uiState.languageMode,
                  onOpenRewards = onOpenRewards,
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

              // 4. Visual SATPIN Winding Path OR A-Z Grid
              if (uiState.learningMode == LearningMode.SATPIN) {
                val grouped = uiState.letters.groupBy { it.group }
                val visibleGroups =
                  if (uiState.selectedGroup == null) SatpinGroup.entries
                  else SatpinGroup.entries.filter { it.groupNumber == uiState.selectedGroup }

                visibleGroups.forEach { group ->
                  val groupLetters = grouped[group].orEmpty()
                  if (groupLetters.isNotEmpty()) {
                    item(
                      key = "group_card_${group.groupNumber}",
                      span = { GridItemSpan(maxLineSpan) },
                    ) {
                      SatpinGroupPathCard(
                        group = group,
                        groupLetters = groupLetters,
                        progressByLetter = uiState.progressByLetter,
                        languageMode = uiState.languageMode,
                        maxUnlockedOrder = maxUnlockedOrder,
                        freeExplore = uiState.freeExplore,
                        onPracticeGroup = { practiceGroup(group) },
                        onSelectLetter = { letter ->
                          val open = uiState.freeExplore || letter.satpinOrder <= maxUnlockedOrder
                          if (open) onLetterClick(letter)
                          else onSpeakLetter(letter, uiState.languageMode)
                        },
                      )
                    }

                    itemsIndexed(
                      items = groupLetters,
                      key = { _, letter -> letter.letter },
                      span = { _, _ -> GridItemSpan(maxLineSpan) },
                    ) { idxInGroup, letter ->
                      val isUnlocked = uiState.freeExplore || letter.satpinOrder <= maxUnlockedOrder
                      val isNextUp = letter.letter == uiState.nextRecommendedLetter.letter
                      val prevMastered =
                        idxInGroup > 0 &&
                          uiState.progressByLetter[groupLetters[idxInGroup - 1].letter]?.mastered == true

                      SatpinPathStepRow(
                        letter = letter,
                        indexInGroup = idxInGroup,
                        showConnectorAbove = idxInGroup > 0,
                        prevLetterMastered = prevMastered,
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

                  PhonicsLetterTile(
                    letter = letter,
                    orderNumber = letter.azOrder,
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
