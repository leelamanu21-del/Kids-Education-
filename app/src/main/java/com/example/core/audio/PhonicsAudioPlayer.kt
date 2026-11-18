package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
 * Child-friendly Indian-accent audio engine combining Google Speech Services high-quality
 * Indian English (en-IN) & Hindi (hi-IN) neural/local voices with warm musical chimes.
 */
class PhonicsAudioPlayer(context: Context) : TextToSpeech.OnInitListener {

  private data class PendingSpeech(
    val text: String,
    val locale: Locale,
    val speechRate: Float,
    val fallbackText: String? = null,
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
      tts?.setPitch(DEFAULT_PITCH)
      tts?.setSpeechRate(SENTENCE_SPEECH_RATE)
      pendingSpeech?.let { request ->
        pendingSpeech = null
        speakInternal(
          text = request.text,
          preferredLocale = request.locale,
          speechRate = request.speechRate,
          fallbackText = request.fallbackText,
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
    val englishFallback =
      "$englishSound, ${letter.letter} makes the sound $englishSound, as in ${letter.exampleWord}."

    when (languageMode) {
      LanguageMode.HINGLISH -> {
        val hindiScript = hinglishVoiceScript(letter)
        speakInternal(
          text = hindiScript,
          preferredLocale = hindiLocale,
          speechRate = SENTENCE_SPEECH_RATE,
          fallbackText = englishFallback,
          fallbackLocale = indianEnglishLocale,
        )
      }
      LanguageMode.ENGLISH -> {
        speakInternal(
          text = englishFallback,
          preferredLocale = indianEnglishLocale,
          speechRate = SENTENCE_SPEECH_RATE,
        )
      }
    }
  }

  fun speakPhonemeOnly(letter: PhonicsLetter) {
    val sound = spokenEnglishSound(letter)
    speakInternal(
      text = "$sound.",
      preferredLocale = indianEnglishLocale,
      speechRate = WORD_SPEECH_RATE,
    )
  }

  fun speakWord(word: String, languageMode: LanguageMode) {
    val cleanWord = formatWordForNaturalSpeech(word)
    speakInternal(
      text = "$cleanWord.",
      preferredLocale = indianEnglishLocale,
      speechRate = WORD_SPEECH_RATE,
    )
  }

  fun speakHindiCue(cue: String) {
    speakInternal(
      text = cue,
      preferredLocale = hindiLocale,
      speechRate = SENTENCE_SPEECH_RATE,
      fallbackText = cue,
      fallbackLocale = indianEnglishLocale,
    )
  }

  fun speakText(text: String, locale: Locale = indianEnglishLocale) {
    speakInternal(
      text = text,
      preferredLocale = locale,
      speechRate = SENTENCE_SPEECH_RATE,
      fallbackText = text,
      fallbackLocale = indianEnglishLocale,
    )
  }

  fun speakSoundToken(spokenSyllable: String, hindiSound: String? = null, languageMode: LanguageMode) {
    if (languageMode == LanguageMode.HINGLISH && !hindiSound.isNullOrBlank()) {
      speakInternal(
        text = "$hindiSound.",
        preferredLocale = hindiLocale,
        speechRate = WORD_SPEECH_RATE,
        fallbackText = "$spokenSyllable.",
        fallbackLocale = indianEnglishLocale,
      )
    } else {
      speakInternal(
        text = "$spokenSyllable.",
        preferredLocale = indianEnglishLocale,
        speechRate = WORD_SPEECH_RATE,
      )
    }
  }

  fun speakBilingualScript(
    hindiDevanagariScript: String,
    englishScript: String,
    languageMode: LanguageMode,
  ) {
    if (languageMode == LanguageMode.HINGLISH) {
      speakInternal(
        text = hindiDevanagariScript,
        preferredLocale = hindiLocale,
        speechRate = SENTENCE_SPEECH_RATE,
        fallbackText = englishScript,
        fallbackLocale = indianEnglishLocale,
      )
    } else {
      speakInternal(
        text = englishScript,
        preferredLocale = indianEnglishLocale,
        speechRate = SENTENCE_SPEECH_RATE,
      )
    }
  }

  private fun speakInternal(
    text: String,
    preferredLocale: Locale,
    speechRate: Float,
    fallbackText: String? = null,
    fallbackLocale: Locale = indianEnglishLocale,
  ) {
    val engine = tts ?: return
    if (!isReady) {
      pendingSpeech =
        PendingSpeech(
          text = text,
          locale = preferredLocale,
          speechRate = speechRate,
          fallbackText = fallbackText,
          fallbackLocale = fallbackLocale,
        )
      return
    }

    var textToSpeak = text
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
      textToSpeak = fallbackText ?: text
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

    engine.setPitch(DEFAULT_PITCH)
    engine.setSpeechRate(speechRate)
    engine.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "phonics_${System.nanoTime()}")
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
          score += 2000
        } else if (targetLang == "en" && voiceCountry == "GB") {
          score += 400
        }

        score +=
          when (voice.quality) {
            Voice.QUALITY_VERY_HIGH -> 500
            Voice.QUALITY_HIGH -> 350
            Voice.QUALITY_NORMAL -> 150
            Voice.QUALITY_LOW -> 20
            else -> 0
          }

        if ("-x-" in name) score += 250
        if (name.endsWith("-language")) score -= 120
        if (FEMALE_VOICE_HINTS.any { it in name }) score += 160
        if (!voice.isNetworkConnectionRequired && voice.quality >= Voice.QUALITY_HIGH) {
          score += 60
        }
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

  private fun spokenEnglishSound(letter: PhonicsLetter): String =
    when (letter.letter.uppercase(Locale.ROOT)) {
      "S" -> "suh"
      "A" -> "aah"
      "T" -> "tuh"
      "P" -> "puh"
      "I" -> "ih"
      "N" -> "nuh"
      "C" -> "kuh"
      "K" -> "kuh"
      "E" -> "eh"
      "H" -> "huh"
      "R" -> "ruh"
      "M" -> "muh"
      "D" -> "duh"
      "G" -> "guh"
      "O" -> "aw"
      "U" -> "uh"
      "L" -> "luh"
      "F" -> "fuh"
      "B" -> "buh"
      "J" -> "juh"
      "Z" -> "zuh"
      "W" -> "wuh"
      "V" -> "vuh"
      "Y" -> "yuh"
      "X" -> "eks"
      "Q" -> "kwuh"
      else -> letter.phonemeSpeech
    }

  private fun hinglishVoiceScript(letter: PhonicsLetter): String =
    when (letter.letter.uppercase(Locale.ROOT)) {
      "S" -> "स, एस की आवाज़ है स, जैसे सूरज और Sun."
      "A" -> "ऐ, ए की आवाज़ है ऐ, जैसे चींटी और Ant."
      "T" -> "ट, टी की आवाज़ है ट, जैसे नल, Tap और Tiger."
      "P" -> "प, पी की आवाज़ है प, जैसे पतंग और Pan."
      "I" -> "इ, आई की आवाज़ है इ, जैसे Igloo और Ink."
      "N" -> "न, एन की आवाज़ है न, जैसे जाल और Net."
      "C" -> "क, सी की आवाज़ है क, जैसे प्यारी बिल्ली और Cat."
      "K" -> "क, के की आवाज़ है क, जैसे पतंग और Kite."
      "E" -> "ए, ई की आवाज़ है ए, जैसे अंडा, Egg और Elephant."
      "H" -> "ह, एच की आवाज़ है ह, जैसे टोपी और Hat."
      "R" -> "र, आर की आवाज़ है र, जैसे चूहा और Rat."
      "M" -> "म, एम की आवाज़ है म, जैसे मीठा आम और Mango."
      "D" -> "ड, डी की आवाज़ है ड, जैसे ढोल और Drum."
      "G" -> "ग, जी की आवाज़ है ग, जैसे बकरी और Goat."
      "O" -> "ऑ, ओ की आवाज़ है ऑ, जैसे संतरा और Orange."
      "U" -> "अ, यू की आवाज़ है अ, जैसे छाता और Umbrella."
      "L" -> "ल, एल की आवाज़ है ल, जैसे Lollipop और Lion."
      "F" -> "फ़, एफ़ की आवाज़ है फ़, जैसे मछली और Fish."
      "B" -> "ब, बी की आवाज़ है ब, जैसे गेंद और Ball."
      "J" -> "ज, जे की आवाज़ है ज, जैसे जग और Jam."
      "Z" -> "ज़, ज़ेड की आवाज़ है ज़, जैसे ज़ेबरा और Zoo."
      "W" -> "व, डब्ल्यू की आवाज़ है व, जैसे Web और Whale."
      "V" -> "व, वी की आवाज़ है व, जैसे Van और Violin."
      "Y" -> "य, वाई की आवाज़ है य, जैसे Yellow और Yo-Yo."
      "X" -> "क्स, एक्स की आवाज़ है क्स, जैसे Box और Fox."
      "Q" -> "क्व, क्यू की आवाज़ है क्व, जैसे रानी और Queen."
      else -> letter.hinglishAudioScript
    }

  fun playPopSound() {
    playToneSequence(listOf(523.25 to 65, 659.25 to 85), volume = 0.24f)
  }

  fun playCelebrationChime() {
    playToneSequence(
      listOf(523.25 to 90, 659.25 to 90, 783.99 to 105, 1046.50 to 200),
      volume = 0.28f,
    )
  }

  fun playGentleTryAgain() {
    playToneSequence(listOf(330.0 to 100, 293.66 to 140), volume = 0.18f)
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

  private companion object {
    const val TAG = "PhonicsAudioPlayer"
    const val GOOGLE_TTS_PACKAGE = "com.google.android.tts"
    const val DEFAULT_PITCH = 1.02f
    const val SENTENCE_SPEECH_RATE = 0.92f
    const val WORD_SPEECH_RATE = 0.88f
    val FEMALE_VOICE_HINTS =
      listOf("ena", "enc", "ene", "ahp", "hia", "hic", "hie", "female", "woman")
  }
}
