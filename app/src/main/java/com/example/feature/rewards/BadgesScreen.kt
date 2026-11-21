package com.example.feature.rewards

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.PhonicsAudioPlayer
import com.example.domain.model.BadgeCategory
import com.example.domain.model.BadgeProgress
import com.example.domain.model.LanguageMode
import com.example.domain.model.LearnerRank
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SunshineAmber
import com.example.ui.theme.toColorSet

private enum class RewardFilter(val labelEn: String, val labelHinglish: String) {
  ALL("All", "Sabhi"),
  UNLOCKED("Unlocked", "Jeete Hue"),
  LEVEL_1("Level 1", "Level 1"),
  COURSE("Levels 2–5", "Levels 2–5"),
  STARS("Star Tiers", "Star Tiers"),
}

@Composable
fun BadgesScreen(
  childName: String,
  totalStars: Long,
  learnerRank: LearnerRank,
  badgeProgress: List<BadgeProgress>,
  languageMode: LanguageMode,
  audioPlayer: PhonicsAudioPlayer,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var selectedFilter by remember { mutableStateOf(RewardFilter.ALL) }
  val unlockedCount = badgeProgress.count { it.unlocked }
  val totalBadges = badgeProgress.size
  val nextClosestBadge =
    remember(badgeProgress) {
      badgeProgress.filter { !it.unlocked }.maxByOrNull { it.fraction }
    }

  val visibleBadges =
    remember(badgeProgress, selectedFilter) {
      when (selectedFilter) {
        RewardFilter.ALL -> badgeProgress
        RewardFilter.UNLOCKED -> badgeProgress.filter { it.unlocked }
        RewardFilter.LEVEL_1 ->
          badgeProgress.filter { it.badge.category == BadgeCategory.PHONICS_PATH }
        RewardFilter.COURSE ->
          badgeProgress.filter { it.badge.category == BadgeCategory.COURSE_STAGES }
        RewardFilter.STARS ->
          badgeProgress.filter { it.badge.category == BadgeCategory.STAR_TIERS }
      }
    }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding).statusBarsPadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 158.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier =
          Modifier
            .fillMaxSize()
            .widthIn(max = 820.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("badges_grid"),
      ) {
        // Top Header
        item(span = { GridItemSpan(maxLineSpan) }) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            IconButton(
              onClick = onBack,
              modifier =
                Modifier
                  .minimumInteractiveComponentSize()
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surface)
                  .testTag("rewards_back_button"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back to home",
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text =
                  if (languageMode == LanguageMode.HINGLISH) "Trophies & Badges"
                  else "Stars & Digital Badges",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
              )
              Text(
                text = "$unlockedCount of $totalBadges unlocked",
                style = MaterialTheme.typography.labelLarge,
                color = PlumPurple,
              )
            }

            Spacer(modifier = Modifier.size(48.dp))
          }
        }

        // Hero Trophy & Rank Card
        item(span = { GridItemSpan(maxLineSpan) }) {
          Card(
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Box(
              modifier =
                Modifier
                  .fillMaxWidth()
                  .background(
                    Brush.linearGradient(
                      listOf(PlumPurple, CoralPrimary.copy(alpha = 0.92f))
                    )
                  )
                  .padding(20.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                  ) {
                    Surface(
                      color = Color.White.copy(alpha = 0.22f),
                      shape = CircleShape,
                      modifier = Modifier.size(62.dp),
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Text(text = learnerRank.emoji, fontSize = 32.sp)
                      }
                    }
                    Column {
                      Text(
                        text = "$childName's Rank",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.88f),
                      )
                      Text(
                        text =
                          if (languageMode == LanguageMode.HINGLISH) learnerRank.titleHinglish
                          else learnerRank.titleEn,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                      )
                    }
                  }

                  Surface(
                    color = SunshineAmber,
                    shape = RoundedCornerShape(50),
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = InkNavy,
                        modifier = Modifier.size(18.dp),
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "$totalStars",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkNavy,
                      )
                    }
                  }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  val ratio =
                    if (totalBadges > 0) (unlockedCount.toFloat() / totalBadges).coerceIn(0.04f, 1f)
                    else 0f
                  LinearProgressIndicator(
                    progress = { ratio },
                    color = SunshineAmber,
                    trackColor = Color.White.copy(alpha = 0.28f),
                    modifier = Modifier.fillMaxWidth().height(11.dp).clip(RoundedCornerShape(50)),
                  )
                  Text(
                    text =
                      if (languageMode == LanguageMode.HINGLISH)
                        "$unlockedCount / $totalBadges badges jeete! Har activity par stars kamao!"
                      else "$unlockedCount / $totalBadges badges unlocked! Complete levels to collect more!",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.94f),
                  )
                }
              }
            }
          }
        }

        // Next Closest Goal Card
        if (nextClosestBadge != null) {
          item(span = { GridItemSpan(maxLineSpan) }) {
            val badge = nextClosestBadge.badge
            Surface(
              color = SunshineAmber.copy(alpha = 0.22f),
              shape = RoundedCornerShape(22.dp),
              modifier =
                Modifier
                  .fillMaxWidth()
                  .border(1.5.dp, SunshineAmber, RoundedCornerShape(22.dp)),
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Text(text = badge.emoji, fontSize = 36.sp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(
                    text =
                      if (languageMode == LanguageMode.HINGLISH) "Agla Target Badge:"
                      else "Next Badge Goal:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CoralPrimary,
                  )
                  Text(
                    text =
                      if (languageMode == LanguageMode.HINGLISH) badge.titleHinglish
                      else badge.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkNavy,
                  )
                  Text(
                    text =
                      if (languageMode == LanguageMode.HINGLISH) badge.descriptionHinglish
                      else badge.descriptionEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkNavy.copy(alpha = 0.85f),
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  LinearProgressIndicator(
                    progress = { nextClosestBadge.fraction.coerceAtLeast(0.05f) },
                    color = CoralPrimary,
                    trackColor = Color.White,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                  )
                  Text(
                    text = "${nextClosestBadge.currentProgress} / ${badge.targetProgress}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkNavy,
                  )
                }
              }
            }
          }
        }

        // Filter Chips Row
        item(span = { GridItemSpan(maxLineSpan) }) {
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(RewardFilter.entries) { filter ->
              FilterChip(
                selected = selectedFilter == filter,
                onClick = { selectedFilter = filter },
                label = {
                  Text(
                    text =
                      if (languageMode == LanguageMode.HINGLISH) filter.labelHinglish
                      else filter.labelEn,
                    fontWeight = FontWeight.Bold,
                  )
                },
                modifier =
                  Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("reward_filter_${filter.name.lowercase()}"),
              )
            }
          }
        }

        // Empty state for Unlocked filter when 0 unlocked
        if (visibleBadges.isEmpty()) {
          item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
              shape = RoundedCornerShape(24.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.EmojiEvents,
                  contentDescription = null,
                  tint = SunshineAmber,
                  modifier = Modifier.size(48.dp),
                )
                Text(
                  text =
                    if (languageMode == LanguageMode.HINGLISH)
                      "Pehla lesson ya Word Blender khelkar apna pehla badge jeetein!"
                    else "Complete a lesson or Word Blender challenge to earn your first badge!",
                  style = MaterialTheme.typography.titleMedium,
                  textAlign = TextAlign.Center,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }
        } else {
          items(items = visibleBadges, key = { it.badge.id }) { item ->
            BadgeShowcaseCard(
              badgeProgress = item,
              languageMode = languageMode,
              onClick = {
                val badge = item.badge
                if (item.unlocked) {
                  audioPlayer.playCelebrationChime()
                  audioPlayer.speakBilingualScript(
                    hindiDevanagariScript = "शाबाश! आपने ${badge.titleEn} बैज जीत लिया है!",
                    englishScript = "Awesome job! You unlocked the ${badge.titleEn} badge!",
                    languageMode = languageMode,
                  )
                } else {
                  val remaining = (badge.targetProgress - item.currentProgress).coerceAtLeast(1)
                  audioPlayer.speakBilingualScript(
                    hindiDevanagariScript = "${badge.titleEn} बैज के लिए बस $remaining और पूरा करें!",
                    englishScript = "Just $remaining more to unlock ${badge.titleEn}!",
                    languageMode = languageMode,
                  )
                }
              },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BadgeShowcaseCard(
  badgeProgress: BadgeProgress,
  languageMode: LanguageMode,
  onClick: () -> Unit,
) {
  val badge = badgeProgress.badge
  val paletteColors = badge.palette.toColorSet()
  val unlocked = badgeProgress.unlocked

  val containerColor =
    if (unlocked) MaterialTheme.colorScheme.surface
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
  val borderColor = if (unlocked) GoldStar else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)

  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (unlocked) 4.dp else 0.dp),
    modifier =
      Modifier
        .fillMaxWidth()
        .border(if (unlocked) 2.5.dp else 1.dp, borderColor, RoundedCornerShape(24.dp))
        .clickable(onClick = onClick)
        .minimumInteractiveComponentSize()
        .testTag("badge_card_${badge.id}"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          color = if (unlocked) MintGreen.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.08f),
          shape = RoundedCornerShape(50),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
              imageVector = if (unlocked) Icons.Rounded.CheckCircle else Icons.Rounded.Lock,
              contentDescription = if (unlocked) "Unlocked" else "Locked",
              tint = if (unlocked) MintGreen else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp),
            )
            Text(
              text = if (unlocked) "Unlocked" else "Locked",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = if (unlocked) InkNavy else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Icon(
          imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
          contentDescription = "Hear badge hint",
          tint = if (unlocked) paletteColors.fill else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp),
        )
      }

      Box(
        modifier =
          Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              if (unlocked) paletteColors.fill.copy(alpha = 0.2f)
              else Color.Black.copy(alpha = 0.06f)
            )
            .border(
              2.dp,
              if (unlocked) paletteColors.fill else Color.Transparent,
              CircleShape,
            ),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = badge.emoji,
          fontSize = 36.sp,
          modifier = Modifier.alpha(if (unlocked) 1f else 0.42f),
        )
      }

      Text(
        text = if (languageMode == LanguageMode.HINGLISH) badge.titleHinglish else badge.titleEn,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )

      Text(
        text =
          if (languageMode == LanguageMode.HINGLISH) badge.descriptionHinglish
          else badge.descriptionEn,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        LinearProgressIndicator(
          progress = { badgeProgress.fraction.coerceAtLeast(if (unlocked) 1f else 0.05f) },
          color = if (unlocked) MintGreen else SkyTeal,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(50)),
        )
        Text(
          text = "${badgeProgress.currentProgress} / ${badge.targetProgress}",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = if (unlocked) InkNavy else MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}
