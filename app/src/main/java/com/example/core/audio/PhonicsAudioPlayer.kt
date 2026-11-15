package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.domain.model.LanguageMode
import com.example.domain.model.PhonicsLetter
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Dual-mode audio engine combining Android TextToSpeech (English & Hindi/Hinglish locales)
 * with low-latency synthesized musical reward chimes for preschoolers.
 */
class PhonicsAudioPlayer(context: Context) : TextToSpeech.OnInitListener {

  private val appContext = context.applicationContext
  private var tts: TextToSpeech? = TextToSpeech(appContext, this)
  @Volatile private var isReady = false
  @Volatile private var pendingSpeech: Pair<String, Locale>? = null

  private val indianEnglishLocale = Locale.forLanguageTag("en-IN")
  private val hindiLocale = Locale.forLanguageTag("hi-IN")

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isReady = true
      tts?.setSpeechRate(0.84f)
      tts?.setPitch(1.12f)
      pendingSpeech?.let { (text, locale) ->
        pendingSpeech = null
        speakText(text, locale)
      }
    } else {
      Log.w(TAG, "TextToSpeech init failed with status=$status")
    }
  }

  fun speakLetterPhoneme(letter: PhonicsLetter, languageMode: LanguageMode) {
    val (script, locale) =
      when (languageMode) {
        LanguageMode.HINGLISH ->
          "${letter.phonemeSpeech}! ${letter.hinglishAudioScript}" to indianEnglishLocale
        LanguageMode.ENGLISH ->
          "${letter.phonemeSpeech}! ${letter.englishAudioScript}" to Locale.US
      }
    speakText(script, locale)
  }

  fun speakPhonemeOnly(letter: PhonicsLetter) {
    speakText(letter.phonemeSpeech, indianEnglishLocale)
  }

  fun speakWord(word: String, languageMode: LanguageMode) {
    val locale = if (languageMode == LanguageMode.HINGLISH) indianEnglishLocale else Locale.US
    speakText(word, locale)
  }

  fun speakHindiCue(cue: String) {
    val result = tts?.setLanguage(hindiLocale)
    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
      speakText(cue, indianEnglishLocale)
    } else {
      tts?.speak(cue, TextToSpeech.QUEUE_FLUSH, null, "phonics_hi_${System.nanoTime()}")
    }
  }

  fun speakText(text: String, locale: Locale = indianEnglishLocale) {
    val engine = tts ?: return
    if (!isReady) {
      pendingSpeech = text to locale
      return
    }
    val availability = engine.setLanguage(locale)
    if (availability == TextToSpeech.LANG_MISSING_DATA || availability == TextToSpeech.LANG_NOT_SUPPORTED) {
      engine.setLanguage(Locale.US)
    }
    engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "phonics_${System.nanoTime()}")
  }

  fun playPopSound() {
    playToneSequence(listOf(523.25 to 70, 659.25 to 90), volume = 0.28f)
  }

  fun playCelebrationChime() {
    playToneSequence(
      listOf(523.25 to 95, 659.25 to 95, 783.99 to 110, 1046.50 to 210),
      volume = 0.34f,
    )
  }

  fun playGentleTryAgain() {
    playToneSequence(listOf(330.0 to 110, 293.66 to 150), volume = 0.22f)
  }

  private fun playToneSequence(notes: List<Pair<Double, Int>>, volume: Float) {
    thread(start = true, isDaemon = true) {
      try {
        val sampleRate = 22050
        val totalSamples = notes.sumOf { (_, ms) -> (sampleRate * ms) / 1000 }
        val pcm = ShortArray(totalSamples)
        var offset = 0
        for ((freq, ms) in notes) {
          val count = (sampleRate * ms) / 1000
          for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-4.5 * i.toDouble() / count.coerceAtLeast(1))
            val wave = sin(2.0 * PI * freq * t) * envelope * volume
            pcm[offset + i] = (wave * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
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
  }
}
