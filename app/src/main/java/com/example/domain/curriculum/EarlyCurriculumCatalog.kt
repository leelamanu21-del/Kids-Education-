package com.example.domain.curriculum

import com.example.domain.model.BlendClusterItem
import com.example.domain.model.DigraphItem
import com.example.domain.model.FluencySentenceItem
import com.example.domain.model.GrammarCategory
import com.example.domain.model.GrammarQuizItem
import com.example.domain.model.MagicEWordPair
import com.example.domain.model.OppositePairItem
import com.example.domain.model.PictureStoryItem
import com.example.domain.model.RhymeFamilyItem
import com.example.domain.model.SightWordItem
import com.example.domain.model.WordFamilyEntry
import com.example.domain.model.WordFamilyGroup

object EarlyCurriculumCatalog {

  // Level 1 & 2: Tricky Sight Words (LKG / Nursery)
  val sightWords: List<SightWordItem> =
    listOf(
      SightWordItem(
        word = "the",
        hinglishTip = "Ise dekh kar yaad rakhein — 'द' bolte hain, jaise 'the sun'.",
        hindiDevanagariCue = "यह sight word है द, जैसे the sun.",
        exampleSentence = "The sun is bright.",
        emoji = "☀️",
      ),
      SightWordItem(
        word = "is",
        hinglishTip = "'is' ka matlab 'है' — jaise 'He is happy'.",
        hindiDevanagariCue = "is का मतलब है, जैसे He is happy.",
        exampleSentence = "The cat is big.",
        emoji = "🐱",
      ),
      SightWordItem(
        word = "he",
        hinglishTip = "Ladke (Boy) ke liye 'he' bolte hain.",
        hindiDevanagariCue = "लड़के के लिए he बोलते हैं, जैसे He can run.",
        exampleSentence = "He can run fast.",
        emoji = "👦",
      ),
      SightWordItem(
        word = "she",
        hinglishTip = "Ladki (Girl) ke liye 'she' bolte hain.",
        hindiDevanagariCue = "लड़की के लिए she बोलते हैं, जैसे She has a kite.",
        exampleSentence = "She has a red kite.",
        emoji = "👧",
      ),
      SightWordItem(
        word = "to",
        hinglishTip = "Kisi jagah jaane ke liye 'to' — jaise 'go to school'.",
        hindiDevanagariCue = "यह है to, जैसे I go to school.",
        exampleSentence = "I go to the park.",
        emoji = "🏫",
      ),
      SightWordItem(
        word = "you",
        hinglishTip = "'you' matlab 'Aap' ya 'Tum' — 'You are a star!'",
        hindiDevanagariCue = "you मतलब आप या तुम, You are a star!",
        exampleSentence = "You are my friend.",
        emoji = "🌟",
      ),
      SightWordItem(
        word = "we",
        hinglishTip = "'we' matlab 'Hum sab' — 'We play together'.",
        hindiDevanagariCue = "we मतलब हम सब, We play together.",
        exampleSentence = "We love to read.",
        emoji = "🤝",
      ),
      SightWordItem(
        word = "my",
        hinglishTip = "'my' matlab 'Mera / Meri' — jaise 'my book'.",
        hindiDevanagariCue = "my मतलब मेरा या मेरी, जैसे my book.",
        exampleSentence = "This is my red ball.",
        emoji = "⚽",
      ),
    )

