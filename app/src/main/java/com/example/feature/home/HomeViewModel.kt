package com.example.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.PhonicsProgressRepository
import com.example.domain.curriculum.BadgeCatalog
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.BadgeProgress
import com.example.domain.model.DigitalBadge
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerProfile
import com.example.domain.model.LearnerRank
import com.example.domain.model.LearningMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.example.domain.model.StageProgress
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val stageProgressById: Map<String, StageProgress>,
    val badgeProgress: List<BadgeProgress>,
    val unlockedBadgeCount: Int,
    val learnerRank: LearnerRank,
    val nextRecommendedLetter: PhonicsLetter,
    val masteredCount: Int,
  ) : HomeUiState

  data class Error(val message: String) : HomeUiState
}

private data class LocalUiFilterState(
  val selectedGroup: Int?,
  val freeExplore: Boolean,
  val modeOverride: LearningMode?,
  val langOverride: LanguageMode?,
)

class HomeViewModel(
  private val repository: PhonicsProgressRepository,
  private val currentUserId: String,
  defaultDisplayName: String,
) : ViewModel() {

  private val selectedGroupFilter = MutableStateFlow<Int?>(null)
  private val freeExploreFlow = MutableStateFlow(false)
  private val localModeOverride = MutableStateFlow<LearningMode?>(null)
  private val localLanguageOverride = MutableStateFlow<LanguageMode?>(null)

  private var initializedBadgeTracking = false
  private val seenUnlockedBadgeIds = mutableSetOf<String>()
  private val _newlyUnlockedBadge = MutableStateFlow<DigitalBadge?>(null)
  val newlyUnlockedBadge: StateFlow<DigitalBadge?> = _newlyUnlockedBadge.asStateFlow()

  init {
    viewModelScope.launch {
      repository.ensureProfileExists(defaultDisplayName)
    }
  }

  private val localFilterFlow =
    combine(
      selectedGroupFilter,
      freeExploreFlow,
      localModeOverride,
      localLanguageOverride,
    ) { groupFilter, freeExplore, modeOverride, langOverride ->
      LocalUiFilterState(
        selectedGroup = groupFilter,
        freeExplore = freeExplore,
        modeOverride = modeOverride,
        langOverride = langOverride,
      )
    }

  val uiState: StateFlow<HomeUiState> =
    combine(
        repository.observeProfile(currentUserId),
        repository.observeLetterProgress(currentUserId),
        repository.observeStageProgress(currentUserId),
        localFilterFlow,
      ) { remoteProfile, progressMap, stageMap, filters ->
        val profile =
          remoteProfile
            ?: LearnerProfile(
              id = currentUserId,
              userId = currentUserId,
            )

        val activeMode = filters.modeOverride ?: LearningMode.fromStorage(profile.preferredMode)
        val activeLang = filters.langOverride ?: LanguageMode.fromStorage(profile.preferredLanguage)
        val orderedLetters = PhonicsCurriculum.lettersForMode(activeMode)
        val visibleLetters =
          if (filters.selectedGroup == null || activeMode == LearningMode.AZ) {
            orderedLetters
          } else {
            orderedLetters.filter { it.group.groupNumber == filters.selectedGroup }
          }

        val satpinLetters = PhonicsCurriculum.lettersForMode(LearningMode.SATPIN)
        val nextLetter =
          satpinLetters.firstOrNull { progressMap[it.letter]?.mastered != true }
            ?: satpinLetters.first()
        val masteredCount = progressMap.values.count { it.mastered }

        val badgeProgressList =
          BadgeCatalog.evaluate(
            totalStars = profile.totalStars,
            progressByLetter = progressMap,
            stageProgress = stageMap,
          )
        val unlockedIds = badgeProgressList.filter { it.unlocked }.map { it.badge.id }.toSet()
        if (remoteProfile != null) {
          if (!initializedBadgeTracking) {
            seenUnlockedBadgeIds.addAll(unlockedIds)
            initializedBadgeTracking = true
          } else {
            val freshBadge =
              badgeProgressList.firstOrNull { it.unlocked && it.badge.id !in seenUnlockedBadgeIds }
            seenUnlockedBadgeIds.addAll(unlockedIds)
            if (freshBadge != null) {
              _newlyUnlockedBadge.value = freshBadge.badge
            }
          }
        }

        val unlockedCount = unlockedIds.size
        val rank = BadgeCatalog.rankFor(unlockedCount)

        HomeUiState.Success(
          profile = profile,
          learningMode = activeMode,
          languageMode = activeLang,
          selectedGroup = filters.selectedGroup,
          freeExplore = filters.freeExplore,
          letters = visibleLetters,
          progressByLetter = progressMap,
          stageProgressById = stageMap,
          badgeProgress = badgeProgressList,
          unlockedBadgeCount = unlockedCount,
          learnerRank = rank,
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

  fun dismissUnlockedBadge() {
    _newlyUnlockedBadge.value = null
  }

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

  fun recordStageReward(
    stageId: String,
    starsEarnedDelta: Int,
  ) {
    viewModelScope.launch {
      repository.recordStageReward(stageId = stageId, starsEarnedDelta = starsEarnedDelta)
    }
  }

  private companion object {
    const val TAG = "HomeViewModel"
    const val STOP_TIMEOUT_MILLIS = 5000L
  }
}
