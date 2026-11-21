package com.example.domain.model

enum class CourseLevelId(
  val levelNumber: Int,
  val gradeTag: String,
  val ageTag: String,
  val titleEnglish: String,
  val titleHinglish: String,
  val subtitleEnglish: String,
  val subtitleHinglish: String,
) {
  LEVEL_1_PHONICS(
    levelNumber = 1,
    gradeTag = "LKG / Nursery",
    ageTag = "Age 3-4",
    titleEnglish = "Phonics & Alphabets",
    titleHinglish = "Akshar aur Awaazein",
    subtitleEnglish = "26 letter sounds · 156 example words",
    subtitleHinglish = "26 awaazein · 156 shabd examples",
  ),
  LEVEL_2_BLENDING(
    levelNumber = 2,
    gradeTag = "LKG / Nursery",
    ageTag = "Age 3-4",
    titleEnglish = "CVC Blending & Sight Words",
    titleHinglish = "Shabd Jodo & Sight Words",
    subtitleEnglish = "32 CVC blending words · 4 groups",
    subtitleHinglish = "32 CVC shabd · 4 groups mein sikhein",
  ),
  LEVEL_3_UKG(
    levelNumber = 3,
    gradeTag = "UKG",
    ageTag = "Age 4-5",
    titleEnglish = "Word Families, Digraphs & Magic E",
    titleHinglish = "Word Families, Digraphs & Magic E",
    subtitleEnglish = "8 families · 9 digraphs · 12 Magic E",
    subtitleHinglish = "8 families · 9 digraphs · 12 Magic 'e'",
  ),
  LEVEL_4_CLASS1(
    levelNumber = 4,
    gradeTag = "Class 1",
    ageTag = "Age 5-6",
    titleEnglish = "Blends & Sentence Reading",
    titleHinglish = "Blends aur Vaakya Padhna",
    subtitleEnglish = "12 blends (60 words) · 10 sentences",
    subtitleHinglish = "12 blends (60 shabd) · 10 vaakya",
  ),
  LEVEL_5_CLASS2(
    levelNumber = 5,
    gradeTag = "Class 2",
    ageTag = "Age 6-7",
    titleEnglish = "Grammar, Opposites & Stories",
    titleHinglish = "Grammar, Vilom aur Kahaniyan",
    subtitleEnglish = "18 grammar · 14 opposites · 5 stories",
    subtitleHinglish = "18 grammar · 14 vilom · 5 kahaniyan",
  ),
}

data class SightWordItem(
  val word: String,
  val hinglishTip: String,
  val hindiDevanagariCue: String,
  val exampleSentence: String,
  val emoji: String,
)

data class WordFamilyGroup(
  val ending: String,
  val rimePronunciation: String,
  val hinglishTip: String,
  val hindiVoiceTip: String,
  val words: List<WordFamilyEntry>,
)

data class WordFamilyEntry(
  val onset: String,
  val word: String,
  val emoji: String,
  val hinglishMeaning: String,
)

data class DigraphItem(
  val digraph: String,
  val soundSyllable: String,
  val hindiSound: String,
  val exampleWord: String,
  val remainingPart: String,
  val emoji: String,
  val hinglishTip: String,
  val hindiVoiceScript: String,
  val moreWords: List<String>,
)

data class MagicEWordPair(
  val shortWord: String,
  val shortMeaning: String,
  val shortEmoji: String,
  val magicWord: String,
  val magicMeaning: String,
  val magicEmoji: String,
  val vowelRuleTag: String,
  val hinglishRuleTip: String,
  val hindiVoiceScript: String,
)

data class BlendClusterItem(
  val blend: String,
  val spokenBlend: String,
  val exampleWord: String,
  val remainingPart: String,
  val emoji: String,
  val hinglishTip: String,
  val hindiVoiceScript: String,
  val sampleWords: List<String>,
)

data class FluencySentenceItem(
  val id: Int,
  val sentence: String,
  val emoji: String,
  val hinglishMeaning: String,
  val hinglishTip: String,
  val hindiVoiceExplain: String,
  val quizQuestion: String,
  val quizOptions: List<String>,
  val correctOptionIndex: Int,
)

enum class GrammarCategory(
  val titleEn: String,
  val titleHinglish: String,
  val hintHinglish: String,
  val emoji: String,
) {
  NOUN("Noun", "Noun (Naam)", "Kisi vyakti, jagah ya cheez ka naam", "🏷️"),
  VERB("Verb", "Verb (Kaam)", "Koi kaam ya action batane wala shabd", "🏃"),
  ADJECTIVE("Adjective", "Adjective (Gun/Rang)", "Kaisa hai — rang, size ya gun", "🎨"),
}

data class GrammarQuizItem(
  val word: String,
  val emoji: String,
  val sentence: String,
  val category: GrammarCategory,
  val hinglishTip: String,
)

data class OppositePairItem(
  val wordA: String,
  val emojiA: String,
  val wordB: String,
  val emojiB: String,
  val hinglishPair: String,
)

data class RhymeFamilyItem(
  val anchorWord: String,
  val emoji: String,
  val rhymingWords: List<String>,
  val oddOneOut: String,
)

data class PictureStoryItem(
  val title: String,
  val emojiBanner: String,
  val lines: List<String>,
  val hinglishSummary: String,
  val blankPrompt: String,
  val blankOptions: List<String>,
  val blankCorrectIndex: Int,
  val comprehensionQuestion: String,
  val comprehensionOptions: List<String>,
  val comprehensionCorrectIndex: Int,
)
