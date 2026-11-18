package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.audio.PhonicsAudioPlayer
import com.example.core.timer.SessionTimerUiState
import com.example.data.repository.PhonicsProgressRepository
import com.example.domain.curriculum.PhonicsCurriculum
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerProfile
import com.example.feature.auth.AuthScreen
import com.example.feature.auth.signOut
import com.example.feature.blender.WordBlenderScreen
import com.example.feature.home.HomeLearningPathScreen
import com.example.feature.home.HomeUiState
import com.example.feature.home.HomeViewModel
import com.example.feature.lesson.LetterLessonScreen
import com.example.feature.parent.ParentMathGateDialog
import com.example.feature.parent.ParentZoneScreen
import com.example.feature.stages.Class1FluencyScreen
import com.example.feature.stages.Class2GrammarStoryScreen
import com.example.feature.stages.SightWordsScreen
import com.example.feature.stages.UkgStageScreen
import com.example.feature.timer.EyeRestBreakScreen
import com.example.ui.theme.PhonicsPathTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object PhonicsRoutes {
  const val HOME = "home"
  const val LESSON = "lesson/{letter}"
  const val BLENDER = "blender"
  const val SIGHT_WORDS = "sight_words"
  const val UKG_STAGE = "ukg_stage"
  const val CLASS1_STAGE = "class1_stage"
  const val CLASS2_STAGE = "class2_stage"
  const val PARENT = "parent"

  fun lessonRoute(letter: String): String = "lesson/${letter.uppercase()}"
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PhonicsPathTheme {
        PhonicsAppNavigation()
      }
    }
  }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
  val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
  addAuthStateListener(listener)
  awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun PhonicsAppNavigation(auth: FirebaseAuth = Firebase.auth) {
  val currentUser by
    auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  val user = currentUser

  if (user == null) {
    AuthScreen(onAuthSuccess = {})
  } else {
    key(user.uid) {
      AuthenticatedPhonicsFlow(
        currentUserId = user.uid,
        defaultChildName = user.displayName?.split(" ")?.firstOrNull() ?: "Little Star",
      )
    }
  }
}

