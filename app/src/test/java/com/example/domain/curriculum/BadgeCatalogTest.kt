package com.example.domain.curriculum

import com.example.domain.model.LetterProgress
import com.example.domain.model.RewardStageIds
import com.example.domain.model.StageProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeCatalogTest {

  @Test
  fun emptyProgress_unlocksZeroBadges_andStarterRank() {
    val result =
      BadgeCatalog.evaluate(
        totalStars = 0L,
        progressByLetter = emptyMap(),
        stageProgress = emptyMap(),
      )
    assertEquals(16, result.size)
    assertTrue(result.none { it.unlocked })
    assertEquals("Budding Listener", BadgeCatalog.rankFor(0).titleEn)
  }

  @Test
  fun masteringGroup1AndStages_unlocksCorrespondingBadgesAndRank() {
    val satpinProgress =
      listOf("S", "A", "T", "P", "I", "N").associateWith { letter ->
        LetterProgress(letter = letter, starsEarned = 3L, mastered = true)
      }
    val stageProgress =
      mapOf(
        RewardStageIds.LEVEL_2_BLENDER to
          StageProgress(stageId = RewardStageIds.LEVEL_2_BLENDER, starsEarned = 6L, completionsCount = 3L),
        RewardStageIds.LEVEL_3_UKG to
          StageProgress(stageId = RewardStageIds.LEVEL_3_UKG, starsEarned = 4L, completionsCount = 3L),
      )

    val result =
      BadgeCatalog.evaluate(
        totalStars = 28L,
        progressByLetter = satpinProgress,
        stageProgress = stageProgress,
      ).associateBy { it.badge.id }

    assertTrue(result.getValue("first_sound_star").unlocked)
    assertTrue(result.getValue("satpin_explorer").unlocked)
    assertFalse(result.getValue("action_consonants").unlocked)
    assertTrue(result.getValue("cvc_blender_pro").unlocked)
    assertTrue(result.getValue("ukg_magic_star").unlocked)
    assertTrue(result.getValue("stars_15").unlocked)
    assertFalse(result.getValue("stars_45").unlocked)

    val unlockedCount = result.values.count { it.unlocked }
    assertEquals(5, unlockedCount)
    assertEquals("Word Builder", BadgeCatalog.rankFor(unlockedCount).titleEn)
  }

  @Test
  fun masteringAllLettersAndStages_unlocksLegendRank() {
    val allLetters =
      PhonicsCurriculum.letters.associate { item ->
        item.letter to LetterProgress(letter = item.letter, starsEarned = 3L, mastered = true)
      }
    val allStages =
      RewardStageIds.ALL.associateWith { id ->
        StageProgress(stageId = id, starsEarned = 10L, completionsCount = 5L)
      }

    val result =
      BadgeCatalog.evaluate(
        totalStars = 120L,
        progressByLetter = allLetters,
        stageProgress = allStages,
      )
    val byId = result.associateBy { it.badge.id }
    assertTrue(byId.getValue("class3_vocab_ace").unlocked)
    assertTrue(byId.getValue("class4_word_scholar").unlocked)
    assertTrue(byId.getValue("class5_master_graduate").unlocked)
    assertTrue(result.all { it.unlocked })
    assertEquals("BolBuddy Legend", BadgeCatalog.rankFor(result.size).titleEn)
  }
}
