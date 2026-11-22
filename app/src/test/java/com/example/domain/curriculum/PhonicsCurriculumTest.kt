package com.example.domain.curriculum

import com.example.domain.model.LearnerProfile
import com.example.domain.model.LearningMode
import com.example.feature.parent.buildWhatsAppReportMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhonicsCurriculumTest {

  @Test
  fun curriculum_hasAll26Letters_withUniqueSatpinAndAzOrders() {
    assertEquals(26, PhonicsCurriculum.letters.size)
    assertEquals((1..26).toSet(), PhonicsCurriculum.letters.map { it.satpinOrder }.toSet())
    assertEquals((1..26).toSet(), PhonicsCurriculum.letters.map { it.azOrder }.toSet())
    assertEquals(('A'..'Z').map { it.toString() }.toSet(), PhonicsCurriculum.letters.map { it.letter }.toSet())
  }

  @Test
  fun satpinMode_startsWithSatpinFoundation() {
    val firstSix = PhonicsCurriculum.lettersForMode(LearningMode.SATPIN).take(6).map { it.letter }
    assertEquals(listOf("S", "A", "T", "P", "I", "N"), firstSix)
  }

  @Test
  fun cvcWords_referenceValidCurriculumLetters() {
    PhonicsCurriculum.cvcWords.forEach { cvc ->
      assertEquals(cvc.word.length, cvc.phonemes.size)
      cvc.phonemes.forEach { phoneme ->
        assertNotNull("Missing curriculum letter for $phoneme", PhonicsCurriculum.findByLetter(phoneme))
      }
    }
  }

  @Test
  fun hindiSoundTag_andWhatsAppReport_formatCorrectly() {
    assertEquals("ऐ", PhonicsCurriculum.hindiSoundTag("A"))
    assertEquals("स", PhonicsCurriculum.hindiSoundTag("S"))
    val report =
      buildWhatsAppReportMessage(
        profile = LearnerProfile(childName = "Aarav", totalStars = 12L),
        masteredLetters = listOf("S", "A", "T"),
        wordsBlendedToday = 4,
        sessionDuration = "5m 20s",
      )
    assertTrue(report.contains("Aarav"))
    assertTrue(report.contains("S, A, T"))
    assertTrue(report.contains("Words Blended Today: 4"))
  }

  @Test
  fun expandedCurriculumCatalogs_meetCountAndIntegrityChecks() {
    assertTrue(PhonicsCurriculum.letters.all { it.sampleWords.size == 6 })
    assertEquals(32, PhonicsCurriculum.cvcWords.size)
    assertTrue(PhonicsCurriculum.cvcWords.all { it.word.length == 3 })

    assertEquals(24, EarlyCurriculumCatalog.sightWords.size)
    assertEquals(8, EarlyCurriculumCatalog.wordFamilies.size)
    assertEquals(9, EarlyCurriculumCatalog.digraphs.size)
    assertEquals(12, EarlyCurriculumCatalog.magicEWords.size)

    assertEquals(12, UpperCurriculumCatalog.blends.size)
    assertEquals(10, UpperCurriculumCatalog.fluencySentences.size)
    assertTrue(UpperCurriculumCatalog.fluencySentences.all { it.correctOptionIndex in it.quizOptions.indices })
    assertEquals(18, UpperCurriculumCatalog.grammarItems.size)
    assertEquals(14, UpperCurriculumCatalog.opposites.size)
    assertEquals(8, UpperCurriculumCatalog.rhymeFamilies.size)
    assertTrue(UpperCurriculumCatalog.rhymeFamilies.all { it.oddOneOut !in it.rhymingWords })
    assertEquals(5, UpperCurriculumCatalog.pictureStories.size)
    assertTrue(UpperCurriculumCatalog.pictureStories.all {
      it.blankCorrectIndex in it.blankOptions.indices &&
        it.comprehensionCorrectIndex in it.comprehensionOptions.indices
    })

    // Class 3 (Level 6)
    assertEquals(10, Class3CurriculumCatalog.vowelTeams.size)
    assertTrue(Class3CurriculumCatalog.vowelTeams.all { it.correctIndex in it.quizOptions.indices && it.sampleWords.size >= 5 })
    assertEquals(12, Class3CurriculumCatalog.affixItems.size)
    assertTrue(Class3CurriculumCatalog.affixItems.all { it.correctIndex in it.quizOptions.indices })
    assertEquals(12, Class3CurriculumCatalog.grammarQuizzes.size)
    assertTrue(Class3CurriculumCatalog.grammarQuizzes.all { it.correctIndex in it.options.indices })

    // Class 4 (Level 7)
    assertEquals(14, Class4CurriculumCatalog.vocabRelations.size)
    assertTrue(Class4CurriculumCatalog.vocabRelations.all { it.correctIndex in it.quizOptions.indices })
    assertEquals(12, Class4CurriculumCatalog.mechanicsQuizzes.size)
    assertTrue(Class4CurriculumCatalog.mechanicsQuizzes.all { it.correctIndex in it.options.indices })
    assertEquals(5, Class4CurriculumCatalog.readingPassages.size)
    assertTrue(Class4CurriculumCatalog.readingPassages.all {
      it.paragraphs.isNotEmpty() &&
        it.vocabulary.isNotEmpty() &&
        it.correctIndex1 in it.options1.indices &&
        it.correctIndex2 in it.options2.indices
    })

    // Class 5 (Level 8)
    assertEquals(12, Class5CurriculumCatalog.idiomsAndProverbs.size)
    assertTrue(Class5CurriculumCatalog.idiomsAndProverbs.all { it.correctIndex in it.quizOptions.indices })
    assertEquals(12, Class5CurriculumCatalog.voiceAndFigures.size)
    assertTrue(Class5CurriculumCatalog.voiceAndFigures.all { it.correctIndex in it.options.indices })
    assertEquals(5, Class5CurriculumCatalog.criticalPassages.size)
    assertTrue(Class5CurriculumCatalog.criticalPassages.all {
      it.paragraphs.isNotEmpty() &&
        it.vocabulary.isNotEmpty() &&
        it.correctIndex1 in it.options1.indices &&
        it.correctIndex2 in it.options2.indices
    })
  }

  @Test
  fun soundboardCatalog_coversLettersDigraphsBlendsAndVowelTeams() {
    val all = SoundboardCatalog.allTiles
    assertEquals(57, all.size)
    assertEquals(57, all.map { it.id }.toSet().size)
    assertEquals(26, SoundboardCatalog.tilesFor(SoundboardCategory.LETTERS).size)
    assertEquals(9, SoundboardCatalog.tilesFor(SoundboardCategory.DIGRAPHS).size)
    assertEquals(12, SoundboardCatalog.tilesFor(SoundboardCategory.BLENDS).size)
    assertEquals(10, SoundboardCatalog.tilesFor(SoundboardCategory.VOWEL_TEAMS).size)

    val ids = all.map { it.id }.toSet()
    assertTrue("digraph_sh" in ids)
    assertTrue("digraph_ch" in ids)
    assertTrue("digraph_th" in ids)
    assertTrue(all.all { it.examples.isNotEmpty() && it.hindiSound.isNotBlank() && it.primaryWord.isNotBlank() })
  }
}