@Composable
private fun AuthenticatedPhonicsFlow(
  currentUserId: String,
  defaultChildName: String,
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember(context) { CredentialManager.create(context) }
  val audioPlayer = remember(context) { PhonicsAudioPlayer(context) }

  DisposableEffect(audioPlayer) {
    onDispose {
      audioPlayer.shutdown()
    }
  }

  val homeViewModel: HomeViewModel =
    viewModel(
      key = currentUserId,
      factory =
        viewModelFactory {
          initializer {
            val app =
              checkNotNull(this[APPLICATION_KEY]) {
                "APPLICATION_KEY missing from CreationExtras"
              }
            val databaseId = app.getString(R.string.firestore_database_id)
            val db = FirebaseFirestore.getInstance(databaseId)
            HomeViewModel(
              repository = PhonicsProgressRepository(db),
              currentUserId = currentUserId,
              defaultDisplayName = defaultChildName,
            )
          }
        },
    )

  val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
  val navController = rememberNavController()
  val successState = homeState as? HomeUiState.Success
  val currentLanguageMode = successState?.languageMode ?: LanguageMode.HINGLISH

  // Guilt-free session eye-rest timer (10-15 minutes continuous learning)
  var timerState by remember { mutableStateOf(SessionTimerUiState()) }
  var showParentMathGate by remember { mutableStateOf(false) }

  LaunchedEffect(timerState.isBreakActive, timerState.limitSeconds) {
    while (!timerState.isBreakActive) {
      delay(1000L)
      val nextElapsed = timerState.elapsedSeconds + 1
      val shouldBreak = nextElapsed >= timerState.limitSeconds
      timerState =
        timerState.copy(
          elapsedSeconds = nextElapsed,
          isBreakActive = shouldBreak,
        )
    }
  }

  if (timerState.isBreakActive) {
    EyeRestBreakScreen(
      childName = successState?.profile?.childName ?: defaultChildName,
      languageMode = currentLanguageMode,
      elapsedLabel = timerState.formattedElapsedMinutes,
      totalStars = successState?.profile?.totalStars ?: 0L,
      wordsBlendedToday = timerState.wordsBlendedToday,
      audioPlayer = audioPlayer,
      onGrantBonusMinutes = {
        timerState =
          timerState.copy(
            isBreakActive = false,
            bonusMinutesGranted = timerState.bonusMinutesGranted + 5,
          )
      },
      onRestartFreshSession = {
        timerState =
          timerState.copy(
            elapsedSeconds = 0,
            isBreakActive = false,
            bonusMinutesGranted = 0,
          )
      },
    )
    return
  }

  NavHost(
    navController = navController,
    startDestination = PhonicsRoutes.HOME,
  ) {
    composable(PhonicsRoutes.HOME) {
      HomeLearningPathScreen(
        uiState = homeState,
        timerState = timerState,
        onSelectMode = homeViewModel::setLearningMode,
        onToggleLanguage = homeViewModel::toggleLanguageMode,
        onSelectGroup = homeViewModel::selectGroup,
        onToggleFreeExplore = homeViewModel::toggleFreeExplore,
        onUpdateChildName = homeViewModel::updateChildName,
        onLetterClick = { letter ->
          navController.navigate(PhonicsRoutes.lessonRoute(letter.letter))
        },
        onSpeakLetter = { letter, langMode ->
          audioPlayer.speakLetterPhoneme(letter, langMode)
        },
        onOpenWordBlender = { navController.navigate(PhonicsRoutes.BLENDER) },
        onOpenSightWords = { navController.navigate(PhonicsRoutes.SIGHT_WORDS) },
        onOpenUkgStage = { navController.navigate(PhonicsRoutes.UKG_STAGE) },
        onOpenClass1Stage = { navController.navigate(PhonicsRoutes.CLASS1_STAGE) },
        onOpenClass2Stage = { navController.navigate(PhonicsRoutes.CLASS2_STAGE) },
        onOpenParentZone = { showParentMathGate = true },
        onSignOut = {
          audioPlayer.stop()
          signOut(credentialManager = credentialManager, scope = coroutineScope)
        },
      )
    }

    composable(
      route = PhonicsRoutes.LESSON,
      arguments = listOf(navArgument("letter") { type = NavType.StringType }),
    ) { backStackEntry ->
      val letterKey = backStackEntry.arguments?.getString("letter") ?: "S"
      val phonicsLetter =
        PhonicsCurriculum.findByLetter(letterKey) ?: PhonicsCurriculum.letters.first()
      LetterLessonScreen(
        letter = phonicsLetter,
        languageMode = currentLanguageMode,
        audioPlayer = audioPlayer,
        onCompleteLesson = { stars, score, mastered ->
          homeViewModel.completeLetterLesson(
            letter = phonicsLetter,
            earnedStars = stars,
            quizScore = score,
            mastered = mastered,
          )
        },
        onBack = {
          audioPlayer.stop()
          navController.popBackStack()
        },
      )
    }

    composable(PhonicsRoutes.BLENDER) {
      WordBlenderScreen(
        languageMode = currentLanguageMode,
        audioPlayer = audioPlayer,
        onWordBlended = {
          timerState = timerState.copy(wordsBlendedToday = timerState.wordsBlendedToday + 1)
        },
        onBack = {
          audioPlayer.stop()
          navController.popBackStack()
        },
      )
    }

    composable(PhonicsRoutes.SIGHT_WORDS) {
      SightWordsScreen(
        languageMode = currentLanguageMode,
        audioPlayer = audioPlayer,
        onBack = {
          audioPlayer.stop()
          navController.popBackStack()
        },
      )
    }

    composable(PhonicsRoutes.UKG_STAGE) {
      UkgStageScreen(
        languageMode = currentLanguageMode,
        audioPlayer = audioPlayer,
        onBack = {
          audioPlayer.stop()
          navController.popBackStack()
        },
      )
    }

    composable(PhonicsRoutes.CLASS1_STAGE) {
      Class1FluencyScreen(
        languageMode = currentLanguageMode,
        audioPlayer = audioPlayer,
        onBack = {
          audioPlayer.stop()
          navController.popBackStack()
        },
      )
    }

    composable(PhonicsRoutes.CLASS2_STAGE) {
      Class2GrammarStoryScreen(
        languageMode = currentLanguageMode,
        audioPlayer = audioPlayer,
        onBack = {
          audioPlayer.stop()
          navController.popBackStack()
        },
      )
    }

    composable(PhonicsRoutes.PARENT) {
      ParentZoneScreen(
        profile =
          successState?.profile
            ?: LearnerProfile(id = currentUserId, userId = currentUserId, childName = defaultChildName),
        progressByLetter = successState?.progressByLetter.orEmpty(),
        timerState = timerState,
        onSelectTimerPreset = { preset ->
          timerState = timerState.copy(preset = preset)
        },
        onTriggerBreakPreview = {
          navController.popBackStack()
          timerState = timerState.copy(isBreakActive = true)
        },
        onBack = { navController.popBackStack() },
      )
    }
  }

  if (showParentMathGate) {
    ParentMathGateDialog(
      onDismiss = { showParentMathGate = false },
      onSolved = {
        showParentMathGate = false
        navController.navigate(PhonicsRoutes.PARENT)
      },
    )
  }
}
