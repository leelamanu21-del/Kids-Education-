const express = require('express');

const router = express.Router();

const SATPIN_GROUPS = [
  {
    group: 1,
    name: 'Group 1 (S, A, T, P, I, N)',
    letters: ['S', 'A', 'T', 'P', 'I', 'N'],
    sampleWords: ['SAT', 'TAP', 'PIN', 'PAN', 'ANT', 'NAP'],
  },
  {
    group: 2,
    name: 'Group 2 (C, K, E, H, R, M, D)',
    letters: ['C', 'K', 'E', 'H', 'R', 'M', 'D'],
    sampleWords: ['CAT', 'HAT', 'MAT', 'RED', 'HEN', 'KID'],
  },
  {
    group: 3,
    name: 'Group 3 (G, O, U, L, F, B)',
    letters: ['G', 'O', 'U', 'L', 'F', 'B'],
    sampleWords: ['DOG', 'SUN', 'BUS', 'LOG', 'FUN', 'GOT'],
  },
  {
    group: 4,
    name: 'Group 4 (J, Z, W, V, Y, X, Q)',
    letters: ['J', 'Z', 'W', 'V', 'Y', 'X', 'Q'],
    sampleWords: ['JAM', 'ZIP', 'VAN', 'FOX', 'YES', 'WEB'],
  },
];

function sanitizeLetters(input) {
  if (!Array.isArray(input)) return [];
  const seen = new Set();
  for (const item of input) {
    if (typeof item === 'string') {
      const cleaned = item.trim().toUpperCase();
      if (/^[A-Z]$/.test(cleaned)) {
        seen.add(cleaned);
      }
    }
  }
  return Array.from(seen);
}

function resolveMilestoneBadge(masteredCount, wordsBlendedCount) {
  if (masteredCount >= 26) return '26-Sound Phonics Champion';
  if (masteredCount >= 14) return 'Story & Word Explorer';
  if (wordsBlendedCount >= 5 || masteredCount >= 6) return 'SATPIN Word Blender';
  return 'SATPIN Sound Starter';
}

function computeGroupBreakdown(masteredLetters) {
  const masteredSet = new Set(masteredLetters);
  return SATPIN_GROUPS.map((g) => {
    const completed = g.letters.filter((l) => masteredSet.has(l));
    return {
      group: g.group,
      name: g.name,
      masteredCount: completed.length,
      totalCount: g.letters.length,
      isComplete: completed.length === g.letters.length,
      masteredLetters: completed,
    };
  });
}

function buildHomePracticeTip(languageMode, nextLetter, groupBreakdown) {
  const activeGroup =
    groupBreakdown.find((g) => !g.isComplete) || groupBreakdown[groupBreakdown.length - 1];
  const sampleWord = SATPIN_GROUPS[activeGroup.group - 1].sampleWords[0];

  if (languageMode === 'HINGLISH') {
    return `Ghar par 2-minute game: Kitchen ya room mein '${nextLetter}' sound se shuru hone wali 2 cheezein dhoondhein, aur '${sampleWord}' ke sounds saath mein bolein!`;
  }
  return `2-minute offline tip: Spot 2 objects around the house starting with the '${nextLetter}' sound, and sound out '${sampleWord}' together!`;
}

function formatWeeklyReport({
  appName,
  childName,
  learningMode,
  languageMode,
  masteredLetters,
  wordsBlendedCount,
  totalStars,
  activeMinutes,
  streakDays,
  nextLetter,
  milestoneBadge,
  homePracticeTip,
}) {
  const lettersDisplay =
    masteredLetters.length > 0
      ? masteredLetters.join(', ')
      : 'Getting started with SATPIN Group 1 (S, A, T, P, I, N)';

  if (languageMode === 'HINGLISH') {
    return [
      `🌟 *${appName} Weekly Progress — ${childName}* 🌟`,
      `🏅 Level Badge: ${milestoneBadge}`,
      `⭐ Total Stars: ${totalStars} | 🔥 Streak: ${streakDays} din`,
      `🔤 Mastered Sounds (${masteredLetters.length}/26): ${lettersDisplay}`,
      `🪄 CVC Words Blended: ${wordsBlendedCount}`,
      `⏱️ Screen Time This Week: ${activeMinutes} min (${learningMode} • Hinglish)`,
      `💡 Parent Tip: ${homePracticeTip}`,
    ].join('\n');
  }

  return [
    `🌟 *${appName} Weekly Progress — ${childName}* 🌟`,
    `🏅 Level Badge: ${milestoneBadge}`,
    `⭐ Total Stars: ${totalStars} | 🔥 Streak: ${streakDays} days`,
    `🔤 Mastered Sounds (${masteredLetters.length}/26): ${lettersDisplay}`,
    `🪄 CVC Words Blended: ${wordsBlendedCount}`,
    `⏱️ Active Learning Time: ${activeMinutes} min (${learningMode} • English)`,
    `💡 Home Activity Tip: ${homePracticeTip}`,
  ].join('\n');
}

