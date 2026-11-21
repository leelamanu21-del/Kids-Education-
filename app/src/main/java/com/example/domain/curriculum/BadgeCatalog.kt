package com.example.domain.curriculum

import com.example.domain.model.BadgeCategory
import com.example.domain.model.BadgeProgress
import com.example.domain.model.DigitalBadge
import com.example.domain.model.LearnerRank
import com.example.domain.model.LetterProgress
import com.example.domain.model.RewardStageIds
import com.example.domain.model.SatpinGroup
import com.example.domain.model.StageProgress
import com.example.domain.model.TilePalette

object BadgeCatalog {

  val badges: List<DigitalBadge> =
    listOf(
      DigitalBadge(
        id = "first_sound_star",
        emoji = "🌟",
        titleEn = "First Sound Star",
        titleHinglish = "Pehli Awaaz Star",
        descriptionEn = "Master your very first letter sound in Level 1",
        descriptionHinglish = "Level 1 mein apna pehla akshar master karein",
        category = BadgeCategory.PHONICS_PATH,
        targetProgress = 1,
        palette = TilePalette.SUNSHINE,
      ),
      DigitalBadge(
        id = "satpin_explorer",
        emoji = "🚀",
        titleEn = "SATPIN Explorer",
        titleHinglish = "SATPIN Champion",
        descriptionEn = "Master all 6 Group 1 sounds (S, A, T, P, I, N)",
        descriptionHinglish = "Group 1 ke sabhi 6 akshar (S A T P I N) sikhein",
        category = BadgeCategory.PHONICS_PATH,
        targetProgress = 6,
        palette = TilePalette.CORAL,
      ),
      DigitalBadge(
        id = "action_consonants",
        emoji = "🎨",
        titleEn = "Word Maker",
        titleHinglish = "Shabd Builder",
        descriptionEn = "Master all 7 Group 2 sounds (C, K, E, H, R, M, D)",
        descriptionHinglish = "Group 2 ke sabhi 7 akshar (C K E H R M D) sikhein",
        category = BadgeCategory.PHONICS_PATH,
        targetProgress = 7,
        palette = TilePalette.SKY_TEAL,
      ),
      DigitalBadge(
        id = "story_builders",
        emoji = "🦋",
        titleEn = "Story Builder",
        titleHinglish = "Kahani Builder",
        descriptionEn = "Master all 6 Group 3 sounds (G, O, U, L, F, B)",
        descriptionHinglish = "Group 3 ke sabhi 6 akshar (G O U L F B) sikhein",
        category = BadgeCategory.PHONICS_PATH,
        targetProgress = 6,
        palette = TilePalette.MINT,
      ),
      DigitalBadge(
        id = "alphabet_champion",
        emoji = "👑",
        titleEn = "Alphabet Champion",
        titleHinglish = "26 Akshar Raja",
        descriptionEn = "Master every single one of all 26 phonics sounds!",
        descriptionHinglish = "Poore 26 phonics akshar master karke Raja banein!",
        category = BadgeCategory.PHONICS_PATH,
        targetProgress = 26,
        palette = TilePalette.PLUM,
      ),
      DigitalBadge(
        id = "cvc_blender_pro",
        emoji = "🪄",
        titleEn = "CVC Word Wizard",
        titleHinglish = "Word Blender Master",
        descriptionEn = "Blend 3 CVC words in Level 2 Word Blender",
        descriptionHinglish = "Level 2 Word Blender mein 3 shabd jodein",
        category = BadgeCategory.COURSE_STAGES,
        targetProgress = 3,
        palette = TilePalette.PLUM,
      ),
      DigitalBadge(
        id = "sight_word_spotter",
        emoji = "👀",
        titleEn = "Sight Word Spotter",
        titleHinglish = "Sight Word Guru",
        descriptionEn = "Solve 3 Sight Word quizzes in Level 2",
        descriptionHinglish = "Level 2 mein 3 Sight Word quizzes solve karein",
        category = BadgeCategory.COURSE_STAGES,
        targetProgress = 3,
        palette = TilePalette.SUNSHINE,
      ),
      DigitalBadge(
        id = "ukg_magic_star",
        emoji = "✨",
        titleEn = "Magic 'E' Star",
        titleHinglish = "UKG Magic 'E' Star",
        descriptionEn = "Complete 3 UKG Word Family or Magic 'E' challenges",
        descriptionHinglish = "Level 3 UKG mein 3 Magic 'E' ya Family tasks poore karein",
        category = BadgeCategory.COURSE_STAGES,
        targetProgress = 3,
        palette = TilePalette.SKY_TEAL,
      ),
      DigitalBadge(
        id = "class1_fluency_reader",
        emoji = "📖",
        titleEn = "Fluency Reader",
        titleHinglish = "Class 1 Reader",
        descriptionEn = "Solve 3 Class 1 sentence quizzes in Level 4",
        descriptionHinglish = "Level 4 Class 1 mein 3 vaakya quizzes jeetein",
        category = BadgeCategory.COURSE_STAGES,
        targetProgress = 3,
        palette = TilePalette.TANGERINE,
      ),
      DigitalBadge(
        id = "class2_story_hero",
        emoji = "🦉",
        titleEn = "Story & Grammar Hero",
        titleHinglish = "Class 2 Story Hero",
        descriptionEn = "Complete 3 Grammar, Rhyme, or Story quizzes in Level 5",
        descriptionHinglish = "Level 5 Class 2 mein 3 Grammar ya Story quizzes jeetein",
        category = BadgeCategory.COURSE_STAGES,
        targetProgress = 3,
        palette = TilePalette.MINT,
      ),
      DigitalBadge(
        id = "stars_15",
        emoji = "⭐",
        titleEn = "Rising Star",
        titleHinglish = "Chamakta Sitara",
        descriptionEn = "Collect 15 total stars across any levels",
        descriptionHinglish = "Kisi bhi level se kul 15 stars jama karein",
        category = BadgeCategory.STAR_TIERS,
        targetProgress = 15,
        palette = TilePalette.SUNSHINE,
      ),
      DigitalBadge(
        id = "stars_45",
        emoji = "🏆",
        titleEn = "Super Learner",
        titleHinglish = "Super Champion",
        descriptionEn = "Collect 45 total stars across the course",
        descriptionHinglish = "Poore course se kul 45 stars jeetein",
        category = BadgeCategory.STAR_TIERS,
        targetProgress = 45,
        palette = TilePalette.CORAL,
      ),
      DigitalBadge(
        id = "stars_90",
        emoji = "🌈",
        titleEn = "BolBuddy Legend",
        titleHinglish = "BolBuddy Maha-Star",
        descriptionEn = "Collect 90 total stars to rule Phonics Park!",
        descriptionHinglish = "90 stars kama kar BolBuddy Legend banein!",
        category = BadgeCategory.STAR_TIERS,
        targetProgress = 90,
        palette = TilePalette.PLUM,
      ),
    )

