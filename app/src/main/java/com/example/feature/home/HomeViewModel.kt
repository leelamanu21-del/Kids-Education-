package com.example.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.PhonicsProgressRepository
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerProfile
import com.example.domain.model.LearningMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HomeUiState {
  data object Loading : HomeUiState

  data class Success(
    val profile: LearnerProfile,
    val learningMode: LearningMode,
    val languageMode: LanguageMode,
    val selectedGroup: Int?,
    val freeExplore: Boolean,
    val letters: List<PhonicsLetter>,
    val progressByLetter: Map<String, LetterProgress>,
    val nextRecommendedLetter: PhonicsLetter,
    val masteredCount: Int,
  ) : HomeUiState

  data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
  private val repository: PhonicsProgressRepository,
  private val currentUserId: String,
  defaultDisplayName: String,
) : ViewModel() {

  private val selectedGroupFilter = MutableStateFlow<Int?>(null)
  private val freeExploreFlow = MutableStateFlow(false)
  private val localModeOverride = MutableStateFlow<LearningMode?>(null)
  private val localLanguageOverride = MutableStateFlow<LanguageMode?>(null)

  init {
    viewModelScope.launch {
      repository.ensureProfileExists(defaultDisplayName)
    }
  }

  val uiState: StateFlow<HomeUiState> =
    combine(
        repository.observeProfile(currentUserId),
        repository.observeLetterProgress(currentUserId),
        selectedGroupFilter,
        freeExploreFlow,
        localModeOverride,
        localLanguageOverride,
      ) { values ->
        @Suppress("UNCHECKED_CAST")
        val profile = (values[0] as LearnerProfile?) ?: LearnerProfile(
          id = currentUserId,
          userId = currentUserId,
        )
        @Suppress("UNCHECKED_CAST")
        val progressMap = values[1] as Map<String, LetterProgress>
        val groupFilter = values[2] as Int?
        val freeExplore = values[3] as Boolean
        val modeOverride = values[4] as LearningMode?
        val langOverride = values[5] as LanguageMode?

        val activeMode = modeOverride ?: LearningMode.fromStorage(profile.preferredMode)
        val activeLang = langOverride ?: LanguageMode.fromStorage(profile.preferredLanguage)
        val orderedLetters = PhonicsCurriculum.lettersForMode(activeMode)
        val visibleLetters =
          if (groupFilter == null || activeMode == LearningMode.AZ) {
            orderedLetters
          } else {
            orderedLetters.filter { it.group.groupNumber == groupFilter }
          }

        val satpinLetters = PhonicsCurriculum.lettersForMode(LearningMode.SATPIN)
        val nextLetter =
          satpinLetters.firstOrNull { progressMap[it.letter]?.mastered != true }
            ?: satpinLetters.first()
        val masteredCount = progressMap.values.count { it.mastered }

        HomeUiState.Success(
          profile = profile,
          learningMode = activeMode,
          languageMode = activeLang,
          selectedGroup = groupFilter,
          freeExplore = freeExplore,
          letters = visibleLetters,
          progressByLetter = progressMap,
          nextRecommendedLetter = nextLetter,
          masteredCount = masteredCount,
        ) as HomeUiState
      }
      .catch { error ->
        val firestoreError =
          generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing phonics progress", rootError)
        emit(HomeUiState.Error(rootError.localizedMessage ?: "Could not load learning path"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = HomeUiState.Loading,
      )

  fun setLearningMode(mode: LearningMode) {
    localModeOverride.value = mode
    if (mode == LearningMode.AZ) {
      selectedGroupFilter.value = null
    }
    val currentLang =
      localLanguageOverride.value
        ?: (uiState.value as? HomeUiState.Success)?.languageMode
        ?: LanguageMode.HINGLISH
    viewModelScope.launch { repository.updatePreferences(mode = mode, language = currentLang) }
  }

  fun toggleLanguageMode() {
    val currentState = uiState.value as? HomeUiState.Success
    val currentLang = localLanguageOverride.value ?: currentState?.languageMode ?: LanguageMode.HINGLISH
    val nextLang =
      if (currentLang == LanguageMode.HINGLISH) LanguageMode.ENGLISH else LanguageMode.HINGLISH
    localLanguageOverride.value = nextLang
    val currentMode = localModeOverride.value ?: currentState?.learningMode ?: LearningMode.SATPIN
    viewModelScope.launch { repository.updatePreferences(mode = currentMode, language = nextLang) }
  }

  fun selectGroup(groupNumber: Int?) {
    selectedGroupFilter.value = groupNumber
  }

  fun toggleFreeExplore() {
    freeExploreFlow.value = !freeExploreFlow.value
  }

  fun updateChildName(newName: String) {
    val clean = newName.trim().take(40)
    if (clean.isEmpty()) return
    val currentState = uiState.value as? HomeUiState.Success
    val mode = localModeOverride.value ?: currentState?.learningMode ?: LearningMode.SATPIN
    val lang = localLanguageOverride.value ?: currentState?.languageMode ?: LanguageMode.HINGLISH
    viewModelScope.launch {
      repository.updatePreferences(mode = mode, language = lang, childName = clean)
    }
  }

  fun completeLetterLesson(
    letter: PhonicsLetter,
    earnedStars: Int,
    quizScore: Int,
    mastered: Boolean,
  ) {
    viewModelScope.launch {
      repository.recordLetterCompletion(
        letter = letter.letter,
        satpinOrder = letter.satpinOrder,
        earnedStars = earnedStars,
        quizScore = quizScore,
        mastered = mastered,
      )
    }
  }

  private companion object {
    const val TAG = "HomeViewModel"
    const val STOP_TIMEOUT_MILLIS = 5000L
  }
}