  // Level 3: Word Families (UKG)
  val wordFamilies: List<WordFamilyGroup> =
    listOf(
      WordFamilyGroup(
        ending = "-at",
        rimePronunciation = "at",
        hinglishTip = "Ek jaisi aawaz: b-at, c-at, m-at, h-at!",
        hindiVoiceTip = "चलो ऐट फैमिली सीखें, जैसे बैट, कैट, और मैट।",
        words =
          listOf(
            WordFamilyEntry("b", "bat", "🏏", "Balla (Bat)"),
            WordFamilyEntry("c", "cat", "🐱", "Billi (Cat)"),
            WordFamilyEntry("m", "mat", "🧶", "Chatai (Mat)"),
            WordFamilyEntry("h", "hat", "🎩", "Topi (Hat)"),
            WordFamilyEntry("r", "rat", "🐭", "Chuha (Rat)"),
          ),
      ),
      WordFamilyGroup(
        ending = "-an",
        rimePronunciation = "an",
        hinglishTip = "Sabke peeche 'an' lagao: m-an, p-an, c-an, v-an!",
        hindiVoiceTip = "चलो ऐन फैमिली सीखें, जैसे मैन, पैन, और वैन।",
        words =
          listOf(
            WordFamilyEntry("m", "man", "👨", "Aadmi (Man)"),
            WordFamilyEntry("p", "pan", "🍳", "Tawa (Pan)"),
            WordFamilyEntry("c", "can", "🥫", "Dibba (Can)"),
            WordFamilyEntry("v", "van", "🚐", "Gaadi (Van)"),
            WordFamilyEntry("f", "fan", "🪭", "Pankha (Fan)"),
          ),
      ),
      WordFamilyGroup(
        ending = "-in",
        rimePronunciation = "in",
        hinglishTip = "Chhoti 'in' aawaz: p-in, t-in, b-in, w-in!",
        hindiVoiceTip = "चलो इन फैमिली सीखें, जैसे पिन, टिन, और बिन।",
        words =
          listOf(
            WordFamilyEntry("p", "pin", "📌", "Pin"),
            WordFamilyEntry("t", "tin", "🥫", "Tin ka dabba"),
            WordFamilyEntry("b", "bin", "🗑️", "Dustbin"),
            WordFamilyEntry("w", "win", "🏆", "Jeetna (Win)"),
          ),
      ),
      WordFamilyGroup(
        ending = "-op",
        rimePronunciation = "op",
        hinglishTip = "Uchhalne wali 'op' aawaz: h-op, t-op, m-op, p-op!",
        hindiVoiceTip = "चलो ऑप फैमिली सीखें, जैसे हॉप, टॉप, और मॉप।",
        words =
          listOf(
            WordFamilyEntry("h", "hop", "🐰", "Koodna (Hop)"),
            WordFamilyEntry("t", "top", "🪀", "Lattu (Top)"),
            WordFamilyEntry("m", "mop", "🧹", "Pocha (Mop)"),
            WordFamilyEntry("p", "pop", "🍿", "Popcorn"),
          ),
      ),
    )

  // Level 3: Digraphs (UKG)
  val digraphs: List<DigraphItem> =
    listOf(
      DigraphItem(
        digraph = "sh",
        soundSyllable = "shuh",
        hindiSound = "श",
        exampleWord = "ship",
        remainingPart = "ip",
        emoji = "🚢",
        hinglishTip = "Chalo, ab 'sh' aur 'ip' ko milakar 'ship' banate hain!",
        hindiVoiceScript = "चलो, अब sh और ip को मिलाकर ship बनाते हैं!",
        moreWords = listOf("ship", "shop", "fish", "shoe"),
      ),
      DigraphItem(
        digraph = "ch",
        soundSyllable = "chuh",
        hindiSound = "च",
        exampleWord = "chair",
        remainingPart = "air",
        emoji = "🪑",
        hinglishTip = "C aur H milkar banate hain 'च' — jaise ch + air = chair!",
        hindiVoiceScript = "सी और एच मिलकर बनाते हैं च, जैसे chair और cheese!",
        moreWords = listOf("chair", "chin", "chop", "chick"),
      ),
      DigraphItem(
        digraph = "th",
        soundSyllable = "thuh",
        hindiSound = "थ",
        exampleWord = "thumb",
        remainingPart = "umb",
        emoji = "👍",
        hinglishTip = "T aur H milkar banate hain 'थ' — jaise th + umb = thumb!",
        hindiVoiceScript = "टी और एच मिलकर बनाते हैं थ, जैसे thumb और three!",
        moreWords = listOf("thumb", "three", "thin", "bath"),
      ),
      DigraphItem(
        digraph = "ph",
        soundSyllable = "fuh",
        hindiSound = "फ़",
        exampleWord = "phone",
        remainingPart = "one",
        emoji = "📱",
        hinglishTip = "P aur H saath aayein to 'फ़' (F) jaisi aawaz aati hai — phone!",
        hindiVoiceScript = "पी और एच साथ आएं तो फ़ जैसी आवाज़ आती है, जैसे phone और photo!",
        moreWords = listOf("phone", "photo", "dolphin", "elephant"),
      ),
      DigraphItem(
        digraph = "wh",
        soundSyllable = "wuh",
        hindiSound = "व",
        exampleWord = "whale",
        remainingPart = "ale",
        emoji = "🐳",
        hinglishTip = "W aur H milkar bolte hain 'व' — jaise wh + ale = whale!",
        hindiVoiceScript = "डब्ल्यू और एच मिलकर बोलते हैं व, जैसे whale और wheel!",
        moreWords = listOf("whale", "wheel", "white", "whistle"),
      ),
    )

