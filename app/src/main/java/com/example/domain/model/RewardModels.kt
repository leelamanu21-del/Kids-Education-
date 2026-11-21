package com.example.domain.model

object RewardStageIds {
  const val LEVEL_2_BLENDER = "level2_blender"
  const val LEVEL_2_SIGHT_WORDS = "level2_sight_words"
  const val LEVEL_3_UKG = "level3_ukg"
  const val LEVEL_4_CLASS1 = "level4_class1"
  const val LEVEL_5_CLASS2 = "level5_class2"

  val ALL =
    setOf(
      LEVEL_2_BLENDER,
      LEVEL_2_SIGHT_WORDS,
      LEVEL_3_UKG,
      LEVEL_4_CLASS1,
      LEVEL_5_CLASS2,
    )
}

enum class BadgeCategory(val titleEn: String, val titleHinglish: String) {
  PHONICS_PATH("Phonics Path (Level 1)", "Phonics Path (Level 1)"),
  COURSE_STAGES("Course Adventures (Levels 2–5)", "Course Levels (2–5)"),
  STAR_TIERS("Star Milestones", "Star Champion Tiers"),
}

data class DigitalBadge(
  val id: String,
  val emoji: String,
  val titleEn: String,
  val titleHinglish: String,
  val descriptionEn: String,
  val descriptionHinglish: String,
  val category: BadgeCategory,
  val targetProgress: Int,
  val palette: TilePalette,
)

data class BadgeProgress(
  val badge: DigitalBadge,
  val currentProgress: Int,
  val unlocked: Boolean,
) {
  val fraction: Float
    get() =
      if (badge.targetProgress <= 0) 1f
      else (currentProgress.toFloat() / badge.targetProgress.toFloat()).coerceIn(0f, 1f)
}

data class LearnerRank(
  val titleEn: String,
  val titleHinglish: String,
  val emoji: String,
)
