package com.example.domain.curriculum

import com.example.domain.model.LearningMode
import com.example.domain.model.PhonicsLetter
import com.example.domain.model.TilePalette

enum class SoundboardCategory(
  val labelEn: String,
  val labelHinglish: String,
) {
  ALL("All", "Sabhi"),
  LETTERS("Letters", "Akshar (A-Z)"),
  DIGRAPHS("Digraphs", "Digraphs (sh, ch, th)"),
  BLENDS("Blends", "Blends (bl, gr, st)"),
  VOWEL_TEAMS("Vowel Teams", "Vowel Teams (ai, ee)"),
}

data class SoundboardExampleWord(
  val word: String,
  val emoji: String,
)

data class SoundboardTile(
  val id: String,
  val displaySymbol: String,
  val category: SoundboardCategory,
  val phonemeBadge: String,
  val hindiSound: String,
  val primaryWord: String,
  val primaryEmoji: String,
  val breakdownHint: String,
  val cueHinglish: String,
  val cueEnglish: String,
  val hindiVoiceScript: String,
  val englishVoiceScript: String,
  val spokenToken: String,
  val palette: TilePalette,
  val examples: List<SoundboardExampleWord>,
  val letterSource: PhonicsLetter? = null,
)

object SoundboardCatalog {

  val allTiles: List<SoundboardTile> by lazy {
    letterTiles + digraphTiles + blendTiles + vowelTeamTiles
  }

  private val letterTiles: List<SoundboardTile> by lazy {
    PhonicsCurriculum.lettersForMode(LearningMode.AZ).map { letter ->
      SoundboardTile(
        id = "letter_${letter.letter}",
        displaySymbol = "${letter.letter}${letter.lowercase}",
        category = SoundboardCategory.LETTERS,
        phonemeBadge = letter.phonemeSymbol,
        hindiSound = PhonicsCurriculum.hindiSoundTag(letter.letter),
        primaryWord = letter.exampleWord,
        primaryEmoji = letter.exampleEmoji,
        breakdownHint = "${letter.letter} · ${letter.exampleWord} (${letter.hindiWord})",
        cueHinglish = letter.hinglishCue,
        cueEnglish = letter.englishCue,
        hindiVoiceScript = letter.hinglishAudioScript,
        englishVoiceScript = letter.englishAudioScript,
        spokenToken = letter.phonemeSpeech,
        palette = letter.palette,
        examples = letter.sampleWords.map { SoundboardExampleWord(it, emojiForWord(it, letter.exampleEmoji)) },
        letterSource = letter,
      )
    }
  }

  private val digraphPalettes =
    listOf(
      TilePalette.PLUM,
      TilePalette.CORAL,
      TilePalette.SKY_TEAL,
      TilePalette.TANGERINE,
      TilePalette.MINT,
      TilePalette.SUNSHINE,
    )

  private val digraphTiles: List<SoundboardTile> by lazy {
    EarlyCurriculumCatalog.digraphs.mapIndexed { idx, item ->
      val split =
        if (item.remainingPart.isNotBlank() && item.exampleWord.startsWith(item.digraph, ignoreCase = true)) {
          "${item.digraph} + ${item.remainingPart} = ${item.exampleWord}"
        } else {
          "${item.digraph} in ${item.exampleWord}"
        }
      SoundboardTile(
        id = "digraph_${item.digraph}",
        displaySymbol = item.digraph,
        category = SoundboardCategory.DIGRAPHS,
        phonemeBadge = "/${item.digraph}/",
        hindiSound = item.hindiSound,
        primaryWord = item.exampleWord,
        primaryEmoji = item.emoji,
        breakdownHint = split,
        cueHinglish = item.hinglishTip,
        cueEnglish = "${item.digraph} makes the ${item.soundSyllable} sound, as in ${item.exampleWord}.",
        hindiVoiceScript = item.hindiVoiceScript,
        englishVoiceScript = "${item.digraph} says ${item.soundSyllable}, as in ${item.exampleWord}.",
        spokenToken = item.soundSyllable,
        palette = digraphPalettes[idx % digraphPalettes.size],
        examples = item.moreWords.map { SoundboardExampleWord(it, emojiForWord(it, item.emoji)) },
      )
    }
  }

