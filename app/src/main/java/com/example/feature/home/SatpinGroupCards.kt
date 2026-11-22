package com.example.feature.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearningMode
import com.example.domain.model.LetterProgress
import com.example.domain.model.PhonicsLetter
import com.example.domain.model.SatpinGroup
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.OutlineWarm
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.TangerineOrange

internal fun SatpinGroup.accentColor(): Color =
  when (this) {
    SatpinGroup.GROUP_1 -> CoralPrimary
    SatpinGroup.GROUP_2 -> SkyTeal
    SatpinGroup.GROUP_3 -> PlumPurple
    SatpinGroup.GROUP_4 -> TangerineOrange
  }

internal fun SatpinGroup.letterSequenceText(): String =
  when (this) {
    SatpinGroup.GROUP_1 -> "S · A · T · P · I · N"
    SatpinGroup.GROUP_2 -> "C · K · E · H · R · M · D"
    SatpinGroup.GROUP_3 -> "G · O · U · L · F · B"
    SatpinGroup.GROUP_4 -> "J · Z · W · V · Y · X · Q"
  }

@Composable
internal fun SequenceControlSection(
  learningMode: LearningMode,
  selectedGroup: Int?,
  freeExplore: Boolean,
  languageMode: LanguageMode,
  onSelectMode: (LearningMode) -> Unit,
  onSelectGroup: (Int?) -> Unit,
  onToggleFreeExplore: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.weight(1f),
      ) {
        Row(modifier = Modifier.padding(4.dp)) {
          LearningMode.entries.forEach { mode ->
            val selected = learningMode == mode
            val bg by
              animateColorAsState(
                targetValue = if (selected) CoralPrimary else Color.Transparent,
                label = "modeTabBg",
              )
            val textColor by
              animateColorAsState(
                targetValue =
                  if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "modeTabText",
              )
            Box(
              modifier =
                Modifier
                  .weight(1f)
                  .height(48.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(bg)
                  .clickable { onSelectMode(mode) }
                  .testTag("mode_tab_${mode.storageValue.lowercase()}"),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = mode.label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
              )
            }
          }
        }
      }

      FilterChip(
        selected = freeExplore,
        onClick = onToggleFreeExplore,
        label = {
          Text(
            text = if (freeExplore) "All Open" else "Guided",
            style = MaterialTheme.typography.labelLarge,
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Rounded.Explore,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
        },
        colors =
          FilterChipDefaults.filterChipColors(
            selectedContainerColor = MintGreen.copy(alpha = 0.25f),
            selectedLabelColor = InkNavy,
          ),
        modifier = Modifier.height(48.dp).testTag("free_explore_chip"),
      )
    }

    if (learningMode == LearningMode.SATPIN) {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        item {
          FilterChip(
            selected = selectedGroup == null,
            onClick = { onSelectGroup(null) },
            label = { Text("All 26") },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("group_chip_all"),
          )
        }
        items(SatpinGroup.entries) { group ->
          val chipText =
            when (group) {
              SatpinGroup.GROUP_1 -> "1: SATPIN"
              SatpinGroup.GROUP_2 -> "2: CKEHRMD"
              SatpinGroup.GROUP_3 -> "3: GOULFB"
              SatpinGroup.GROUP_4 -> "4: JZWVYXQ"
            }
          FilterChip(
            selected = selectedGroup == group.groupNumber,
            onClick = { onSelectGroup(group.groupNumber) },
            label = { Text(chipText) },
            modifier =
              Modifier
                .minimumInteractiveComponentSize()
                .testTag("group_chip_${group.groupNumber}"),
          )
        }
      }
      if (selectedGroup != null) {
        val groupObj = SatpinGroup.entries.first { it.groupNumber == selectedGroup }
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) groupObj.titleHinglish
            else groupObj.titleEn,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
      }
    }
  }
}

