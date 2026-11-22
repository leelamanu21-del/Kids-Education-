package com.example.feature.stages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.CourseLevelId
import com.example.domain.model.LanguageMode
import com.example.domain.model.RewardStageIds
import com.example.domain.model.StageProgress
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.TangerineOrange

@Composable
fun CourseRoadmapSection(
  languageMode: LanguageMode,
  masteredCount: Int = 0,
  stageProgressById: Map<String, StageProgress> = emptyMap(),
  onOpenSoundboard: () -> Unit = {},
  onOpenSightWords: () -> Unit,
  onOpenWordBlender: () -> Unit,
  onOpenUkgStage: () -> Unit,
  onOpenClass1Stage: () -> Unit,
  onOpenClass2Stage: () -> Unit,
  onOpenClass3Stage: () -> Unit = {},
  onOpenClass4Stage: () -> Unit = {},
  onOpenClass5Stage: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) "LKG se Class 5 Course"
            else "LKG to Class 5 Course Path",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH)
              "Step-by-step: Phonics → Blends → Grammar → Critical Reading"
            else "Step-by-step progression: Phonics → Blends → Grammar → Essays",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(vertical = 4.dp),
    ) {
      item {
        StageModuleCard(
          level = CourseLevelId.LEVEL_1_PHONICS,
          languageMode = languageMode,
          icon = Icons.Rounded.RecordVoiceOver,
          accentColor = CoralPrimary,
          badgeOverride =
            if (masteredCount > 0) "⭐ $masteredCount/26"
            else if (languageMode == LanguageMode.HINGLISH) "Neeche Active ↓"
            else "Active Below ↓",
          onClick = null,
          testTag = "course_card_level_1",
        )
      }
      item {
        SoundboardQuickCard(
          languageMode = languageMode,
          onClick = onOpenSoundboard,
        )
      }
      item {
        val l2Stars = stageProgressById[RewardStageIds.LEVEL_2_BLENDER]?.starsEarned ?: 0L
        StageModuleCard(
          level = CourseLevelId.LEVEL_2_BLENDING,
          languageMode = languageMode,
          icon = Icons.Rounded.Extension,
          accentColor = PlumPurple,
          badgeOverride = if (l2Stars > 0L) "⭐ $l2Stars Stars" else null,
          onClick = onOpenWordBlender,
          testTag = "course_card_level_2",
        )
      }
      item {
        val sightStars = stageProgressById[RewardStageIds.LEVEL_2_SIGHT_WORDS]?.starsEarned ?: 0L
        SightWordsQuickCard(
          languageMode = languageMode,
          starsEarned = sightStars,
          onClick = onOpenSightWords,
        )
      }
      item {
        val l3Stars = stageProgressById[RewardStageIds.LEVEL_3_UKG]?.starsEarned ?: 0L
        StageModuleCard(
          level = CourseLevelId.LEVEL_3_UKG,
          languageMode = languageMode,
          icon = Icons.Rounded.AutoStories,
          accentColor = SkyTeal,
          badgeOverride = if (l3Stars > 0L) "⭐ $l3Stars Stars" else null,
          onClick = onOpenUkgStage,
          testTag = "course_card_level_3",
        )
      }
      item {
        val l4Stars = stageProgressById[RewardStageIds.LEVEL_4_CLASS1]?.starsEarned ?: 0L
        StageModuleCard(
          level = CourseLevelId.LEVEL_4_CLASS1,
          languageMode = languageMode,
          icon = Icons.Rounded.MenuBook,
          accentColor = TangerineOrange,
          badgeOverride = if (l4Stars > 0L) "⭐ $l4Stars Stars" else null,
          onClick = onOpenClass1Stage,
          testTag = "course_card_level_4",
        )
      }
      item {
        val l5Stars = stageProgressById[RewardStageIds.LEVEL_5_CLASS2]?.starsEarned ?: 0L
        StageModuleCard(
          level = CourseLevelId.LEVEL_5_CLASS2,
          languageMode = languageMode,
          icon = Icons.Rounded.School,
          accentColor = MintGreen,
          darkContent = true,
          badgeOverride = if (l5Stars > 0L) "⭐ $l5Stars Stars" else null,
          onClick = onOpenClass2Stage,
          testTag = "course_card_level_5",
        )
      }
      item {
        val l6Stars = stageProgressById[RewardStageIds.LEVEL_6_CLASS3]?.starsEarned ?: 0L
        StageModuleCard(
          level = CourseLevelId.LEVEL_6_CLASS3,
          languageMode = languageMode,
          icon = Icons.Rounded.Create,
          accentColor = PlumPurple,
          badgeOverride = if (l6Stars > 0L) "⭐ $l6Stars Stars" else null,
          onClick = onOpenClass3Stage,
          testTag = "course_card_level_6",
        )
      }
      item {
        val l7Stars = stageProgressById[RewardStageIds.LEVEL_7_CLASS4]?.starsEarned ?: 0L
        StageModuleCard(
          level = CourseLevelId.LEVEL_7_CLASS4,
          languageMode = languageMode,
          icon = Icons.Rounded.AutoStories,
          accentColor = CoralPrimary,
          badgeOverride = if (l7Stars > 0L) "⭐ $l7Stars Stars" else null,
          onClick = onOpenClass4Stage,
          testTag = "course_card_level_7",
        )
      }
      item {
        val l8Stars = stageProgressById[RewardStageIds.LEVEL_8_CLASS5]?.starsEarned ?: 0L
        StageModuleCard(
          level = CourseLevelId.LEVEL_8_CLASS5,
          languageMode = languageMode,
          icon = Icons.Rounded.EmojiEvents,
          accentColor = InkNavy,
          badgeOverride = if (l8Stars > 0L) "⭐ $l8Stars Stars" else null,
          onClick = onOpenClass5Stage,
          testTag = "course_card_level_8",
        )
      }
    }
  }
}