  private val blendTiles: List<SoundboardTile> by lazy {
    UpperCurriculumCatalog.blends.mapIndexed { idx, item ->
      SoundboardTile(
        id = "blend_${item.blend}",
        displaySymbol = item.blend,
        category = SoundboardCategory.BLENDS,
        phonemeBadge = "/${item.blend}/",
        hindiSound = hindiCueForBlend(item.blend),
        primaryWord = item.exampleWord,
        primaryEmoji = item.emoji,
        breakdownHint = "${item.blend} + ${item.remainingPart} = ${item.exampleWord}",
        cueHinglish = item.hinglishTip,
        cueEnglish = "Blend ${item.blend} smoothly together, as in ${item.exampleWord}.",
        hindiVoiceScript = item.hindiVoiceScript,
        englishVoiceScript = "${item.blend} as in ${item.exampleWord}.",
        spokenToken = item.spokenBlend,
        palette = digraphPalettes[(idx + 2) % digraphPalettes.size],
        examples = item.sampleWords.map { SoundboardExampleWord(it, emojiForWord(it, item.emoji)) },
      )
    }
  }

  private val vowelTeamTiles: List<SoundboardTile> by lazy {
    Class3CurriculumCatalog.vowelTeams.mapIndexed { idx, item ->
      val safeId = item.team.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
      SoundboardTile(
        id = "team_$safeId",
        displaySymbol = item.team,
        category = SoundboardCategory.VOWEL_TEAMS,
        phonemeBadge = item.spokenPhoneme,
        hindiSound = item.hindiSound,
        primaryWord = item.exampleWord,
        primaryEmoji = item.emoji,
        breakdownHint = "${item.team} → ${item.exampleWord}",
        cueHinglish = item.hinglishTip,
        cueEnglish = "${item.team} says ${item.spokenPhoneme}, as in ${item.exampleWord}.",
        hindiVoiceScript = item.hindiVoiceScript,
        englishVoiceScript = "${item.team} makes the ${item.spokenPhoneme} sound, as in ${item.exampleWord}.",
        spokenToken = item.spokenPhoneme,
        palette = digraphPalettes[(idx + 4) % digraphPalettes.size],
        examples = item.sampleWords.map { SoundboardExampleWord(it, emojiForWord(it, item.emoji)) },
      )
    }
  }

  fun tilesFor(category: SoundboardCategory): List<SoundboardTile> =
    if (category == SoundboardCategory.ALL) allTiles
    else allTiles.filter { it.category == category }

  private fun hindiCueForBlend(blend: String): String =
    when (blend.lowercase()) {
      "bl" -> "ब्ल"
      "gr" -> "ग्र"
      "st" -> "स्ट"
      "fr" -> "फ्र"
      "cl" -> "क्ल"
      "tr" -> "ट्र"
      "br" -> "ब्र"
      "cr" -> "क्र"
      "dr" -> "ड्र"
      "fl" -> "फ़्ल"
      "pl" -> "प्ल"
      "sp" -> "स्प"
      else -> blend
    }