@Composable
internal fun SatpinJourneyOverviewStrip(
  allSatpinLetters: List<PhonicsLetter>,
  progressByLetter: Map<String, LetterProgress>,
  selectedGroup: Int?,
  freeExplore: Boolean,
  maxUnlockedOrder: Int,
  languageMode: LanguageMode,
  onPracticeGroup: (SatpinGroup) -> Unit,
) {
  val grouped = remember(allSatpinLetters) { allSatpinLetters.groupBy { it.group } }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text =
          if (languageMode == LanguageMode.HINGLISH) "SATPIN Sound Path · Groups"
          else "SATPIN Sequence Path · Tap a Group or Sound",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
      )
      Surface(color = CoralPrimary.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
        Text(
          text = "4 Stages",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = CoralPrimary,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
      }
    }

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(vertical = 2.dp),
    ) {
      itemsIndexed(SatpinGroup.entries) { idx, group ->
        val groupLetters = grouped[group].orEmpty()
        val mastered = groupLetters.count { progressByLetter[it.letter]?.mastered == true }
        val total = groupLetters.size.coerceAtLeast(1)
        val groupUnlocked =
          freeExplore || groupLetters.any { it.satpinOrder <= maxUnlockedOrder }
        val isActiveGroup = selectedGroup == group.groupNumber
        val accent = group.accentColor()

        Row(verticalAlignment = Alignment.CenterVertically) {
          Card(
            onClick = { onPracticeGroup(group) },
            shape = RoundedCornerShape(20.dp),
            colors =
              CardDefaults.cardColors(
                containerColor =
                  if (isActiveGroup) accent
                  else if (groupUnlocked) accent.copy(alpha = 0.12f)
                  else MaterialTheme.colorScheme.surfaceVariant,
              ),
            modifier =
              Modifier
                .width(194.dp)
                .border(
                  width = if (isActiveGroup) 2.dp else 1.5.dp,
                  color = if (groupUnlocked) accent else OutlineWarm,
                  shape = RoundedCornerShape(20.dp),
                )
                .minimumInteractiveComponentSize()
                .testTag("satpin_journey_card_${group.groupNumber}"),
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(
                  color = if (isActiveGroup) Color.White else accent,
                  shape = RoundedCornerShape(50),
                ) {
                  Text(
                    text = "Stage ${group.groupNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isActiveGroup) accent else Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  )
                }
                Text(
                  text = if (groupUnlocked) "$mastered/$total ⭐" else "Locked",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (isActiveGroup) Color.White else InkNavy,
                )
              }

              Text(
                text = group.letterSequenceText(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isActiveGroup) Color.White else InkNavy,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )

              LinearProgressIndicator(
                progress = { (mastered.toFloat() / total.toFloat()).coerceIn(0.06f, 1f) },
                color = if (isActiveGroup) SunshineAmber else accent,
                trackColor = if (isActiveGroup) Color.White.copy(alpha = 0.25f) else Color.White,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text =
                    if (groupUnlocked) "Tap to practice"
                    else "Complete prev group",
                  style = MaterialTheme.typography.labelSmall,
                  color =
                    if (isActiveGroup) Color.White.copy(alpha = 0.9f)
                    else MutedSlate,
                )
                Icon(
                  imageVector =
                    if (groupUnlocked) Icons.AutoMirrored.Rounded.ArrowForward
                    else Icons.Rounded.Lock,
                  contentDescription = null,
                  tint = if (isActiveGroup) Color.White else accent,
                  modifier = Modifier.size(16.dp),
                )
              }
            }
          }

          if (idx < SatpinGroup.entries.lastIndex) {
            Spacer(modifier = Modifier.width(6.dp))
            Canvas(modifier = Modifier.width(18.dp).height(4.dp)) {
              drawLine(
                color = accent.copy(alpha = 0.65f),
                start = Offset(0f, size.height / 2f),
                end = Offset(size.width, size.height / 2f),
                strokeWidth = 5f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
          }
        }
      }
    }
  }
}
