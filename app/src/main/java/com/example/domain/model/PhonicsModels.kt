package com.example.domain.model

enum class LearningMode(val storageValue: String, val label: String) {
  SATPIN("SATPIN", "SATPIN Path"),
  AZ("AZ", "A to Z");

  companion object {
    fun fromStorage(value: String?): LearningMode =
      entries.firstOrNull { it.storageValue == value } ?: SATPIN
  }
}

enum class LanguageMode(val storageValue: String, val label: String) {
  HINGLISH("HINGLISH", "Hinglish"),
  ENGLISH("ENGLISH", "English");

  companion object {
    fun fromStorage(value: String?): LanguageMode =
      entries.firstOrNull { it.storageValue == value } ?: HINGLISH
  }
}

enum class SatpinGroup(
  val groupNumber: Int,
  val titleEn: String,
  val titleHinglish: String,
  val subtitle: String,
) {
  GROUP_1(
    groupNumber = 1,
    titleEn = "Group 1 · Foundation Sounds",
    titleHinglish = "Group 1 · Pehli Awaaz (S A T P I N)",
    subtitle = "Blend 20+ words right away: sat, tap, pin, ant!",
  ),
  GROUP_2(
    groupNumber = 2,
    titleEn = "Group 2 · Action Consonants",
    titleHinglish = "Group 2 · Naye Shabd (C K E H R M D)",
    subtitle = "Unlock cat, red, mat, hen, and kid!",
  ),
  GROUP_3(
    groupNumber = 3,
    titleEn = "Group 3 · Story Builders",
    titleHinglish = "Group 3 · Kahani Wale Akshar (G O U L F B)",
    subtitle = "Read dog, sun, bus, frog, and log!",
  ),
  GROUP_4(
    groupNumber = 4,
    titleEn = "Group 4 · Master Explorers",
    titleHinglish = "Group 4 · Champion Sounds (J Z W V Y X Q)",
    subtitle = "Complete the full 26-sound phonics universe!",
  ),
}

enum class TilePalette(val index: Int) {
  CORAL(0),
  SUNSHINE(1),
  SKY_TEAL(2),
  MINT(3),
  PLUM(4),
  TANGERINE(5),
}

data class PhonicsLetter(
  val letter: String,
  val lowercase: String,
  val satpinOrder: Int,
  val azOrder: Int,
  val group: SatpinGroup,
  val phonemeSymbol: String,
  val phonemeSpeech: String,
  val exampleWord: String,
  val exampleEmoji: String,
  val hindiWord: String,
  val hindiScriptWord: String,
  val hinglishCue: String,
  val englishCue: String,
  val hinglishAudioScript: String,
  val englishAudioScript: String,
  val sampleWords: List<String>,
  val palette: TilePalette,
)

data class CvcWord(
  val word: String,
  val phonemes: List<String>,
  val emoji: String,
  val hinglishMeaning: String,
  val groupNumber: Int,
)
