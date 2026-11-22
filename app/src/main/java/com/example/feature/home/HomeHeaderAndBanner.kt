package com.example.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.ui.LanguageScriptBadge
import com.example.domain.model.LanguageMode
import com.example.domain.model.PhonicsLetter
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InkNavy
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PlumPurple
import com.example.ui.theme.SkyTeal
import com.example.ui.theme.SoftSand
import com.example.ui.theme.SunshineAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TopLearnerHeader(
  childName: String,
  totalStars: Long,
  unlockedBadgeCount: Int,
  totalBadgeCount: Int,
  languageMode: LanguageMode,
  onEditNameClick: () -> Unit,
  onToggleLanguage: () -> Unit,
  onOpenRewards: () -> Unit,
  onOpenParentZone: () -> Unit,
  onSignOut: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier =
            Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(SoftSand)
              .border(2.dp, SunshineAmber, CircleShape),
          contentAlignment = Alignment.Center,
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_mascot_welcome),
            contentDescription = "Learner avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (languageMode == LanguageMode.HINGLISH) "Namaste!" else "Welcome back!",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false,
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = childName,
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onBackground,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false),
            )
            IconButton(
              onClick = onEditNameClick,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("edit_name_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Edit child name",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
              )
            }
          }
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        IconButton(
          onClick = onOpenParentZone,
          modifier =
            Modifier
              .minimumInteractiveComponentSize()
              .clip(CircleShape)
              .background(PlumPurple.copy(alpha = 0.12f))
              .testTag("open_parent_zone_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.AdminPanelSettings,
            contentDescription = "Open Parent Zone",
            tint = PlumPurple,
          )
        }

        IconButton(
          onClick = onSignOut,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("sign_out_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.Logout,
            contentDescription = "Sign out",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Surface(
        onClick = onOpenRewards,
        color = SunshineAmber.copy(alpha = 0.24f),
        shape = RoundedCornerShape(50),
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .border(1.5.dp, SunshineAmber, RoundedCornerShape(50))
            .testTag("stars_rewards_pill"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Total stars",
            tint = GoldStar,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "$totalStars Stars",
            style = MaterialTheme.typography.titleMedium,
            color = InkNavy,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
          )
        }
      }

      Surface(
        onClick = onOpenRewards,
        color = PlumPurple.copy(alpha = 0.14f),
        shape = RoundedCornerShape(50),
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .border(1.5.dp, PlumPurple.copy(alpha = 0.6f), RoundedCornerShape(50))
            .testTag("open_badges_pill"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.EmojiEvents,
            contentDescription = "Open Badges and Rewards",
            tint = PlumPurple,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "$unlockedBadgeCount/$totalBadgeCount Badges",
            style = MaterialTheme.typography.labelLarge,
            color = PlumPurple,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
          )
        }
      }

      Surface(
        onClick = onToggleLanguage,
        color = SkyTeal.copy(alpha = 0.14f),
        shape = RoundedCornerShape(50),
        modifier =
          Modifier
            .minimumInteractiveComponentSize()
            .border(1.5.dp, SkyTeal, RoundedCornerShape(50))
            .testTag("language_toggle_button"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          LanguageScriptBadge(
            tint = SkyTeal,
            fontSize = 14.sp,
            description = "Toggle Hinglish or English",
          )
          Spacer(modifier = Modifier.width(7.dp))
          Text(
            text = languageMode.label,
            style = MaterialTheme.typography.labelLarge,
            color = SkyTeal,
            maxLines = 1,
            softWrap = false,
          )
        }
      }
    }
  }
}

@Composable
internal fun HeroLearningBanner(
  nextLetter: PhonicsLetter,
  masteredCount: Int,
  languageMode: LanguageMode,
  onContinueClick: () -> Unit,
  onOpenWordBlender: () -> Unit,
  onOpenSoundboard: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(28.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Box(
      modifier = Modifier.fillMaxWidth().height(206.dp),
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_hero_phonics_park),
        contentDescription = "Phonics adventure park banner",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )

      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0x331E293B),
                  Color(0xBF1E293B),
                )
              )
            )
      )

      Surface(
        onClick = onOpenSoundboard,
        color = SkyTeal,
        shape = RoundedCornerShape(50),
        shadowElevation = 4.dp,
        modifier =
          Modifier
            .align(Alignment.TopStart)
            .padding(14.dp)
            .minimumInteractiveComponentSize()
            .testTag("open_soundboard_button"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.GraphicEq,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Soundboard",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
          )
        }
      }

      Surface(
        onClick = onOpenWordBlender,
        color = PlumPurple,
        shape = RoundedCornerShape(50),
        shadowElevation = 4.dp,
        modifier =
          Modifier
            .align(Alignment.TopEnd)
            .padding(14.dp)
            .minimumInteractiveComponentSize()
            .testTag("open_word_blender_button"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Word Blender",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
          )
        }
      }

      Column(
        modifier =
          Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .padding(18.dp),
      ) {
        Text(
          text =
            if (languageMode == LanguageMode.HINGLISH) {
              "Aaj ki Awaaz: Letter ${nextLetter.letter} (${nextLetter.phonemeSymbol})"
            } else {
              "Up Next: Sound ${nextLetter.letter} (${nextLetter.phonemeSymbol})"
            },
          style = MaterialTheme.typography.headlineMedium,
          color = Color.White,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.weight(1f)) {
            LinearProgressIndicator(
              progress = { (masteredCount / 26f).coerceIn(0.04f, 1f) },
              color = SunshineAmber,
              trackColor = Color.White.copy(alpha = 0.3f),
              modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "$masteredCount of 26 sounds mastered",
              style = MaterialTheme.typography.labelMedium,
              color = Color.White.copy(alpha = 0.92f),
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = onContinueClick,
            shape = RoundedCornerShape(18.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = MintGreen,
                contentColor = InkNavy,
              ),
            modifier = Modifier.height(48.dp).testTag("continue_next_letter_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Play ${nextLetter.letter}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }
    }
  }
}
