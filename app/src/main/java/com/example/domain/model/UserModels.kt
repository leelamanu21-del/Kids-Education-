package com.example.domain.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class LearnerProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val childName: String = "Little Star",
  val preferredMode: String = LearningMode.SATPIN.storageValue,
  val preferredLanguage: String = LanguageMode.HINGLISH.storageValue,
  val totalStars: Long = 0L,
  val unlockedLevelOrder: Long = 1L,
  val streakDays: Long = 1L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class LetterProgress(
  @DocumentId val id: String = "",
  val userId: String = "",
  val letter: String = "",
  val starsEarned: Long = 0L,
  val mastered: Boolean = false,
  val practiceCount: Long = 0L,
  val bestQuizScore: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class StageProgress(
  @DocumentId val id: String = "",
  val userId: String = "",
  val stageId: String = "",
  val starsEarned: Long = 0L,
  val completionsCount: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)
