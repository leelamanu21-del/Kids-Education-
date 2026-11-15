package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearningMode
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhonicsProgressRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun ensureProfileExists_authenticatedOwner_createsAndReadsProfile() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = PhonicsProgressRepository(firestore, auth)

    val createResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.ensureProfileExists("Aarav") }
    assertTrue("Profile create failed: ${createResult.exceptionOrNull()}", createResult.isSuccess)
    val profile = createResult.getOrThrow()
    assertEquals(uid, profile.userId)
    assertEquals("SATPIN", profile.preferredMode)
    assertEquals("HINGLISH", profile.preferredLanguage)
  }

  @Test
  fun updatePreferences_andRecordLetterCompletion_updatesStarsAndUnlocksNextLevel() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = PhonicsProgressRepository(firestore, auth)
    withTimeout(DEFAULT_TIMEOUT_MS) { repo.ensureProfileExists("Meera").getOrThrow() }

    val prefResult =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repo.updatePreferences(LearningMode.AZ, LanguageMode.ENGLISH, "Meera")
      }
    assertTrue("Pref update failed: ${prefResult.exceptionOrNull()}", prefResult.isSuccess)

    val letterResult =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repo.recordLetterCompletion(
          letter = "S",
          satpinOrder = 1,
          earnedStars = 3,
          quizScore = 100,
          mastered = true,
        )
      }
    assertTrue("Letter completion failed: ${letterResult.exceptionOrNull()}", letterResult.isSuccess)

    val progressMap =
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeLetterProgress(uid).first { it.containsKey("S") }
      }
    assertEquals(3L, progressMap["S"]?.starsEarned)
    assertTrue(progressMap["S"]?.mastered == true)

    val updatedProfile =
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeProfile(uid).first { it != null && it.unlockedLevelOrder >= 2L }
      }
    assertNotNull(updatedProfile)
    assertEquals(2L, updatedProfile?.unlockedLevelOrder)
  }

  @Test
  fun observeLetterProgress_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = PhonicsProgressRepository(firestore, auth)
    withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.ensureProfileExists("Alice").getOrThrow() }

    signInTestUser(BOB_EMAIL)
    val bobRepo = PhonicsProgressRepository(firestore, auth)

    val exception =
      try {
        withTimeout(FLOW_TIMEOUT_MS) { bobRepo.observeLetterProgress(aliceUid).first() }
        null
      } catch (e: Exception) {
        e
      }
    assertNotNull("Expected PERMISSION_DENIED for cross-user access", exception)
    val firestoreException = exception!!.unwrapFirestoreException()
    assertNotNull("Expected nested FirebaseFirestoreException, got $exception", firestoreException)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException!!.code)
  }

  @Test
  fun observeLetterProgress_unauthenticated_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repo = PhonicsProgressRepository(firestore, auth)

    val exception =
      try {
        withTimeout(FLOW_TIMEOUT_MS) { repo.observeLetterProgress("some_uid_123").first() }
        null
      } catch (e: Exception) {
        e
      }
    assertNotNull("Expected PERMISSION_DENIED when unauthenticated", exception)
    val firestoreException = exception!!.unwrapFirestoreException()
    assertNotNull("Expected nested FirebaseFirestoreException, got $exception", firestoreException)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException!!.code)
  }

  private companion object {
    const val ALICE_EMAIL = "alice.phonics@test.com"
    const val BOB_EMAIL = "bob.phonics@test.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 3000L
  }
}