  fun evaluate(
    totalStars: Long,
    progressByLetter: Map<String, LetterProgress>,
    stageProgress: Map<String, StageProgress>,
  ): List<BadgeProgress> {
    val totalMastered = progressByLetter.values.count { it.mastered }
    val group1Mastered = masteredInGroup(SatpinGroup.GROUP_1, progressByLetter)
    val group2Mastered = masteredInGroup(SatpinGroup.GROUP_2, progressByLetter)
    val group3Mastered = masteredInGroup(SatpinGroup.GROUP_3, progressByLetter)
    val starsInt = totalStars.toInt().coerceAtLeast(0)

    return badges.map { badge ->
      val current =
        when (badge.id) {
          "first_sound_star" -> totalMastered
          "satpin_explorer" -> group1Mastered
          "action_consonants" -> group2Mastered
          "story_builders" -> group3Mastered
          "alphabet_champion" -> totalMastered
          "cvc_blender_pro" ->
            (stageProgress[RewardStageIds.LEVEL_2_BLENDER]?.completionsCount ?: 0L).toInt()
          "sight_word_spotter" ->
            (stageProgress[RewardStageIds.LEVEL_2_SIGHT_WORDS]?.completionsCount ?: 0L).toInt()
          "ukg_magic_star" ->
            (stageProgress[RewardStageIds.LEVEL_3_UKG]?.completionsCount ?: 0L).toInt()
          "class1_fluency_reader" ->
            (stageProgress[RewardStageIds.LEVEL_4_CLASS1]?.completionsCount ?: 0L).toInt()
          "class2_story_hero" ->
            (stageProgress[RewardStageIds.LEVEL_5_CLASS2]?.completionsCount ?: 0L).toInt()
          "stars_15", "stars_45", "stars_90" -> starsInt
          else -> 0
        }.coerceIn(0, badge.targetProgress)

      BadgeProgress(
        badge = badge,
        currentProgress = current,
        unlocked = current >= badge.targetProgress,
      )
    }
  }

  fun rankFor(unlockedBadgesCount: Int): LearnerRank =
    when {
      unlockedBadgesCount >= 11 ->
        LearnerRank(
          titleEn = "BolBuddy Legend",
          titleHinglish = "BolBuddy Maha-Champion",
          emoji = "👑",
        )
      unlockedBadgesCount >= 8 ->
        LearnerRank(
          titleEn = "Story Star",
          titleHinglish = "Kahani Sitara",
          emoji = "📚",
        )
      unlockedBadgesCount >= 5 ->
        LearnerRank(
          titleEn = "Word Builder",
          titleHinglish = "Shabd Builder",
          emoji = "🧱",
        )
      unlockedBadgesCount >= 2 ->
        LearnerRank(
          titleEn = "Sound Explorer",
          titleHinglish = "Awaaz Khoji",
          emoji = "🔎",
        )
      else ->
        LearnerRank(
          titleEn = "Budding Listener",
          titleHinglish = "Nanha Sitara",
          emoji = "🌱",
        )
    }

  private fun masteredInGroup(
    group: SatpinGroup,
    progressByLetter: Map<String, LetterProgress>,
  ): Int =
    PhonicsCurriculum.letters
      .filter { it.group == group }
      .count { progressByLetter[it.letter]?.mastered == true }
}
