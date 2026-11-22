package com.example.core.audio

import com.example.domain.curriculum.EarlyCurriculumCatalog
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.curriculum.UpperCurriculumCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhonicsAudioPlayerTest {

  @Test
  fun `hinglish example clause uses English example word without Hindi translation`() {
    PhonicsCurriculum.letters.forEach { letter ->
      val clause = PhonicsAudioPlayer.buildHinglishExampleClause(letter)
      assertTrue(
        "Expected English example word ${letter.exampleWord} in '$clause'",
        clause.contains(letter.exampleWord),
      )
      assertFalse(
        "Did not expect Hindi script translation '${letter.hindiScriptWord}' in '$clause'",
        clause.contains(letter.hindiScriptWord),
      )
      if (!letter.hindiWord.equals(letter.exampleWord, ignoreCase = true)) {
        assertFalse(
          "Did not expect Hindi translation '${letter.hindiWord}' in '$clause'",
          clause.contains(letter.hindiWord, ignoreCase = true),
        )
      }
    }
  }

  @Test
  fun `satpin phoneme sounds avoid uh distortion in both English and Hinglish`() {
    assertEquals("स", PhonicsAudioPlayer.hinglishPureSoundForLetter("S", ""))
    assertEquals("ऐ", PhonicsAudioPlayer.hinglishPureSoundForLetter("A", ""))
    assertEquals("ट", PhonicsAudioPlayer.hinglishPureSoundForLetter("T", ""))

    assertEquals("sss", PhonicsAudioPlayer.spokenEnglishSoundForLetter("S", ""))
    assertEquals("ah", PhonicsAudioPlayer.spokenEnglishSoundForLetter("A", ""))
    assertEquals("tah", PhonicsAudioPlayer.spokenEnglishSoundForLetter("T", ""))

    PhonicsCurriculum.letters.forEach { letter ->
      val enSound = PhonicsAudioPlayer.spokenEnglishSoundForLetter(letter.letter, letter.phonemeSpeech)
      if (letter.letter != "U") {
        assertFalse(
          "Letter ${letter.letter} should not use '-uh' spelling '$enSound'",
          enSound.endsWith("uh"),
        )
      }
    }

    EarlyCurriculumCatalog.digraphs.forEach { digraph ->
      assertFalse(
        "Digraph ${digraph.digraph} should not use '-uh' spelling '${digraph.soundSyllable}'",
        digraph.soundSyllable.endsWith("uh"),
      )
    }

    UpperCurriculumCatalog.blends.forEach { blend ->
      assertFalse(
        "Blend ${blend.blend} should not use '-uh' spelling '${blend.spokenBlend}'",
        blend.spokenBlend.endsWith("uh"),
      )
    }
  }
}
