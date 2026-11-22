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
  LEVEL_6_CLASS3(
    levelNumber = 6,
    gradeTag = "Class 3",
    ageTag = "Age 7-8",
    titleEnglish = "Vowel Teams, Affixes & Tenses",
    titleHinglish = "Vowel Teams, Prefixes & Tenses",
    subtitleEnglish = "10 vowel teams · 12 affixes · 12 tenses",
    subtitleHinglish = "10 vowel teams · 12 affixes · 12 tenses",
  ),
  LEVEL_7_CLASS4(
    levelNumber = 7,
    gradeTag = "Class 4",
    ageTag = "Age 8-9",
    titleEnglish = "Homophones, Grammar & Reading",
    titleHinglish = "Homophones, Mechanics & Passages",
    subtitleEnglish = "14 vocab pairs · 12 mechanics · 5 passages",
    subtitleHinglish = "14 vocab pairs · 12 rules · 5 passages",
  ),
  LEVEL_8_CLASS5(
    levelNumber = 8,
    gradeTag = "Class 5",
    ageTag = "Age 9-10",
    titleEnglish = "Idioms, Voice & Critical Reading",
    titleHinglish = "Muhavare, Voice & Critical Reading",
    subtitleEnglish = "12 idioms · 12 voice/figures · 5 essays",
    subtitleHinglish = "12 idioms · 12 voice & figures · 5 essays",
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

// --- Level 6 · Class 3 (Age 7-8) ---

data class VowelTeamItem(
  val team: String,
  val spokenPhoneme: String,
  val hindiSound: String,
  val exampleWord: String,
  val emoji: String,
  val hinglishTip: String,
  val hindiVoiceScript: String,
  val sampleWords: List<String>,
  val quizQuestion: String,
  val quizOptions: List<String>,
  val correctIndex: Int,
)

data class AffixWordItem(
  val affix: String,
  val ruleType: String,
  val rootWord: String,
  val formedWord: String,
  val emoji: String,
  val hinglishMeaning: String,
  val grammarTip: String,
  val quizPrompt: String,
  val quizOptions: List<String>,
  val correctIndex: Int,
)

data class Class3GrammarQuizItem(
  val topicTag: String,
  val sentenceWithBlank: String,
  val emoji: String,
  val options: List<String>,
  val correctIndex: Int,
  val completedSentence: String,
  val hinglishExplanation: String,
)

// --- Level 7 · Class 4 (Age 8-9) ---

data class VocabRelationItem(
  val category: String,
  val wordA: String,
  val wordB: String,
  val emoji: String,
  val hinglishMeaning: String,
  val exampleSentenceA: String,
  val exampleSentenceB: String,
  val quizPrompt: String,
  val quizOptions: List<String>,
  val correctIndex: Int,
)

data class Class4MechanicsQuizItem(
  val topicTag: String,
  val promptSentence: String,
  val emoji: String,
  val options: List<String>,
  val correctIndex: Int,
  val completedSentence: String,
  val hinglishRule: String,
)

data class PassageWordMeaning(
  val word: String,
  val meaningEn: String,
  val hinglishCue: String,
)

data class ReadingPassageItem(
  val id: String,
  val title: String,
  val emojiBanner: String,
  val levelLabel: String,
  val paragraphs: List<String>,
  val vocabulary: List<PassageWordMeaning>,
  val hinglishTakeaway: String,
  val question1: String,
  val options1: List<String>,
  val correctIndex1: Int,
  val question2: String,
  val options2: List<String>,
  val correctIndex2: Int,
)

// --- Level 8 · Class 5 (Age 9-10) ---

data class IdiomProverbItem(
  val expression: String,
  val typeTag: String,
  val emoji: String,
  val simpleMeaningEn: String,
  val hinglishExplanation: String,
  val hindiVoiceScript: String,
  val exampleSentence: String,
  val quizQuestion: String,
  val quizOptions: List<String>,
  val correctIndex: Int,
)

data class VoiceAndFigureItem(
  val conceptTag: String,
  val baseSentence: String,
  val emoji: String,
  val promptQuestion: String,
  val options: List<String>,
  val correctIndex: Int,
  val answerSentence: String,
  val hinglishRuleTip: String,
)
