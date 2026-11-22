package com.example.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasTestTag
import com.example.core.timer.SessionTimerUiState
import com.example.domain.curriculum.BadgeCatalog
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerProfile
import com.example.domain.model.LearningMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.example.ui.theme.PhonicsPathTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HomeLearningPathScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private fun sampleState(
    selectedGroup: Int? = null,
    freeExplore: Boolean = false,
  ): HomeUiState.Success {
    val satpinLetters = PhonicsCurriculum.lettersForMode(LearningMode.SATPIN)
    val visibleLetters =
      if (selectedGroup == null) satpinLetters
      else satpinLetters.filter { it.group.groupNumber == selectedGroup }
    val progress =
      mapOf(
        "S" to LetterProgress(id = "S", userId = "u1", letter = "S", starsEarned = 3, mastered = true),
      )
    val badges = BadgeCatalog.evaluate(totalStars = 3, progressByLetter = progress, stageProgress = emptyMap())
    val unlockedCount = badges.count { it.unlocked }
    return HomeUiState.Success(
      profile =
        LearnerProfile(
          id = "u1",
          userId = "u1",
          childName = "Aarav",
          totalStars = 3L,
          unlockedLevelOrder = 6L,
        ),
      learningMode = LearningMode.SATPIN,
      languageMode = LanguageMode.HINGLISH,
      selectedGroup = selectedGroup,
      freeExplore = freeExplore,
      letters = visibleLetters,
      progressByLetter = progress,
      stageProgressById = emptyMap(),
      badgeProgress = badges,
      unlockedBadgeCount = unlockedCount,
      learnerRank = BadgeCatalog.rankFor(unlockedCount),
      nextRecommendedLetter = satpinLetters[1], // 'A'
      masteredCount = 1,
    )
  }

  @Test
  fun `satpin dashboard renders journey group cards and triggers next unmastered sound on practice`() {
    var clickedLetter: PhonicsLetter? = null
    var spokenLetter: PhonicsLetter? = null

    composeTestRule.setContent {
      PhonicsPathTheme {
        HomeLearningPathScreen(
          uiState = sampleState(),
          timerState = SessionTimerUiState(),
          onSelectMode = {},
          onToggleLanguage = {},
          onSelectGroup = {},
          onToggleFreeExplore = {},
          onUpdateChildName = {},
          onLetterClick = { clickedLetter = it },
          onSpeakLetter = { letter, _ -> spokenLetter = letter },
          onOpenWordBlender = {},
          onOpenSightWords = {},
          onOpenUkgStage = {},
          onOpenClass1Stage = {},
          onOpenClass2Stage = {},
          onOpenParentZone = {},
          onSignOut = {},
        )
      }
    }

    composeTestRule.waitForIdle()

    // Journey overview stage 1 card is visible & clickable -> opens next unmastered in Group 1 ('A')
    composeTestRule
      .onNodeWithTag("learning_path_grid")
      .performScrollToNode(hasTestTag("satpin_journey_card_1"))
    composeTestRule.onNodeWithTag("satpin_journey_card_1").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    assertEquals("A", clickedLetter?.letter)

    // Group 1 Path Card & Practice button
    clickedLetter = null
    composeTestRule
      .onNodeWithTag("learning_path_grid")
      .performScrollToNode(hasTestTag("satpin_group_card_1"))
    composeTestRule.onNodeWithTag("satpin_group_card_1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("practice_group_1").performClick()
    composeTestRule.waitForIdle()
    assertEquals("A", clickedLetter?.letter)

    // Stepping-stone card 'S' and speaker button
    composeTestRule
      .onNodeWithTag("learning_path_grid")
      .performScrollToNode(hasTestTag("letter_tile_S"))
    composeTestRule.onNodeWithTag("letter_tile_S").assertIsDisplayed()
    composeTestRule.onNodeWithTag("speak_letter_S").performClick()
    composeTestRule.waitForIdle()
    assertEquals("S", spokenLetter?.letter)
  }

  @Test
  fun `tapping locked group 2 path card speaks sound preview instead of opening lesson`() {
    var clickedLetter: PhonicsLetter? = null
    var spokenLetter: PhonicsLetter? = null

    composeTestRule.setContent {
      PhonicsPathTheme {
        HomeLearningPathScreen(
          uiState = sampleState(selectedGroup = 2, freeExplore = false),
          timerState = SessionTimerUiState(),
          onSelectMode = {},
          onToggleLanguage = {},
          onSelectGroup = {},
          onToggleFreeExplore = {},
          onUpdateChildName = {},
          onLetterClick = { clickedLetter = it },
          onSpeakLetter = { letter, _ -> spokenLetter = letter },
          onOpenWordBlender = {},
          onOpenSightWords = {},
          onOpenUkgStage = {},
          onOpenClass1Stage = {},
          onOpenClass2Stage = {},
          onOpenParentZone = {},
          onSignOut = {},
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule
      .onNodeWithTag("learning_path_grid")
      .performScrollToNode(hasTestTag("practice_group_2"))
    composeTestRule.onNodeWithTag("practice_group_2").performClick()
    composeTestRule.waitForIdle()

    assertEquals(null, clickedLetter)
    assertEquals("C", spokenLetter?.letter)
  }
}