  // Level 3: Magic 'E' (Silent E) Words (UKG)
  val magicEWords: List<MagicEWordPair> =
    listOf(
      MagicEWordPair(
        shortWord = "cap",
        shortMeaning = "Topi (Cap)",
        shortEmoji = "🧢",
        magicWord = "cape",
        magicMeaning = "Superhero Cape",
        magicEmoji = "🦸",
        hinglishRuleTip = "Aakhir mein Magic 'e' lagte hi 'cap' ban gaya 'cape'!",
        hindiVoiceScript = "आखिर में मैजिक e लगते ही cap बन गया cape!",
      ),
      MagicEWordPair(
        shortWord = "hop",
        shortMeaning = "Koodna (Hop)",
        shortEmoji = "🐰",
        magicWord = "hope",
        magicMeaning = "Aasha (Hope)",
        magicEmoji = "🌈",
        hinglishRuleTip = "Magic 'e' chup rehta hai aur 'o' ko lamba bolwata hai: hop → hope!",
        hindiVoiceScript = "मैजिक e से hop बन जाता है hope!",
      ),
      MagicEWordPair(
        shortWord = "tap",
        shortMeaning = "Nal (Tap)",
        shortEmoji = "🚰",
        magicWord = "tape",
        magicMeaning = "Chipkane wali Tape",
        magicEmoji = "📼",
        hinglishRuleTip = "Tap ke peeche 'e' jodo to banega 'tape'!",
        hindiVoiceScript = "tap के पीछे मैजिक e जोड़ो तो बनेगा tape!",
      ),
      MagicEWordPair(
        shortWord = "kit",
        shortMeaning = "Doctor Kit",
        shortEmoji = "🧰",
        magicWord = "kite",
        magicMeaning = "Patang (Kite)",
        magicEmoji = "🪁",
        hinglishRuleTip = "'kit' mein silent 'e' judte hi udi Patang 'kite'!",
        hindiVoiceScript = "kit में मैजिक e जुड़ते ही बन गया kite!",
      ),
      MagicEWordPair(
        shortWord = "cub",
        shortMeaning = "Sher ka baccha (Cub)",
        shortEmoji = "🦁",
        magicWord = "cube",
        magicMeaning = "Dice / Cube",
        magicEmoji = "🧊",
        hinglishRuleTip = "'cub' se Magic 'e' ne banaya 'cube'!",
        hindiVoiceScript = "cub से मैजिक e ने बनाया cube!",
      ),
    )

  // Level 4: Blends & Consonant Clusters (Class 1)
  val blends: List<BlendClusterItem> =
    listOf(
      BlendClusterItem(
        blend = "bl",
        spokenBlend = "bluh",
        exampleWord = "blue",
        remainingPart = "ue",
        emoji = "💙",
        hinglishTip = "B aur L ko jaldi saath bolo 'ब्ल': bl + ue = blue!",
        hindiVoiceScript = "b और l को साथ बोलो ब्ल, जैसे blue और black.",
        sampleWords = listOf("blue", "black", "block", "blow"),
      ),
      BlendClusterItem(
        blend = "gr",
        spokenBlend = "gruh",
        exampleWord = "green",
        remainingPart = "een",
        emoji = "💚",
        hinglishTip = "G aur R milkar bole 'ग्र': gr + een = green!",
        hindiVoiceScript = "g और r मिलकर बोले ग्र, जैसे green और grapes.",
        sampleWords = listOf("green", "grapes", "grass", "grin"),
      ),
      BlendClusterItem(
        blend = "st",
        spokenBlend = "stuh",
        exampleWord = "star",
        remainingPart = "ar",
        emoji = "⭐",
        hinglishTip = "S aur T saath mein bole 'स्ट': st + ar = star!",
        hindiVoiceScript = "s और t साथ में बोले स्ट, जैसे star और stop.",
        sampleWords = listOf("star", "stop", "step", "stick"),
      ),
      BlendClusterItem(
        blend = "fr",
        spokenBlend = "fruh",
        exampleWord = "frog",
        remainingPart = "og",
        emoji = "🐸",
        hinglishTip = "F aur R milkar bole 'फ्र': fr + og = frog!",
        hindiVoiceScript = "f और r मिलकर बोले फ्र, जैसे frog और fruit.",
        sampleWords = listOf("frog", "fruit", "friend", "frame"),
      ),
      BlendClusterItem(
        blend = "cl",
        spokenBlend = "kluh",
        exampleWord = "clap",
        remainingPart = "ap",
        emoji = "👏",
        hinglishTip = "C aur L saath mein bole 'क्ल': cl + ap = clap!",
        hindiVoiceScript = "c और l साथ में बोले क्ल, जैसे clap और cloud.",
        sampleWords = listOf("clap", "cloud", "clock", "clean"),
      ),
      BlendClusterItem(
        blend = "tr",
        spokenBlend = "truh",
        exampleWord = "train",
        remainingPart = "ain",
        emoji = "🚂",
        hinglishTip = "T aur R milkar bole 'ट्र': tr + ain = train!",
        hindiVoiceScript = "t और r मिलकर बोले ट्र, जैसे train और tree.",
        sampleWords = listOf("train", "tree", "truck", "trip"),
      ),
    )

