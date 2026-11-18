package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.core.firebase.OperationType
import com.example.core.firebase.handleFirestoreError
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerProfile
import com.example.domain.model.LearningMode
import com.example.domain.model.LetterProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PhonicsProgressRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = FirebaseAuth.getInstance(db.app),
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private fun requireUserId(): String =
    auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")

  fun observeProfile(userId: String): Flow<LearnerProfile?> = flow {
    val docRef = db.collection("users").document(userId)
    emit(null)
    docRef
      .snapshots()
      .map { snapshot ->
        if (snapshot.exists()) {
          snapshot.toObject(
            LearnerProfile::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
        } else {
          null
        }
      }
      .catch { error ->
        val firestoreError =
          generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == userId) {
          handleFirestoreError(exceptionToLog, OperationType.GET, docRef.path)
        }
        throw error
      }
      .collect { emit(it) }
  }

  fun observeLetterProgress(userId: String): Flow<Map<String, LetterProgress>> = flow {
    val collectionRef = db.collection("users").document(userId).collection("letter_progress")
    collectionRef
      .snapshots()
      .map { snapshot ->
        snapshot
          .toObjects(
            LetterProgress::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
          .associateBy { it.letter }
      }
      .catch { error ->
        val firestoreError =
          generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == userId) {
          handleFirestoreError(exceptionToLog, OperationType.LIST, collectionRef.path)
        }
        throw error
      }
      .collect { emit(it) }
  }

  suspend fun ensureProfileExists(defaultChildName: String): Result<LearnerProfile> {
    var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
    return try {
      val uid = requireUserId()
      val docRef = db.collection("users").document(uid)
      docPath = docRef.path
      val existingSnapshot = docRef.get().await()
      if (existingSnapshot.exists()) {
        val existing =
          existingSnapshot.toObject(
            LearnerProfile::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
        if (existing != null) return Result.success(existing)
      }

      val cleanName = defaultChildName.trim().ifBlank { "Little Star" }.take(40)
      val payload =
        mapOf(
          "userId" to uid,
          "childName" to cleanName,
          "preferredMode" to LearningMode.SATPIN.storageValue,
          "preferredLanguage" to LanguageMode.HINGLISH.storageValue,
          "totalStars" to 0L,
          "unlockedLevelOrder" to 1L,
          "streakDays" to 1L,
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp(),
        )
      docRef.set(payload).await()
      val created =
        docRef
          .get()
          .await()
          .toObject(
            LearnerProfile::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
          ?: LearnerProfile(id = uid, userId = uid, childName = cleanName)
      Result.success(created)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docPath)
      Result.failure(e)
    }
  }

  suspend fun updatePreferences(
    mode: LearningMode,
    language: LanguageMode,
    childName: String? = null,
  ): Result<Unit> {
    var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
    return try {
      val uid = requireUserId()
      val docRef = db.collection("users").document(uid)
      docPath = docRef.path
      val updates = buildMap<String, Any> {
        put("preferredMode", mode.storageValue)
        put("preferredLanguage", language.storageValue)
        if (!childName.isNullOrBlank()) {
          put("childName", childName.trim().take(40))
        }
        put("updatedAt", FieldValue.serverTimestamp())
      }
      docRef.update(updates).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docPath)
      Result.failure(e)
    }
  }

  suspend fun recordLetterCompletion(
    letter: String,
    satpinOrder: Int,
    earnedStars: Int,
    quizScore: Int,
    mastered: Boolean,
  ): Result<Unit> {
    val cleanLetter = letter.uppercase().take(1)
    var letterPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/letter_progress/$cleanLetter"
    return try {
      val uid = requireUserId()
      val safeStars = earnedStars.coerceIn(0, 3).toLong()
      val safeScore = quizScore.coerceIn(0, 100).toLong()
      val letterRef =
        db.collection("users").document(uid).collection("letter_progress").document(cleanLetter)
      letterPath = letterRef.path
      val profileRef = db.collection("users").document(uid)

      val existingSnap = letterRef.get().await()
      val previousStars = existingSnap.getLong("starsEarned") ?: 0L
      val previousPractice = existingSnap.getLong("practiceCount") ?: 0L
      val previousBestScore = existingSnap.getLong("bestQuizScore") ?: 0L
      val previousMastered = existingSnap.getBoolean("mastered") ?: false

      val newStars = maxOf(previousStars, safeStars)
      val deltaStars = (newStars - previousStars).coerceAtLeast(0L)

      if (!existingSnap.exists()) {
        val createPayload =
          mapOf(
            "userId" to uid,
            "letter" to cleanLetter,
            "starsEarned" to newStars,
            "mastered" to mastered,
            "practiceCount" to 1L,
            "bestQuizScore" to safeScore,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        letterRef.set(createPayload).await()
      } else {
        val updatePayload =
          mapOf(
            "starsEarned" to newStars,
            "mastered" to (previousMastered || mastered),
            "practiceCount" to (previousPractice + 1L).coerceAtMost(10000L),
            "bestQuizScore" to maxOf(previousBestScore, safeScore),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        letterRef.update(updatePayload).await()
      }

      val profileSnap = profileRef.get().await()
      if (profileSnap.exists()) {
        val currentTotalStars = profileSnap.getLong("totalStars") ?: 0L
        val currentUnlocked = profileSnap.getLong("unlockedLevelOrder") ?: 1L
        val nextUnlocked =
          if (mastered) maxOf(currentUnlocked, (satpinOrder + 1).coerceAtMost(26).toLong())
          else currentUnlocked
        profileRef
          .update(
            mapOf(
              "totalStars" to (currentTotalStars + deltaStars).coerceAtMost(10000L),
              "unlockedLevelOrder" to nextUnlocked,
              "updatedAt" to FieldValue.serverTimestamp(),
            )
          )
          .await()
      }
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, letterPath)
      Result.failure(e)
    }
  }
}
