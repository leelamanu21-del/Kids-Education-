package com.example.domain.curriculum

data class GrammarTopicRule(
  val topicTag: String,
  val titleEn: String,
  val titleHinglish: String,
  val ruleEn: String,
  val ruleHinglish: String,
  val formulaOrPattern: String,
  val examples: List<String>,
)

object GrammarRulesCatalog {

  // Level 2 · LKG Tricky Sight Words Rule
  val lkgSightWordRule =
    GrammarTopicRule(
      topicTag = "Sight Words",
      titleEn = "Sight Words (Tricky Words) Rule",
      titleHinglish = "Sight Words Rule (Dekhkar Yaad Rakhne Wale Shabd)",
      ruleEn = "Sight words do not follow normal letter-by-letter phonics rules. Recognise the whole word instantly by sight without sounding out every letter.",
      ruleHinglish = "Sight words normal phonics rules se nahi chalte — inhe akshar-akshar jodne ke bajay seedha dekhkar aur bolkar yaad rakha jaata hai.",
      formulaOrPattern = "See the Whole Word → Say It in One Snap (No Letter-by-Letter Breaking)",
      examples = listOf("the", "she", "you", "was", "they", "said"),
    )

  // Level 3 · UKG Grammar & Phonics Spelling Rules
  val ukgRules: List<GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Word Families",
        titleEn = "Word Family (Rime) Rule",
        titleHinglish = "Word Family Rule (Ek Jaisi Awaaz)",
        ruleEn = "Keep the ending letters the same and change only the starting letter to build rhyming words.",
        ruleHinglish = "Aakhir ke akshar same rakho aur sirf shuruat ka akshar badalkar naye rhyming shabd banao.",
        formulaOrPattern = "Starting Sound (Onset) + Ending Family (-at / -an / -in)",
        examples = listOf("b + at = bat", "c + at = cat", "m + an = man", "p + in = pin"),
      ),
      GrammarTopicRule(
        topicTag = "Digraphs",
        titleEn = "Consonant Digraph Rule",
        titleHinglish = "Digraph Rule (2 Akshar = 1 Nayi Awaaz)",
        ruleEn = "When two consonants stand together and make ONE brand-new sound, they form a digraph.",
        ruleHinglish = "Jab do consonants saath aakar bilkul ek nayi awaaz banayein, use Digraph kehte hain.",
        formulaOrPattern = "s + h → /sh/ (श) · c + h → /ch/ (च) · t + h → /th/ (थ)",
        examples = listOf("sh + ip = ship", "ch + air = chair", "th + umb = thumb", "ph + one = phone"),
      ),
      GrammarTopicRule(
        topicTag = "Magic E",
        titleEn = "Silent Magic 'E' Rule",
        titleHinglish = "Magic 'E' (Silent E) Spelling Rule",
        ruleEn = "A silent 'e' at the end makes the middle vowel say its long alphabet name instead of its short sound.",
        ruleHinglish = "Shabd ke end mein chup 'e' lagne se beech ka vowel chhoti awaaz ki jagah apna lamba naam bolta hai.",
        formulaOrPattern = "Short Vowel Word + Silent 'e' → Long Vowel Word",
        examples = listOf("cap → cape", "kit → kite", "hop → hope", "cub → cube"),
      ),
    )

  // Level 4 · Class 1 Fluency & Sentence Rules
  val class1Rules: List<GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Consonant Blends",
        titleEn = "Consonant Blend Rule",
        titleHinglish = "Consonant Blend Rule (Saath-Saath Awaaz)",
        ruleEn = "In a blend, two consonants slide together quickly while keeping both of their individual sounds.",
        ruleHinglish = "Blend mein dono consonants jaldi saath bole jaate hain, aur dono ki apni awaaz sunai deti hai.",
        formulaOrPattern = "b + l → bl · g + r → gr · s + t → st",
        examples = listOf("bl + ue = blue", "gr + een = green", "st + ar = star", "tr + ain = train"),
      ),
      GrammarTopicRule(
        topicTag = "Sentences",
        titleEn = "Basic Sentence Structure Rule",
        titleHinglish = "Vaakya (Sentence) Banane ka Rule",
        ruleEn = "Every complete sentence expresses a clear thought, begins with a CAPITAL letter, and ends with a full stop (.), question mark (?), or exclamation (!).",
        ruleHinglish = "Har sahi vaakya CAPITAL letter se shuru hota hai, poora matlab batata hai, aur Full Stop (.) ya (?) par khatam hota hai.",
        formulaOrPattern = "CAPITAL Start + Naming Word + Action Word + Ending Mark (. / ?)",
        examples = listOf("The cat sat on the mat.", "A green frog can hop.", "Who has a red kite?"),
      ),
    )

  // Level 5 · Class 2 Grammar, Opposites & Rhymes Rules
  val class2Rules: List<GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Noun",
        titleEn = "Noun (Naming Word)",
        titleHinglish = "Noun Rule (Naam Wale Shabd)",
        ruleEn = "A Noun is the name of any person, place, animal, bird, or thing.",
        ruleHinglish = "Kisi bhi vyakti (person), jagah (place), jaanwar ya cheez ke naam ko Noun kehte hain.",
        formulaOrPattern = "Noun = Person / Place / Animal / Thing",
        examples = listOf("Aarav (Person)", "Delhi (Place)", "Tiger (Animal)", "Pencil (Thing)"),
      ),
      GrammarTopicRule(
        topicTag = "Verb",
        titleEn = "Verb (Action Word)",
        titleHinglish = "Verb Rule (Kaam Wale Shabd)",
        ruleEn = "A Verb shows an action or tells what someone or something does.",
        ruleHinglish = "Jo shabd kisi kaam ya action ke hone ko bataye, use Verb (Kriya) kehte hain.",
        formulaOrPattern = "Verb = What you DO (Action)",
        examples = listOf("Birds fly in the sky.", "Riya reads a book.", "Puppies run and jump."),
      ),
      GrammarTopicRule(
        topicTag = "Adjective",
        titleEn = "Adjective (Describing Word)",
        titleHinglish = "Adjective Rule (Gun / Rang / Size)",
        ruleEn = "An Adjective describes a noun by telling its colour, size, number, shape, or quality.",
        ruleHinglish = "Jo shabd Noun ki visheshata (rang, size, ginti ya gun) bataye, use Adjective kehte hain.",
        formulaOrPattern = "Adjective + Noun (Kaisa / Kitna?)",
        examples = listOf("red apple (Colour)", "tall tree (Size)", "sweet mango (Taste)", "two stars (Number)"),
      ),
      GrammarTopicRule(
        topicTag = "Opposites & Rhymes",
        titleEn = "Antonyms (Opposites) & Rhyming Rule",
        titleHinglish = "Vilom (Opposites) aur Rhyming Rules",
        ruleEn = "Opposites have completely contrasting meanings; rhyming words share the exact same ending sound.",
        ruleHinglish = "Opposites ka matlab ek-doosre se bilkul ulta hota hai; Rhyming words ki aakhir ki awaaz ek jaisi hoti hai.",
        formulaOrPattern = "Opposite: A ↔ Not-A  |  Rhyme: Same ending sound",
        examples = listOf("big ↔ small", "hot ↔ cold", "day ↔ night", "cat – bat – hat (Rhyme)"),
      ),
    )

  // Level 6 · Class 3 Vowel Teams, Affixes & Tenses Rules
  val class3VowelRule =
    GrammarTopicRule(
      topicTag = "Vowel Teams",
      titleEn = "Vowel Team & Bossy 'R' Rule",
      titleHinglish = "Vowel Teams aur Bossy 'R' Rules",
      ruleEn = "When two vowels team up, they work together to produce one long vowel sound; when 'r' follows a vowel, 'r' changes the vowel sound.",
      ruleHinglish = "Jab do vowels saath aate hain toh milkar lambi awaaz banate hain; aur vowel ke baad 'r' aaye toh awaaz 'ar/or/er' ban jaati hai.",
      formulaOrPattern = "ai/ay → ए · ee/ea → ई · oa/ow → ओ · -tion → शन",
      examples = listOf("r + ain = rain", "tr + ee = tree", "b + oat = boat", "sta + tion = station"),
    )

  val class3AffixRules: List<GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Prefix",
        titleEn = "Prefix Rule (Start Attachment)",
        titleHinglish = "Prefix Rule (Shabd ke Aage Lagna)",
        ruleEn = "A prefix is added BEFORE a root word to change its meaning (un-/dis- = opposite, re- = again, pre- = before, mis- = wrongly).",
        ruleHinglish = "Prefix root shabd ke SHURUAT mein lagta hai: un-/dis- (ulta), re- (dobara), pre- (pehle), mis- (galat).",
        formulaOrPattern = "Prefix + Root Word = New Meaning",
        examples = listOf("un + safe = unsafe", "re + play = replay", "dis + agree = disagree", "pre + test = pretest"),
      ),
      GrammarTopicRule(
        topicTag = "Suffix",
        titleEn = "Suffix Rule (End Attachment)",
        titleHinglish = "Suffix Rule (Shabd ke Peeche Lagna)",
        ruleEn = "A suffix is added AFTER a root word (-ful = full of, -less = without, -ly = how/adverb, -ness = noun state).",
        ruleHinglish = "Suffix shabd ke AAKHIR mein lagta hai: -ful (bhara), -less (bina), -ly (kaise hua), -ness (gun ka naam).",
        formulaOrPattern = "Root Word + Suffix = New Word Form",
        examples = listOf("joy + ful = joyful", "fear + less = fearless", "soft + ly = softly", "kind + ness = kindness"),
      ),
      GrammarTopicRule(
        topicTag = "Plural Rules",
        titleEn = "Singular → Plural Spelling Rules",
        titleHinglish = "Ek-Vachan se Bahu-Vachan (Plural) Rules",
        ruleEn = "Words ending in consonant + y change 'y' to '-ies'; words ending in f/fe often change to '-ves'; irregular words change spelling.",
        ruleHinglish = "Consonant + 'y' → '-ies'; aakhri 'f/fe' → '-ves'; aur kuch irregular words poora badal jaate hain.",
        formulaOrPattern = "y → -ies  |  f/fe → -ves  |  Irregular change",
        examples = listOf("city → cities", "wolf → wolves", "tooth → teeth", "child → children"),
      ),
    )

  fun ruleForAffixType(ruleType: String): GrammarTopicRule =
    when {
      ruleType.startsWith("Prefix", ignoreCase = true) -> class3AffixRules[0]
      ruleType.startsWith("Suffix", ignoreCase = true) -> class3AffixRules[1]
      else -> class3AffixRules[2]
    }

  val class3GrammarRulesByTopic: Map<String, GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Articles",
        titleEn = "Articles Rule (A, An, The)",
        titleHinglish = "Articles Rule: A, An aur The kab lagayein?",
        ruleEn = "Use 'a' before consonant sounds, 'an' before vowel sounds (even silent 'h'), and 'the' for unique things like rivers, sun, or specific nouns.",
        ruleHinglish = "Consonant awaaz se pehle 'a', vowel awaaz (a, e, i, o, u ya silent 'h') se pehle 'an', aur khaas/unique cheezon ke liye 'the' lagta hai.",
        formulaOrPattern = "a + consonant sound | an + vowel sound | the + unique/specific",
        examples = listOf("a university ('yu' sound)", "an hour (silent 'h')", "The Himalayas / The Sun"),
      ),
      GrammarTopicRule(
        topicTag = "Simple Present",
        titleEn = "Simple Present Tense Rule",
        titleHinglish = "Simple Present Tense Rule (Roz ki Aadat / Sachai)",
        ruleEn = "Add '-s' or '-es' to the base verb for singular subjects (He, She, It, one name). Use base verb for I, You, We, They.",
        ruleHinglish = "Singular subject (He/She/It/1 naam) ke saath verb mein '-s/-es' lagate hain; I/You/We/They ke saath root verb rehta hai.",
        formulaOrPattern = "He/She/It + Verb(+s/es)  |  I/We/They + Verb",
        examples = listOf("The Moon shines at night.", "Birds fly south.", "She drinks milk daily."),
      ),
      GrammarTopicRule(
        topicTag = "Simple Past",
        titleEn = "Simple Past Tense Rule (-ed)",
        titleHinglish = "Simple Past Tense Rule (Beeta Hua Kal)",
        ruleEn = "Use the past form (Verb + -ed for regular verbs) when time words like 'yesterday' or 'last week' show finished actions.",
        ruleHinglish = "Beete samay ('yesterday', 'last week') ke kaam ke liye regular verb mein '-ed' lagate hain.",
        formulaOrPattern = "Subject + Verb(V2 / -ed) + Object",
        examples = listOf("Rohan cleaned his room yesterday.", "We visited the museum last Sunday."),
      ),
      GrammarTopicRule(
        topicTag = "Simple Past (Irregular)",
        titleEn = "Irregular Past Tense Rule",
        titleHinglish = "Irregular Past Verbs Rule (No '-ed'!)",
        ruleEn = "Some verbs do NOT take '-ed' in past tense—their whole form changes (write → wrote, bring → brought, go → went).",
        ruleHinglish = "Irregular verbs mein '-ed' nahi lagta, balki verb ka roop badal jaata hai!",
        formulaOrPattern = "Present → Irregular Past (No -ed)",
        examples = listOf("buy → bought", "sing → sang", "eat → ate", "write → wrote"),
      ),
      GrammarTopicRule(
        topicTag = "Simple Future",
        titleEn = "Simple Future Tense Rule",
        titleHinglish = "Simple Future Tense Rule (Aane Wala Kal)",
        ruleEn = "Use 'will + base verb' to talk about actions that will happen tomorrow or later.",
        ruleHinglish = "Aane wale samay ('tomorrow', 'next month') ke liye 'will + root verb' lagate hain.",
        formulaOrPattern = "Subject + will + Base Verb (V1)",
        examples = listOf("I will read a storybook tonight.", "We will visit Grandma tomorrow."),
      ),
      GrammarTopicRule(
        topicTag = "Helping Verbs",
        titleEn = "Helping Verbs Rule (is / am / are)",
        titleHinglish = "Helping Verbs Rule: is, am, are",
        ruleEn = "Use 'am' with I, 'is' with singular nouns/he/she/it, and 'are' with plural nouns/you/we/they.",
        ruleHinglish = "'I' ke saath 'am', 1 vyakti/cheez ke saath 'is', aur ek se zyada (plural) ke saath 'are' lagta hai.",
        formulaOrPattern = "I → am  |  Singular → is  |  Plural/You → are",
        examples = listOf("I am ready.", "The flower is fresh.", "Stars are glowing."),
      ),
      GrammarTopicRule(
        topicTag = "Pronouns",
        titleEn = "Subject vs Object Pronoun Rule",
        titleHinglish = "Object Pronoun Rule",
        ruleEn = "Use subject pronouns (I, she, he, they) before a verb, and object pronouns (me, her, him, them) after a verb or preposition.",
        ruleHinglish = "Kaam karne wale ke liye Subject Pronoun (she/he) aur verb ke BAAD Object Pronoun (her/him) lagta hai.",
        formulaOrPattern = "Subject + Verb + Object Pronoun (him / her / me / them)",
        examples = listOf("Riya helped him.", "Father gave her a kite.", "Tell them the truth."),
      ),
      GrammarTopicRule(
        topicTag = "Present Continuous",
        titleEn = "Present Continuous Rule (is/are + -ing)",
        titleHinglish = "Present Continuous Rule (Abhi Chal Raha Kaam)",
        ruleEn = "Use 'is / am / are + Verb-ing' for actions happening right now at this moment.",
        ruleHinglish = "Jo kaam bilkul abhi ho raha hai, uske liye 'is/am/are + Verb-ing' use karte hain.",
        formulaOrPattern = "Subject + is/am/are + Verb + -ing",
        examples = listOf("She is painting a lotus.", "They are watering plants now."),
      ),
      GrammarTopicRule(
        topicTag = "Subject-Verb Match",
        titleEn = "Subject-Verb Agreement Rule",
        titleHinglish = "Subject-Verb Agreement Rule",
        ruleEn = "Words like 'Every' and 'Each' always act as singular subjects, so they take singular verbs like 'has', 'is', or verb+'s'.",
        ruleHinglish = "'Every' aur 'Each' dekhne mein sabko lagte hain par grammar mein SINGULAR hote hain — isliye 'has' / 'is' aata hai!",
        formulaOrPattern = "Every / Each + Singular Noun + Singular Verb (has / is)",
        examples = listOf("Each boy has a cap.", "Every desk is neat."),
      ),
    ).associateBy { it.topicTag }

  // Level 7 · Class 4 Vocab & Mechanics Rules
  val class4VocabRulesByCategory: Map<String, GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Synonym",
        titleEn = "Synonym Rule (Similar Meaning)",
        titleHinglish = "Synonym Rule (Samaan Arth Wale Shabd)",
        ruleEn = "Synonyms are different words that share the same or nearly the same meaning. Replace boring words with rich synonyms!",
        ruleHinglish = "Synonym alag spelling wale shabd hote hain jinka matlab ek jaisa (similar) hota hai.",
        formulaOrPattern = "Word A ≈ Word B (Same Meaning)",
        examples = listOf("big ≈ huge", "happy ≈ joyful", "fast ≈ swift", "quiet ≈ silent"),
      ),
      GrammarTopicRule(
        topicTag = "Antonym",
        titleEn = "Antonym Rule (Opposite Meaning)",
        titleHinglish = "Antonym Rule (Vilom / Ulta Arth)",
        ruleEn = "Antonyms are word pairs with completely opposite meanings.",
        ruleHinglish = "Antonym un shabdon ki jodi hai jinke matlab ek-doosre ke bilkul ulte hote hain.",
        formulaOrPattern = "Word A ↔ Word B (Opposite Meaning)",
        examples = listOf("ancient ↔ modern", "polite ↔ rude", "laugh ↔ cry", "bright ↔ dim"),
      ),
      GrammarTopicRule(
        topicTag = "Homophone",
        titleEn = "Homophone Rule (Same Sound, Different Meaning)",
        titleHinglish = "Homophone Rule (Ek Awaaz, Alag Spelling)",
        ruleEn = "Homophones sound identical when spoken aloud, but have different spellings and meanings—check the sentence context!",
        ruleHinglish = "Homophones bolne mein bilkul ek jaise lagte hain par unki spelling aur matlab alag hote hain — vaakya padhkar pehchanein!",
        formulaOrPattern = "Same Sound = Different Spelling + Different Meaning",
        examples = listOf("see (dekhna) vs sea (samudra)", "two (2) vs too (bhi)", "here vs hear"),
      ),
    ).associateBy { it.topicTag }

  val class4MechanicsRulesByTopic: Map<String, GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Preposition",
        titleEn = "Prepositions Rule (Position & Time)",
        titleHinglish = "Preposition Rule (Jagah aur Samay)",
        ruleEn = "'across' = side to side; 'between' = in the middle of 2; 'among' = 3 or more; 'at' = exact clock time.",
        ruleHinglish = "Ek paar se doosre paar → 'across'; 2 ke beech → 'between'; exact ghadi ke time se pehle → 'at'.",
        formulaOrPattern = "between (2) | among (3+) | at (clock time) | across (one side to other)",
        examples = listOf("walk across a street", "sit between Maya and Raj", "meet at 5:00 PM"),
      ),
      GrammarTopicRule(
        topicTag = "Conjunction",
        titleEn = "Conjunctions Rule (Joining Words)",
        titleHinglish = "Conjunction Rule (Vaakya Jodne Wale Shabd)",
        ruleEn = "'because' gives a reason; 'although' shows contrast (even though); 'so that' shows purpose.",
        ruleHinglish = "Kaaran batane ke liye 'because', ulta contrast dikhane ke liye 'although', aur uddeshya ke liye 'so that' lagta hai.",
        formulaOrPattern = "Reason → because | Contrast → although | Purpose → so that",
        examples = listOf("I stayed in because it rained.", "Although tired, he smiled.", "Study hard so that you shine."),
      ),
      GrammarTopicRule(
        topicTag = "Punctuation",
        titleEn = "End Punctuation Rule (. ? !)",
        titleHinglish = "Punctuation Marks Rule (. ? !)",
        ruleEn = "Use '.' for statements, '?' for questions, and '!' after strong feelings or exclamations starting with 'What a...' or 'How...'.",
        ruleHinglish = "Seedhi baat par '.', sawaal par '?', aur hairani/khushi ('What a...!') ke end mein '!' lagate hain.",
        formulaOrPattern = "Statement → .  |  Question → ?  |  Strong Feeling → !",
        examples = listOf("Where is my bag?", "What a lovely garden!", "The bell rang."),
      ),
      GrammarTopicRule(
        topicTag = "Punctuation (Apostrophe)",
        titleEn = "Possessive Apostrophe ('s) Rule",
        titleHinglish = "Apostrophe ('s) Adhikar Rule",
        ruleEn = "Add apostrophe + 's' ('s) to a singular noun to show belonging or ownership.",
        ruleHinglish = "Kisi ek vyakti ki cheez dikhane ke liye naam ke saath apostrophe `'s` lagate hain.",
        formulaOrPattern = "Owner + 's + Item Owned",
        examples = listOf("Riya's bicycle", "the bird's nest", "teacher's desk"),
      ),
      GrammarTopicRule(
        topicTag = "Punctuation (Comma)",
        titleEn = "Comma (,) in a List Rule",
        titleHinglish = "List mein Comma (,) Lagane ka Rule",
        ruleEn = "Place commas between three or more items in a series to keep them clearly separated.",
        ruleHinglish = "Jab 3 ya zyada cheezon ki list likhein, toh unke beech comma (,) lagate hain.",
        formulaOrPattern = "Item 1, Item 2, and Item 3",
        examples = listOf("red, blue, and yellow kites", "pens, erasers, and rulers"),
      ),
      GrammarTopicRule(
        topicTag = "Adverb",
        titleEn = "Adverb of Manner Rule (-ly)",
        titleHinglish = "Adverb Rule (Kaam Kaise Hua?)",
        ruleEn = "An adverb describes HOW a verb (action) happens. Many adverbs are formed by adding '-ly' to an adjective.",
        ruleHinglish = "Verb (kaam) KAISE hua yeh Adverb batata hai — aksar adjective mein '-ly' lagakar banta hai.",
        formulaOrPattern = "Verb + Adverb (Adjective + -ly)",
        examples = listOf("spoke politely", "ran swiftly", "sang sweetly"),
      ),
      GrammarTopicRule(
        topicTag = "Comparatives",
        titleEn = "Comparative Degree Rule (-er + than)",
        titleHinglish = "Comparative Rule (2 Cheezon ki Tulna)",
        ruleEn = "When comparing TWO people or things, add '-er' (or use 'more') followed by 'than'.",
        ruleHinglish = "Jab 2 cheezon ki tulna (compare) karein, toh '-er' + 'than' use karte hain.",
        formulaOrPattern = "Noun A + is + Adjective(-er) + than + Noun B",
        examples = listOf("A cheetah is faster than a deer.", "Today is warmer than yesterday."),
      ),
      GrammarTopicRule(
        topicTag = "Superlatives",
        titleEn = "Superlative Degree Rule (the + -est)",
        titleHinglish = "Superlative Rule (Sabse Badhkar / Highest)",
        ruleEn = "When comparing THREE or more items to show the top degree, use 'the' before the '-est' form.",
        ruleHinglish = "Poore group mein sabse zyada dikhane ke liye 'the' ke saath '-est' form lagate hain.",
        formulaOrPattern = "the + Adjective(-est) (in a group)",
        examples = listOf("the tallest tower", "the brightest star", "the longest river"),
      ),
    ).associateBy { it.topicTag }

  // Level 8 · Class 5 Idioms, Voice & Figures of Speech Rules
  val class5IdiomRulesByType: Map<String, GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Idiom",
        titleEn = "Idiom Rule (Special Figurative Phrase)",
        titleHinglish = "Idiom (Muhavara) Rule",
        ruleEn = "An idiom has a special figurative meaning different from the literal meaning of each individual word.",
        ruleHinglish = "Idiom (Muhavare) ka matlab uske shabdon ke seedhe arth se alag aur khaas hota hai.",
        formulaOrPattern = "Phrase ≠ Literal Word Meaning",
        examples = listOf("under the weather = feeling unwell", "spill the beans = reveal a secret"),
      ),
      GrammarTopicRule(
        topicTag = "Proverb",
        titleEn = "Proverb Rule (Wise Saying)",
        titleHinglish = "Proverb (Kahawat / Gyaan ki Baat) Rule",
        ruleEn = "A proverb is a well-known traditional saying that shares practical wisdom or a life lesson.",
        ruleHinglish = "Proverb (Kahawat) ek mashhoor vaakya hota hai jo humein jeevan ki seekh ya sachai batata hai.",
        formulaOrPattern = "Short Traditional Saying = Moral Lesson",
        examples = listOf("Honesty is the best policy.", "Slow and steady wins the race."),
      ),
      GrammarTopicRule(
        topicTag = "Simile",
        titleEn = "Simile Rule (Comparison using 'as' / 'like')",
        titleHinglish = "Simile Rule ('as' ya 'like' se Tulna)",
        ruleEn = "A simile compares two different things directly using the cue words 'as' or 'like'.",
        ruleHinglish = "Simile do alag cheezon ki tulna 'as' ya 'like' shabd lagakar karta hai.",
        formulaOrPattern = "as + Quality + as  OR  Verb + like + Noun",
        examples = listOf("as white as snow", "swims like a dolphin"),
      ),
    ).associateBy { it.topicTag }

  val class5VoiceAndFigureRulesByTag: Map<String, GrammarTopicRule> =
    listOf(
      GrammarTopicRule(
        topicTag = "Active → Passive",
        titleEn = "Active to Passive Voice Rule",
        titleHinglish = "Active se Passive Voice Badalne ka Rule",
        ruleEn = "Move the Object to the front, add helping verb (is/are/was/were) + Past Participle (V3) + 'by' + original Subject.",
        ruleHinglish = "Object ko aage laayein + is/are/was/were + Verb ka 3rd form (V3) + 'by' + Subject.",
        formulaOrPattern = "Object + is/are/was/were + Verb(V3) + by + Doer",
        examples = listOf("Active: Maya baked a cake.", "Passive: A cake was baked by Maya."),
      ),
      GrammarTopicRule(
        topicTag = "Passive → Active",
        titleEn = "Passive to Active Voice Rule",
        titleHinglish = "Passive se Active Voice Banane ka Rule",
        ruleEn = "Put the doer (after 'by') first as the Subject, match the verb tense cleanly, and place the receiver as the Object.",
        ruleHinglish = "'by' ke baad wale doer ko shuruat mein Subject banayein, sahi tense ka verb rakhein, aur receiver ko baad mein.",
        formulaOrPattern = "Doer + Action Verb (matching tense) + Object",
        examples = listOf("Passive: The bell was rung by the monitor.", "Active: The monitor rang the bell."),
      ),
      GrammarTopicRule(
        topicTag = "Simile vs Metaphor",
        titleEn = "Metaphor Rule (No 'like' / 'as')",
        titleHinglish = "Metaphor Rule (Seedhi Tulna Bina 'like/as' ke)",
        ruleEn = "A Metaphor states one thing IS another symbolically—WITHOUT using 'like' or 'as' (unlike a Simile).",
        ruleHinglish = "Jab bina 'like' ya 'as' ke ek cheez ko doosri cheez hi bata diya jaaye ('IS'), use Metaphor kehte hain!",
        formulaOrPattern = "A IS B (Direct symbolic match, NO like/as)",
        examples = listOf("The classroom was a buzzing beehive.", "Books are wings for the mind."),
      ),
      GrammarTopicRule(
        topicTag = "Simile",
        titleEn = "Simile Rule ('like' / 'as')",
        titleHinglish = "Simile Pehchanne ka Rule ('like' / 'as')",
        ruleEn = "Look for 'like' or 'as' comparing two unlike things that share a vivid quality.",
        ruleHinglish = "Sentence mein 'like' ya 'as' dikhe jo do alag cheezon ki tulna kare, toh woh Simile hai.",
        formulaOrPattern = "Thing A + like / as + Thing B",
        examples = listOf("Clouds floated like cotton pillows.", "He ran as fast as the wind."),
      ),
      GrammarTopicRule(
        topicTag = "Personification",
        titleEn = "Personification Rule",
        titleHinglish = "Personification Rule (Nature ko Insaani Gun)",
        ruleEn = "Personification gives human actions, feelings, or speech to non-human objects, plants, or weather.",
        ruleHinglish = "Jab bejaan cheezon, hawa, ya pedon ko insaanon jaise kaam (whisper, dance, smile) karte dikhayein, woh Personification hai.",
        formulaOrPattern = "Non-Human Object + Human Action / Feeling",
        examples = listOf("The stars winked in the night.", "The angry wind howled outside."),
      ),
      GrammarTopicRule(
        topicTag = "Alliteration",
        titleEn = "Alliteration Rule (Repeated Starting Sound)",
        titleHinglish = "Alliteration Rule (Ek Hi Shuruati Awaaz)",
        ruleEn = "Alliteration repeats the same beginning consonant sound in nearby words to create rhythm.",
        ruleHinglish = "Paas-paas ke shabdon ka ek hi starting sound se shuru hona (Anupras) Alliteration kehlata hai.",
        formulaOrPattern = "Same Starting Sound + Same Starting Sound...",
        examples = listOf("Peter Piper picked peppers.", "Busy bees buzzed brightly."),
      ),
      GrammarTopicRule(
        topicTag = "Direct → Indirect Speech",
        titleEn = "Direct to Indirect (Reported) Speech Rule",
        titleHinglish = "Indirect (Reported) Speech ka Rule",
        ruleEn = "Remove quotation marks, add 'that', shift pronouns (I → he/she), and move present-tense verbs one step back into past tense.",
        ruleHinglish = "Quotation marks hata kar 'that' lagayein, pronoun badlein ('I' → 'she/he'), aur present verb ko past tense banayein.",
        formulaOrPattern = "Speaker + said that + Shifted Pronoun + Past Verb",
        examples = listOf("Direct: Raj said, 'I play chess.'", "Indirect: Raj said that he played chess."),
      ),
      GrammarTopicRule(
        topicTag = "Modal Verbs",
        titleEn = "Polite Modal Verbs Rule (May / Can / Must)",
        titleHinglish = "Modal Verbs Rule (May, Can, Must)",
        ruleEn = "Use 'May' for formal polite permission, 'Can' for ability, and 'Must' for strong duty or rules.",
        ruleHinglish = "Vinamra permission maangne ke liye 'May', shamta (ability) ke liye 'Can', aur zaroori niyam ke liye 'Must' lagta hai.",
        formulaOrPattern = "May I...? (Polite) | Can (Ability) | Must (Duty)",
        examples = listOf("May I enter the class?", "I can swim well.", "We must wear helmets."),
      ),
      GrammarTopicRule(
        topicTag = "Conditionals",
        titleEn = "First Conditional Rule (If + Present → Will)",
        titleHinglish = "First Conditional Rule (Shart aur Nateeja)",
        ruleEn = "For real future possibilities, pair 'If + Simple Present Tense' in the condition with 'will + base verb' in the result.",
        ruleHinglish = "'If + Present Tense' wali shart ke saath nateeje mein hamesha 'will + base verb' aata hai.",
        formulaOrPattern = "If + Subject + Present Verb, ... will + Base Verb",
        examples = listOf("If you study daily, you will succeed.", "If it rains, we will stay inside."),
      ),
      GrammarTopicRule(
        topicTag = "Relative Pronoun",
        titleEn = "Relative Pronouns Rule (Who / Which / Whose)",
        titleHinglish = "Relative Pronoun Rule (Who vs Which)",
        ruleEn = "Use 'who' to refer to people, 'which' for animals or things, and 'whose' to show belonging.",
        ruleHinglish = "Insaan ko jodne ke liye 'who', cheezon/jaanwaron ke liye 'which', aur maalikana haq ke liye 'whose' lagate hain.",
        formulaOrPattern = "Person → who | Thing/Animal → which | Belonging → whose",
        examples = listOf("The girl who won the prize...", "The book which has maps..."),
      ),
    ).associateBy { it.topicTag }
}