  private fun emojiForWord(word: String, fallback: String): String =
    when (word.lowercase().trim()) {
      "sun" -> "☀️"
      "sit" -> "🪑"
      "sip" -> "🥤"
      "star" -> "⭐"
      "sock" -> "🧦"
      "smile" -> "😊"
      "ant" -> "🐜"
      "apple" -> "🍎"
      "axe" -> "🪓"
      "arrow" -> "🏹"
      "astronaut" -> "🧑‍🚀"
      "tap" -> "🚰"
      "tin" -> "🥫"
      "top" -> "🪀"
      "tiger" -> "🐯"
      "tent" -> "⛺"
      "table" -> "🪵"
      "pan" -> "🍳"
      "pin" -> "📌"
      "pen" -> "🖊️"
      "parrot" -> "🦜"
      "igloo" -> "🧊"
      "ink" -> "🖋️"
      "insect" -> "🪲"
      "india" -> "🇮🇳"
      "net" -> "🥅"
      "nest" -> "🪺"
      "nut" -> "🥜"
      "nose" -> "👃"
      "cat" -> "🐱"
      "cup" -> "☕"
      "cap" -> "🧢"
      "cake" -> "🍰"
      "camel" -> "🐪"
      "kite" -> "🪁"
      "king" -> "👑"
      "key" -> "🔑"
      "kettle" -> "🫖"
      "egg" -> "🥚"
      "elephant" -> "🐘"
      "hat" -> "🎩"
      "hen" -> "🐔"
      "house" -> "🏠"
      "hand" -> "✋"
      "rat" -> "🐭"
      "rain" -> "🌧️"
      "ring" -> "💍"
      "rocket" -> "🚀"
      "rose" -> "🌹"
      "mat" -> "🧶"
      "moon" -> "🌙"
      "mango" -> "🥭"
      "monkey" -> "🐒"
      "milk" -> "🥛"
      "dog" -> "🐶"
      "drum" -> "🥁"
      "duck" -> "🦆"
      "dolphin" -> "🐬"
      "ship" -> "🚢"
      "shop" -> "🛍️"
      "fish" -> "🐟"
      "shoe" -> "👟"
      "shell" -> "🐚"
      "brush" -> "🪥"
      "chair" -> "🪑"
      "chick" -> "🐥"
      "cheese" -> "🧀"
      "lunch" -> "🍱"
      "thumb" -> "👍"
      "three" -> "3️⃣"
      "bath" -> "🛁"
      "tooth" -> "🦷"
      "phone" -> "📱"
      "photo" -> "📸"
      "trophy" -> "🏆"
      "whale" -> "🐳"
      "wheel" -> "🛞"
      "whistle" -> "📣"
      "wheat" -> "🌾"
      "clock" -> "🕰️"
      "lock" -> "🔒"
      "truck" -> "🚚"
      "sing" -> "🎤"
      "wing" -> "🪽"
      "song" -> "🎵"
      "queen" -> "👸"
      "quilt" -> "🛏️"
      "spoon" -> "🥄"
      "zoo" -> "🦁"
      "boot" -> "🥾"
      "blue" -> "💙"
      "green" -> "💚"
      "grapes" -> "🍇"
      "grass" -> "🌿"
      "frog" -> "🐸"
      "fruit" -> "🍓"
      "clap" -> "👏"
      "cloud" -> "☁️"
      "train" -> "🚂"
      "tree" -> "🌳"
      "bread" -> "🍞"
      "crab" -> "🦀"
      "crown" -> "👑"
      "crayon" -> "🖍️"
      "flower" -> "🌸"
      "flag" -> "🚩"
      "plane" -> "✈️"
      "plant" -> "🪴"
      "plate" -> "🍽️"
      "spider" -> "🕷️"
      "paint" -> "🎨"
      "boat" -> "⛵"
      "coat" -> "🧥"
      "road" -> "🛣️"
      "snow" -> "❄️"
      "coin" -> "🪙"
      "toy" -> "🧸"
      "mouse" -> "🐭"
      "cow" -> "🐄"
      "cook" -> "🧑‍🍳"
      "book" -> "📖"
      "park" -> "🏞️"
      "garden" -> "🏡"
      "corn" -> "🌽"
      "bird" -> "🐦"
      "turtle" -> "🐢"
      "light" -> "💡"
      "night" -> "🌃"
      "station" -> "🚉"
      "leaf" -> "🍃"
      else -> fallback
    }
}
