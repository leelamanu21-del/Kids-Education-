package com.example.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Bilingual language indicator pairing English 'A' and Hindi 'अ' without foreign script glyphs.
 */
@Composable
fun LanguageScriptBadge(
  tint: Color,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 13.sp,
  description: String? = null,
) {
  Row(
    modifier =
      modifier.then(
        if (description != null) {
          Modifier.semantics { contentDescription = description }
        } else {
          Modifier
        }
      ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    Text(
      text = "A",
      fontSize = fontSize,
      fontWeight = FontWeight.ExtraBold,
      color = tint,
      maxLines = 1,
      softWrap = false,
    )
    Text(
      text = "/",
      fontSize = (fontSize.value * 0.85f).sp,
      fontWeight = FontWeight.SemiBold,
      color = tint.copy(alpha = 0.7f),
      maxLines = 1,
      softWrap = false,
    )
    Text(
      text = "अ",
      fontSize = fontSize,
      fontWeight = FontWeight.ExtraBold,
      color = tint,
      maxLines = 1,
      softWrap = false,
    )
  }
}
