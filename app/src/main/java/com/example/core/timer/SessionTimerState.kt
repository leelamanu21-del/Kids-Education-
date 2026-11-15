package com.example.core.timer

enum class ScreenTimePreset(val minutes: Int, val label: String) {
  TEN_MIN(10, "10 min"),
  TWELVE_MIN(12, "12 min"),
  FIFTEEN_MIN(15, "15 min"),
}

data class SessionTimerUiState(
  val elapsedSeconds: Int = 0,
  val preset: ScreenTimePreset = ScreenTimePreset.TWELVE_MIN,
  val isBreakActive: Boolean = false,
  val bonusMinutesGranted: Int = 0,
  val wordsBlendedToday: Int = 0,
) {
  val limitSeconds: Int
    get() = (preset.minutes + bonusMinutesGranted) * 60

  val remainingSeconds: Int
    get() = (limitSeconds - elapsedSeconds).coerceAtLeast(0)

  val showGentleHeadsUp: Boolean
    get() = !isBreakActive && remainingSeconds in 1..60

  val formattedElapsedMinutes: String
    get() {
      val mins = elapsedSeconds / 60
      val secs = elapsedSeconds % 60
      return "${mins}m ${secs.toString().padStart(2, '0')}s"
    }
}
