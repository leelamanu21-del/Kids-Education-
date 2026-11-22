package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import com.example.domain.model.LanguageMode
import com.example.domain.model.PhonicsLetter
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Natural Indian-teacher phonics audio engine.
 * Prioritizes Google Speech Services HD neural (-network) Indian English (en-IN) & Hindi (hi-IN)
 * voices with slow-motion YouTube-teacher pacing, warm pitch, and segmented breathing pauses.
 */
class PhonicsAudioPlayer(context: Context) : TextToSpeech.OnInitListener {

  private data class SpeechSegment(
    val text: String,
    val speechRate: Float,
    val pauseAfterMs: Long = 0L,
  )

  private data class PendingSpeech(
    val segments: List<SpeechSegment>,
    val locale: Locale,
    val fallbackSegments: List<SpeechSegment>? = null,
    val fallbackLocale: Locale = Locale.forLanguageTag("en-IN"),
  )

  private val appContext = context.applicationContext
  private var tts: TextToSpeech? = TextToSpeech(appContext, this)
  @Volatile private var isReady = false
  @Volatile private var triedGoogleEngine = false
  @Volatile private var revertedToDefaultEngine = false
  @Volatile private var pendingSpeech: PendingSpeech? = null

  private val indianEnglishLocale = Locale.forLanguageTag("en-IN")
  private val hindiLocale = Locale.forLanguageTag("hi-IN")