  // Level 4: Simple Sentences Reading (Class 1)
  val fluencySentences: List<FluencySentenceItem> =
    listOf(
      FluencySentenceItem(
        id = 1,
        sentence = "The cat sat on the mat.",
        emoji = "🐱",
        hinglishMeaning = "Billi chatai par baithi hai.",
        hinglishTip = "Har word par tap karke suno, phir poora vaakya ek saath padho!",
        hindiVoiceExplain = "इसका मतलब है बिल्ली चटाई पर बैठी है।",
        quizQuestion = "Where did the cat sit?",
        quizOptions = listOf("On the mat", "In a ship", "On a star"),
        correctOptionIndex = 0,
      ),
      FluencySentenceItem(
        id = 2,
        sentence = "A green frog can hop fast.",
        emoji = "🐸",
        hinglishMeaning = "Ek hara mendhak tez kood sakta hai.",
        hinglishTip = "'gr' (green) aur 'fr' (frog) blend dhyan se padho!",
        hindiVoiceExplain = "इसका मतलब है एक हरा मेंढक तेज़ कूद सकता है।",
        quizQuestion = "What color is the frog?",
        quizOptions = listOf("Blue", "Green", "Yellow"),
        correctOptionIndex = 1,
      ),
      FluencySentenceItem(
        id = 3,
        sentence = "She has a big blue ship.",
        emoji = "🚢",
        hinglishMeaning = "Uske paas ek bada neela jahaaz hai.",
        hinglishTip = "'She' sight word hai aur 'sh' se 'ship' banta hai!",
        hindiVoiceExplain = "इसका मतलब है उसके पास एक बड़ा नीला जहाज़ है।",
        quizQuestion = "What does she have?",
        quizOptions = listOf("A blue ship", "A red hat", "A tiny dog"),
        correctOptionIndex = 0,
      ),
      FluencySentenceItem(
        id = 4,
        sentence = "Look at the bright star in the sky.",
        emoji = "🌟",
        hinglishMeaning = "Aasman mein chamakdar taare ko dekho.",
        hinglishTip = "'st' blend se 'star' aur 'sk' se 'sky' banta hai!",
        hindiVoiceExplain = "इसका मतलब है आसमान में चमकदार तारे को देखो।",
        quizQuestion = "Where is the bright star?",
        quizOptions = listOf("In a box", "In the sky", "Under a bed"),
        correctOptionIndex = 1,
      ),
    )

