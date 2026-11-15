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
}