  private val speechAudioAttributes: AudioAttributes by lazy {
    AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_MEDIA)
      .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
      .build()
  }

  private val ttsParams: Bundle by lazy {
    Bundle().apply {
      putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
      putString(TextToSpeech.Engine.KEY_FEATURE_NETWORK_RETRIES_COUNT, "2")
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val engine = tts
      if (!triedGoogleEngine && engine != null) {
        val hasGoogleTts =
          runCatching { engine.engines }
            .getOrNull()
            .orEmpty()
            .any { it.name == GOOGLE_TTS_PACKAGE }
        if (hasGoogleTts && engine.defaultEngine != GOOGLE_TTS_PACKAGE) {
          triedGoogleEngine = true
          isReady = false
          engine.shutdown()
          tts = TextToSpeech(appContext, this, GOOGLE_TTS_PACKAGE)
          return
        }
      }

      isReady = true
      engine?.setAudioAttributes(speechAudioAttributes)
      engine?.setPitch(DEFAULT_PITCH)
      engine?.setSpeechRate(SENTENCE_SPEECH_RATE)
      pendingSpeech?.let { request ->
        pendingSpeech = null
        speakSegmentsInternal(
          segments = request.segments,
          preferredLocale = request.locale,
          fallbackSegments = request.fallbackSegments,
          fallbackLocale = request.fallbackLocale,
        )
      }
    } else if (triedGoogleEngine && !revertedToDefaultEngine) {
      revertedToDefaultEngine = true
      isReady = false
      tts?.shutdown()
      tts = TextToSpeech(appContext, this)
    } else {
      Log.w(TAG, "TextToSpeech init failed with status=$status")
    }
  }

  fun speakLetterPhoneme(letter: PhonicsLetter, languageMode: LanguageMode) {
    val englishSound = spokenEnglishSound(letter)
    val englishSegments =
      listOf(
        SpeechSegment(text = "${letter.letter}.", speechRate = PHONEME_SPEECH_RATE, pauseAfterMs = 280L),
        SpeechSegment(
          text = "${letter.letter} says, $englishSound.",
          speechRate = WORD_SPEECH_RATE,
          pauseAfterMs = 260L,
        ),
        SpeechSegment(text = "as in, ${letter.exampleWord}.", speechRate = WORD_SPEECH_RATE),
      )

    when (languageMode) {
      LanguageMode.HINGLISH -> {
        val hindiSegments = buildHinglishLetterSegments(letter)
        speakSegmentsInternal(
          segments = hindiSegments,
          preferredLocale = hindiLocale,
          fallbackSegments = englishSegments,
          fallbackLocale = indianEnglishLocale,
        )
      }
      LanguageMode.ENGLISH -> {
        speakSegmentsInternal(
          segments = englishSegments,
          preferredLocale = indianEnglishLocale,
        )
      }
    }
  }

  fun speakPhonemeOnly(letter: PhonicsLetter, languageMode: LanguageMode = LanguageMode.ENGLISH) {
    val englishSound = spokenEnglishSound(letter)
    if (languageMode == LanguageMode.HINGLISH) {
      val hindiSound = hinglishPureSound(letter)
      speakSegmentsInternal(
        segments = listOf(SpeechSegment(text = "$hindiSound.", speechRate = PHONEME_SPEECH_RATE)),
        preferredLocale = hindiLocale,
        fallbackSegments = listOf(SpeechSegment(text = "$englishSound.", speechRate = PHONEME_SPEECH_RATE)),
        fallbackLocale = indianEnglishLocale,
      )
    } else {
      speakSegmentsInternal(
        segments = listOf(SpeechSegment(text = "$englishSound.", speechRate = PHONEME_SPEECH_RATE)),
        preferredLocale = indianEnglishLocale,
      )
    }
  }

  fun speakWord(word: String, languageMode: LanguageMode) {
    val cleanWord = formatWordForNaturalSpeech(word)
    speakSegmentsInternal(
      segments = listOf(SpeechSegment(text = "$cleanWord.", speechRate = WORD_SPEECH_RATE)),
      preferredLocale = indianEnglishLocale,
    )
  }

  fun speakHindiCue(cue: String) {
    val segments = splitIntoPausedSegments(cue, SENTENCE_SPEECH_RATE)
    speakSegmentsInternal(
      segments = segments,
      preferredLocale = hindiLocale,
      fallbackSegments = segments,
      fallbackLocale = indianEnglishLocale,
    )
  }

  fun speakText(text: String, locale: Locale = indianEnglishLocale) {
    val segments = splitIntoPausedSegments(text, SENTENCE_SPEECH_RATE)
    speakSegmentsInternal(
      segments = segments,
      preferredLocale = locale,
      fallbackSegments = segments,
      fallbackLocale = indianEnglishLocale,
    )
  }

  fun speakSoundToken(
    spokenSyllable: String,
    hindiSound: String? = null,
    languageMode: LanguageMode,
  ) {
    val mappedHindi = hindiSound?.takeIf { it.isNotBlank() } ?: blendHindiCue(spokenSyllable)
    if (languageMode == LanguageMode.HINGLISH && !mappedHindi.isNullOrBlank()) {
      speakSegmentsInternal(
        segments = listOf(SpeechSegment(text = "$mappedHindi.", speechRate = PHONEME_SPEECH_RATE)),
        preferredLocale = hindiLocale,
        fallbackSegments = listOf(SpeechSegment(text = "$spokenSyllable.", speechRate = PHONEME_SPEECH_RATE)),
        fallbackLocale = indianEnglishLocale,
      )
    } else {
      speakSegmentsInternal(
        segments = listOf(SpeechSegment(text = "$spokenSyllable.", speechRate = PHONEME_SPEECH_RATE)),
        preferredLocale = indianEnglishLocale,
      )
    }
  }

  fun speakBilingualScript(
    hindiDevanagariScript: String,
    englishScript: String,
    languageMode: LanguageMode,
  ) {
    val englishSegments = splitIntoPausedSegments(englishScript, SENTENCE_SPEECH_RATE)
    if (languageMode == LanguageMode.HINGLISH) {
      val hindiSegments = splitIntoPausedSegments(hindiDevanagariScript, SENTENCE_SPEECH_RATE)
      speakSegmentsInternal(
        segments = hindiSegments,
        preferredLocale = hindiLocale,
        fallbackSegments = englishSegments,
        fallbackLocale = indianEnglishLocale,
      )
    } else {
      speakSegmentsInternal(
        segments = englishSegments,
        preferredLocale = indianEnglishLocale,
      )
    }
  }

  private fun splitIntoPausedSegments(text: String, rate: Float): List<SpeechSegment> {
    val normalized = text.trim()
    if (normalized.isEmpty()) return emptyList()
    val parts =
      normalized
        .split(Regex("(?<=[.!?।])\\s+"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    if (parts.isEmpty()) return listOf(SpeechSegment(normalized, rate))
    return parts.mapIndexed { idx, part ->
      SpeechSegment(
        text = part,
        speechRate = rate,
        pauseAfterMs = if (idx < parts.lastIndex) 280L else 0L,
      )
    }
  }

  private fun speakSegmentsInternal(
    segments: List<SpeechSegment>,
    preferredLocale: Locale,
    fallbackSegments: List<SpeechSegment>? = null,
    fallbackLocale: Locale = indianEnglishLocale,
  ) {
    val engine = tts ?: return
    if (segments.isEmpty()) return
    if (!isReady) {
      pendingSpeech =
        PendingSpeech(
          segments = segments,
          locale = preferredLocale,
          fallbackSegments = fallbackSegments,
          fallbackLocale = fallbackLocale,
        )
      return
    }

    var activeSegments = segments
    var targetLocale = preferredLocale

    var selectedVoice = selectBestIndianVoice(engine, targetLocale)
    val langReady =
      if (selectedVoice != null) {
        engine.setVoice(selectedVoice)
      } else {
        engine.setLanguage(targetLocale)
      }

    if (
      langReady == TextToSpeech.LANG_MISSING_DATA ||
        langReady == TextToSpeech.LANG_NOT_SUPPORTED
    ) {
      targetLocale = fallbackLocale
      activeSegments = fallbackSegments ?: segments
      selectedVoice = selectBestIndianVoice(engine, targetLocale)
      val fallbackResult =
        if (selectedVoice != null) {
          engine.setVoice(selectedVoice)
        } else {
          engine.setLanguage(targetLocale)
        }
      if (
        fallbackResult == TextToSpeech.LANG_MISSING_DATA ||
          fallbackResult == TextToSpeech.LANG_NOT_SUPPORTED
      ) {
        engine.setLanguage(Locale.forLanguageTag("en-GB"))
      }
    }

    Log.d(
      TAG,
      "Speaking locale=$targetLocale voice=${selectedVoice?.name} netReq=${selectedVoice?.isNetworkConnectionRequired}",
    )

    engine.setAudioAttributes(speechAudioAttributes)
    engine.setPitch(DEFAULT_PITCH)

    activeSegments.forEachIndexed { idx, segment ->
      val queueMode = if (idx == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
      engine.setSpeechRate(segment.speechRate)
      val utteranceId = "phonics_${System.nanoTime()}_$idx"
      engine.speak(segment.text, queueMode, ttsParams, utteranceId)
      if (segment.pauseAfterMs > 0L) {
        engine.playSilentUtterance(
          segment.pauseAfterMs,
          TextToSpeech.QUEUE_ADD,
          "pause_${System.nanoTime()}_$idx",
        )
      }
    }
  }

  private fun selectBestIndianVoice(engine: TextToSpeech, targetLocale: Locale): Voice? {
    val voices = runCatching { engine.voices }.getOrNull().orEmpty()
    if (voices.isEmpty()) return null

    val isOnline = isNetworkAvailable()
    val targetLang = targetLocale.language.lowercase(Locale.ROOT)
    val targetCountry = targetLocale.country.uppercase(Locale.ROOT)

    return voices
      .asSequence()
      .filter { voice ->
        val voiceLang = voice.locale.language.lowercase(Locale.ROOT)
        if (voiceLang != targetLang) return@filter false
        val features = voice.features.orEmpty()
        if (features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)) return@filter false
        if (voice.isNetworkConnectionRequired && !isOnline) return@filter false
        true
      }
      .maxByOrNull { voice ->
        val name = voice.name.lowercase(Locale.ROOT)
        val voiceCountry = voice.locale.country.uppercase(Locale.ROOT)
        var score = 0

        if (voiceCountry == targetCountry) {
          score += 3000
        } else if (targetLang == "en" && voiceCountry == "GB") {
          score += 350
        }

        // Strongly prefer studio-quality Neural (-network) voices when online instead of robotic -local TTS
        val isNeuralNetworkVoice = voice.isNetworkConnectionRequired || "-network" in name
        if (isOnline && isNeuralNetworkVoice) {
          score += 1500
        } else if (!isNeuralNetworkVoice && voice.quality >= Voice.QUALITY_HIGH) {
          score += 120
        }

        score +=
          when (voice.quality) {
            Voice.QUALITY_VERY_HIGH -> 500
            Voice.QUALITY_HIGH -> 350
            Voice.QUALITY_NORMAL -> 150
            Voice.QUALITY_LOW -> 20
            else -> 0
          }

        if ("-x-" in name) score += 280
        if (name.endsWith("-language")) score -= 300
        if (WARM_FEMALE_VOICE_HINTS.any { it in name }) score += 260
        if (NATURAL_INDIAN_VOICE_HINTS.any { it in name }) score += 140
        score
      }
  }

  private fun isNetworkAvailable(): Boolean {
    return runCatching {
      val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        ?: return false
      val network = cm.activeNetwork ?: return false
      val caps = cm.getNetworkCapabilities(network) ?: return false
      caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }.getOrDefault(false)
  }

  private fun formatWordForNaturalSpeech(word: String): String {
    val trimmed = word.trim()
    if (trimmed.isEmpty()) return trimmed
    // Prevent uppercase CVC words like "SAT", "PIN", "NAP" from being spelled letter-by-letter.
    val hasMultipleLetters = trimmed.count { it.isLetter() } >= 2
    val isAllCaps = trimmed.none { it.isLowerCase() }
    return if (hasMultipleLetters && isAllCaps) {
      trimmed.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }
    } else {
      trimmed
    }
  }

  internal fun spokenEnglishSound(letter: PhonicsLetter): String =
    spokenEnglishSoundForLetter(letter.letter, letter.phonemeSpeech)

  internal fun hinglishPureSound(letter: PhonicsLetter): String =
    hinglishPureSoundForLetter(letter.letter, letter.phonemeSpeech)

  private fun blendHindiCue(spokenSyllable: String): String? =
    when (spokenSyllable.lowercase(Locale.ROOT)) {
      "blah", "bluh" -> "ब्ल"
      "grah", "gruh" -> "ग्र"
      "stah", "stuh" -> "स्ट"
      "frah", "fuh", "fruh" -> "फ़्र"
      "klah", "kluh" -> "क्ल"
      "trah", "truh" -> "ट्र"
      "brah", "bruh" -> "ब्र"
      "krah", "kruh" -> "क्र"
      "drah", "druh" -> "ड्र"
      "flah", "fluh" -> "फ़्ल"
      "plah", "pluh" -> "प्ल"
      "spah", "spuh" -> "स्प"
      else -> null
    }

  private fun buildHinglishLetterSegments(letter: PhonicsLetter): List<SpeechSegment> {
    val sound = hinglishPureSound(letter)
    val spokenLetterName = hindiSpokenLetterName(letter.letter)
    val englishWord = letter.exampleWord
    return listOf(
      SpeechSegment(text = "$sound.", speechRate = PHONEME_SPEECH_RATE, pauseAfterMs = 260L),
      SpeechSegment(
        text = "$spokenLetterName की आवाज़ होती है, $sound.",
        speechRate = SENTENCE_SPEECH_RATE,
        pauseAfterMs = 220L,
      ),
      SpeechSegment(
        text = "जैसे, $englishWord.",
        speechRate = WORD_SPEECH_RATE,
      ),
    )
  }

  private fun hindiSpokenLetterName(letter: String): String =
    when (letter.uppercase(Locale.ROOT)) {
      "A" -> "ए"
      "B" -> "बी"
      "C" -> "सी"
      "D" -> "डी"
      "E" -> "ई"
      "F" -> "एफ़"
      "G" -> "जी"
      "H" -> "एच"
      "I" -> "आई"
      "J" -> "जे"
      "K" -> "के"
      "L" -> "एल"
      "M" -> "एम"
      "N" -> "एन"
      "O" -> "ओ"
      "P" -> "पी"
      "Q" -> "क्यू"
      "R" -> "आर"
      "S" -> "एस"
      "T" -> "टी"
      "U" -> "यू"
      "V" -> "वी"
      "W" -> "डब्ल्यू"
      "X" -> "एक्स"
      "Y" -> "वाई"
      "Z" -> "ज़ेड"
      else -> letter
    }

  fun playPopSound() {
    playToneSequence(listOf(523.25 to 65, 659.25 to 85), volume = 0.22f)
  }

  fun playCelebrationChime() {
    playToneSequence(
      listOf(523.25 to 90, 659.25 to 90, 783.99 to 105, 1046.50 to 200),
      volume = 0.26f,
    )
  }

  fun playGentleTryAgain() {
    playToneSequence(listOf(330.0 to 100, 293.66 to 140), volume = 0.16f)
  }

  private fun playToneSequence(notes: List<Pair<Double, Int>>, volume: Float) {
    thread(start = true, isDaemon = true) {
      try {
        val sampleRate = 22050
        val fadeInSamples = (sampleRate * 4) / 1000
        val totalSamples = notes.sumOf { (_, ms) -> (sampleRate * ms) / 1000 }
        val pcm = ShortArray(totalSamples)
        var offset = 0
        for ((freq, ms) in notes) {
          val count = (sampleRate * ms) / 1000
          for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val attack = if (i < fadeInSamples) i.toDouble() / fadeInSamples else 1.0
            val decay = exp(-4.8 * i.toDouble() / count.coerceAtLeast(1))
            val tone = sin(2.0 * PI * freq * t) + 0.20 * sin(4.0 * PI * freq * t)
            val wave = (tone / 1.2) * attack * decay * volume
            pcm[offset + i] =
              (wave * Short.MAX_VALUE)
                .toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
          }
          offset += count
        }

        val track =
          AudioTrack.Builder()
            .setAudioAttributes(
              AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            )
            .setAudioFormat(
              AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
            )
            .setBufferSizeInBytes(pcm.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcm, 0, pcm.size)
        track.play()
        val totalMs = notes.sumOf { it.second }.toLong() + 80L
        Thread.sleep(totalMs)
        track.release()
      } catch (e: Exception) {
        Log.d(TAG, "Audio chime skipped: ${e.message}")
      }
    }
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
    isReady = false
  }

  internal companion object {
    const val TAG = "PhonicsAudioPlayer"
    const val GOOGLE_TTS_PACKAGE = "com.google.android.tts"
    const val DEFAULT_PITCH = 0.96f
    const val SENTENCE_SPEECH_RATE = 0.76f
    const val WORD_SPEECH_RATE = 0.70f
    const val PHONEME_SPEECH_RATE = 0.72f
    val WARM_FEMALE_VOICE_HINTS =
      listOf("ena", "enc", "ene", "hia", "hic", "hie", "female", "woman")
    val NATURAL_INDIAN_VOICE_HINTS =
      listOf("end", "ahp", "hid", "en-in", "hi-in")

    internal fun spokenEnglishSoundForLetter(letter: String, fallback: String): String =
      when (letter.uppercase(Locale.ROOT)) {
        "S" -> "sss"
        "A" -> "ah"
        "T" -> "tah"
        "P" -> "pah"
        "I" -> "ih"
        "N" -> "nnn"
        "C" -> "kah"
        "K" -> "kah"
        "E" -> "eh"
        "H" -> "hah"
        "R" -> "rrr"
        "M" -> "mmm"
        "D" -> "dah"
        "G" -> "gah"
        "O" -> "aw"
        "U" -> "uh"
        "L" -> "lll"
        "F" -> "fff"
        "B" -> "bah"
        "J" -> "jah"
        "Z" -> "zzz"
        "W" -> "wah"
        "V" -> "vvv"
        "Y" -> "yah"
        "X" -> "eks"
        "Q" -> "kwah"
        else -> fallback
      }

    internal fun hinglishPureSoundForLetter(letter: String, fallback: String): String =
      when (letter.uppercase(Locale.ROOT)) {
        "S" -> "स"
        "A" -> "ऐ"
        "T" -> "ट"
        "P" -> "प"
        "I" -> "इ"
        "N" -> "न"
        "C" -> "क"
        "K" -> "क"
        "E" -> "ए"
        "H" -> "ह"
        "R" -> "र"
        "M" -> "म"
        "D" -> "ड"
        "G" -> "ग"
        "O" -> "ऑ"
        "U" -> "अ"
        "L" -> "ल"
        "F" -> "फ़"
        "B" -> "ब"
        "J" -> "ज"
        "Z" -> "ज़"
        "W" -> "व"
        "V" -> "व"
        "Y" -> "य"
        "X" -> "क्स"
        "Q" -> "क्व"
        else -> fallback
      }

    internal fun buildHinglishExampleClause(letter: PhonicsLetter): String =
      "जैसे, ${letter.exampleWord}."
  }
}