  // Level 5: Grammar - Nouns, Verbs, Adjectives (Class 2)
  val grammarItems: List<GrammarQuizItem> =
    listOf(
      GrammarQuizItem(
        word = "Mango",
        emoji = "🥭",
        sentence = "I eat a sweet Mango.",
        category = GrammarCategory.NOUN,
        hinglishTip = "'Mango' ek phal ka naam hai — isliye yeh Noun hai!",
      ),
      GrammarQuizItem(
        word = "Jump",
        emoji = "🦘",
        sentence = "The rabbits jump high.",
        category = GrammarCategory.VERB,
        hinglishTip = "'Jump' matlab koodna (ek kaam/action) — isliye yeh Verb hai!",
      ),
      GrammarQuizItem(
        word = "Blue",
        emoji = "💙",
        sentence = "Look at the blue sky.",
        category = GrammarCategory.ADJECTIVE,
        hinglishTip = "'Blue' aasman ka rang batata hai — isliye yeh Adjective hai!",
      ),
      GrammarQuizItem(
        word = "School",
        emoji = "🏫",
        sentence = "We go to school daily.",
        category = GrammarCategory.NOUN,
        hinglishTip = "'School' ek jagah ka naam hai — isliye yeh Noun hai!",
      ),
      GrammarQuizItem(
        word = "Run",
        emoji = "🏃",
        sentence = "Kids run in the park.",
        category = GrammarCategory.VERB,
        hinglishTip = "'Run' matlab daudna (kaam/action) — isliye yeh Verb hai!",
      ),
      GrammarQuizItem(
        word = "Happy",
        emoji = "😊",
        sentence = "Riya is a happy girl.",
        category = GrammarCategory.ADJECTIVE,
        hinglishTip = "'Happy' batata hai ki ladki kaisi hai — isliye yeh Adjective hai!",
      ),
    )

  // Level 5: Opposites (Class 2)
  val opposites: List<OppositePairItem> =
    listOf(
      OppositePairItem("Big", "🐘", "Small", "🐭", "Bada ↔ Chhota"),
      OppositePairItem("Hot", "☀️", "Cold", "🧊", "Garam ↔ Thanda"),
      OppositePairItem("Day", "🌤️", "Night", "🌙", "Din ↔ Raat"),
      OppositePairItem("Happy", "😊", "Sad", "😢", "Khush ↔ Udaas"),
      OppositePairItem("Fast", "🐆", "Slow", "🐢", "Tez ↔ Dheema"),
      OppositePairItem("Up", "⬆️", "Down", "⬇️", "Upar ↔ Neeche"),
    )

  // Level 5: Rhyming Words (Class 2)
  val rhymeFamilies: List<RhymeFamilyItem> =
    listOf(
      RhymeFamilyItem("Star", "⭐", listOf("Car", "Jar", "Far"), "Dog"),
      RhymeFamilyItem("Cake", "🎂", listOf("Bake", "Lake", "Make"), "Sun"),
      RhymeFamilyItem("Ball", "⚽", listOf("Tall", "Wall", "Call"), "Pen"),
      RhymeFamilyItem("Tree", "🌳", listOf("Bee", "Free", "Three"), "Hat"),
    )

  // Level 5: Picture Stories & Spelling (Class 2)
  val pictureStories: List<PictureStoryItem> =
    listOf(
      PictureStoryItem(
        title = "Aarav's Little Red Kite",
        emojiBanner = "👦🪁🌤️",
        lines =
          listOf(
            "Aarav has a bright red kite.",
            "He goes to the green park with his dog.",
            "The cool wind blows and the kite flies high in the sky!",
          ),
        hinglishSummary = "Aarav apni laal patang lekar hare park mein gaya aur hawa mein patang udi!",
        blankPrompt = "The kite flies high in the _____.",
        blankOptions = listOf("sky", "cup", "box"),
        blankCorrectIndex = 0,
        comprehensionQuestion = "Who went to the park with Aarav?",
        comprehensionOptions = listOf("His dog", "A whale", "A lion"),
        comprehensionCorrectIndex = 0,
      ),
      PictureStoryItem(
        title = "Pip the Friendly Frog",
        emojiBanner = "🐸🪷🌧️",
        lines =
          listOf(
            "Pip is a tiny green frog.",
            "He sits on a big round leaf in the pond.",
            "When soft rain drops fall, Pip sings and hops happily!",
          ),
        hinglishSummary = "Chhota hara mendhak Pip talaab ke patte par baith kar baarish mein khush hokar naachta hai!",
        blankPrompt = "Pip is a tiny _____ frog.",
        blankOptions = listOf("green", "chair", "train"),
        blankCorrectIndex = 0,
        comprehensionQuestion = "Where does Pip sit?",
        comprehensionOptions = listOf("On a round leaf", "Inside a van", "On a bed"),
        comprehensionCorrectIndex = 0,
      ),
    )
}