function requireApiKeyIfConfigured(req, res, next) {
  const configuredKey = (process.env.REPORT_API_KEY || '').trim();
  if (!configuredKey) return next();

  const providedKey = (req.get('x-api-key') || '').trim();
  if (providedKey !== configuredKey) {
    return res.status(401).json({
      success: false,
      error: 'Unauthorized: invalid or missing x-api-key header.',
    });
  }
  return next();
}

router.get('/templates', (req, res) => {
  res.json({
    success: true,
    supportedLanguages: ['HINGLISH', 'ENGLISH'],
    supportedModes: ['SATPIN', 'AZ'],
    satpinGroups: SATPIN_GROUPS,
  });
});

router.post('/weekly', requireApiKeyIfConfigured, (req, res, next) => {
  try {
    const body = req.body || {};
    const rawChildName = typeof body.childName === 'string' ? body.childName.trim() : '';
    if (!rawChildName || rawChildName.length > 40) {
      return res.status(400).json({
        success: false,
        error: 'childName is required and must be between 1 and 40 characters.',
      });
    }

    const learningMode = body.learningMode === 'AZ' ? 'AZ' : 'SATPIN';
    const languageMode = body.languageMode === 'ENGLISH' ? 'ENGLISH' : 'HINGLISH';
    const masteredLetters = sanitizeLetters(body.masteredLetters);
    const wordsBlendedCount = Math.max(0, Math.min(10000, Number(body.wordsBlendedCount) || 0));
    const totalStars = Math.max(0, Math.min(100000, Number(body.totalStars) || 0));
    const activeMinutes = Math.max(0, Math.min(10080, Number(body.activeMinutes) || 0));
    const streakDays = Math.max(1, Math.min(3650, Number(body.streakDays) || 1));

    const allSatpinLetters = SATPIN_GROUPS.flatMap((g) => g.letters);
    const nextSuggestedLetter =
      typeof body.nextLetter === 'string' && /^[A-Za-z]$/.test(body.nextLetter.trim())
        ? body.nextLetter.trim().toUpperCase()
        : allSatpinLetters.find((l) => !masteredLetters.includes(l)) || 'S';

    const appName = (process.env.APP_NAME || 'PhonicsPath').trim();
    const groupBreakdown = computeGroupBreakdown(masteredLetters);
    const milestoneBadge = resolveMilestoneBadge(masteredLetters.length, wordsBlendedCount);
    const homePracticeTip = buildHomePracticeTip(
      languageMode,
      nextSuggestedLetter,
      groupBreakdown
    );

    const reportText = formatWeeklyReport({
      appName,
      childName: rawChildName,
      learningMode,
      languageMode,
      masteredLetters,
      wordsBlendedCount,
      totalStars,
      activeMinutes,
      streakDays,
      nextLetter: nextSuggestedLetter,
      milestoneBadge,
      homePracticeTip,
    });

    const whatsappUrl = `https://wa.me/?text=${encodeURIComponent(reportText)}`;

    return res.status(200).json({
      success: true,
      data: {
        childName: rawChildName,
        learningMode,
        languageMode,
        milestoneBadge,
        masteredCount: masteredLetters.length,
        totalLetters: 26,
        nextSuggestedLetter,
        groupBreakdown,
        homePracticeTip,
        reportText,
        whatsappUrl,
        generatedAt: new Date().toISOString(),
      },
    });
  } catch (err) {
    return next(err);
  }
});

module.exports = router;