@Composable
private fun StageModuleCard(
  level: CourseLevelId,
  languageMode: LanguageMode,
  icon: ImageVector,
  accentColor: Color,
  badgeOverride: String?,
  darkContent: Boolean = false,
  onClick: (() -> Unit)?,
  testTag: String,
) {
  val contentTint = if (darkContent) InkNavy else Color.White

  Card(
    onClick = onClick ?: {},
    enabled = onClick != null,
    shape = RoundedCornerShape(22.dp),
    colors =
      CardDefaults.cardColors(
        containerColor = accentColor,
        disabledContainerColor = accentColor,
      ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier =
      Modifier
        .width(214.dp)
        .minimumInteractiveComponentSize()
        .testTag(testTag),
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxWidth()
          .background(
            Brush.linearGradient(
              listOf(accentColor, accentColor.copy(alpha = 0.84f))
            )
          )
          .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            color = contentTint.copy(alpha = 0.18f),
            shape = RoundedCornerShape(50),
          ) {
            Text(
              text = "Lvl ${level.levelNumber} · ${level.gradeTag}",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = contentTint,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentTint,
            modifier = Modifier.size(20.dp),
          )
        }

        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) level.titleHinglish else level.titleEnglish,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = contentTint,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )

        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) level.subtitleHinglish
            else level.subtitleEnglish,
          style = MaterialTheme.typography.bodySmall,
          color = contentTint.copy(alpha = 0.9f),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
            text = badgeOverride ?: level.ageTag,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = contentTint.copy(alpha = 0.92f),
          )
          if (onClick != null) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
              contentDescription = "Open Level ${level.levelNumber}",
              tint = contentTint,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SoundboardQuickCard(
  languageMode: LanguageMode,
  onClick: () -> Unit,
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = SkyTeal.copy(alpha = 0.16f)),
    modifier =
      Modifier
        .width(204.dp)
        .border(2.dp, SkyTeal, RoundedCornerShape(22.dp))
        .minimumInteractiveComponentSize()
        .testTag("course_card_soundboard"),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          color = SkyTeal,
          shape = RoundedCornerShape(50),
        ) {
          Text(
            text = "All Levels · Board",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
        Icon(
          imageVector = Icons.Rounded.GraphicEq,
          contentDescription = null,
          tint = SkyTeal,
          modifier = Modifier.size(20.dp),
        )
      }

      Text(
        text = if (languageMode == LanguageMode.HINGLISH) "Awaaz Soundboard" else "Phonics Soundboard",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = InkNavy,
      )

      Text(
        text = "57 Sounds: A-Z, sh, ch, th, bl, gr, ai, ee...",
        style = MaterialTheme.typography.bodySmall,
        color = InkNavy.copy(alpha = 0.85f),
        maxLines = 2,
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = "Tap · Hear & See",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = SkyTeal,
        )
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
          contentDescription = "Open Soundboard",
          tint = SkyTeal,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@Composable
private fun SightWordsQuickCard(
  languageMode: LanguageMode,
  starsEarned: Long = 0L,
  onClick: () -> Unit,
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = SunshineAmber.copy(alpha = 0.25f)),
    modifier =
      Modifier
        .width(196.dp)
        .border(2.dp, SunshineAmber, RoundedCornerShape(22.dp))
        .minimumInteractiveComponentSize()
        .testTag("course_card_sight_words"),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          color = SunshineAmber,
          shape = RoundedCornerShape(50),
        ) {
          Text(
            text = "LKG · Sight Words",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = InkNavy,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
        Icon(
          imageVector = Icons.Rounded.Visibility,
          contentDescription = null,
          tint = InkNavy,
          modifier = Modifier.size(20.dp),
        )
      }

      Text(
        text = if (languageMode == LanguageMode.HINGLISH) "Tricky Words" else "Tricky Sight Words",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = InkNavy,
      )

      Text(
        text = "3 Sets (24 Words): the, is, said, what, look...",
        style = MaterialTheme.typography.bodySmall,
        color = InkNavy.copy(alpha = 0.85f),
        maxLines = 2,
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = if (starsEarned > 0L) "⭐ $starsEarned Stars" else "Tap · 24 Words",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = InkNavy,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
          contentDescription = "Open Sight Words",
          tint = InkNavy,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}
