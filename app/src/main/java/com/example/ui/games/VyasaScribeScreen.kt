package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SacredAudioEngine
import com.example.audio.VibrationHelper
import com.example.ui.theme.CelestialTeal
import com.example.ui.theme.DivineGold
import com.example.ui.theme.LotusPink
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SacredGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SandalwoodCream
import com.example.ui.theme.TempleCard
import com.example.ui.theme.TempleDark
import kotlinx.coroutines.delay

data class SacredGlyph(
    val id: Int,
    val symbol: String,
    val name: String,
    val color: Color
)

data class VerseChallenge(
    val shlokaSanskrit: String,
    val meaning: String,
    val sequenceGlyphIds: List<Int>
)

@Composable
fun VyasaScribeScreen(
    onFinishGame: (score: Int, modaksEarned: Int, bonusDistance: Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val glyphs = remember {
        listOf(
            SacredGlyph(1, "ॐ", "Om", RadiantGold),
            SacredGlyph(2, "卐", "Swastik", SaffronPrimary),
            SacredGlyph(3, "🪷", "Padma", LotusPink),
            SacredGlyph(4, "🔱", "Trishula", CelestialTeal),
            SacredGlyph(5, "🪶", "Lekhani", DivineGold)
        )
    }

    val challenges = remember {
        listOf(
            VerseChallenge(
                shlokaSanskrit = "यतो धर्मस्ततो जयः",
                meaning = "Where there is Righteousness (Dharma), there is Victory.",
                sequenceGlyphIds = listOf(1, 2, 3)
            ),
            VerseChallenge(
                shlokaSanskrit = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन",
                meaning = "You have a right to perform your prescribed duty, but not to the fruits.",
                sequenceGlyphIds = listOf(2, 4, 1, 5)
            ),
            VerseChallenge(
                shlokaSanskrit = "वसुधैव कुटुम्बकम्",
                meaning = "The entire world is one sacred family.",
                sequenceGlyphIds = listOf(3, 1, 2, 4)
            ),
            VerseChallenge(
                shlokaSanskrit = "अहिंसा परमो धर्मः",
                meaning = "Non-violence and compassion are the highest Dharma.",
                sequenceGlyphIds = listOf(5, 3, 1, 2, 4)
            ),
            VerseChallenge(
                shlokaSanskrit = "वक्रतुण्ड महाकाय सूर्यकोटि समप्रभ",
                meaning = "O Ganesha with curved trunk and colossal form, radiant as a million suns!",
                sequenceGlyphIds = listOf(1, 4, 2, 3, 5, 1)
            )
        )
    }

    var currentVerseIndex by remember { mutableIntStateOf(0) }
    var userTappedSequence = remember { mutableStateListOf<Int>() }
    var activeHighlightedGlyph by remember { mutableStateOf<Int?>(null) }
    var isShowingSequence by remember { mutableStateOf(false) }

    var inkFlowGauge by remember { mutableFloatStateOf(1.0f) }
    var tuskSacrificeAvailable by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }
    var comboCount by remember { mutableIntStateOf(0) }

    var isGameFinished by remember { mutableStateOf(false) }
    var showIntroModal by remember { mutableStateOf(true) }
    var bannerMessage by remember { mutableStateOf<String?>(null) }

    val currentVerse = challenges.getOrNull(currentVerseIndex) ?: challenges.first()

    // Function to play dictation sequence from Sage Vyasa
    suspend fun playDictationSequence() {
        isShowingSequence = true
        userTappedSequence.clear()
        delay(600)
        for (glyphId in currentVerse.sequenceGlyphIds) {
            activeHighlightedGlyph = glyphId
            SacredAudioEngine.playModakChime()
            VibrationHelper.vibrate(context, 20)
            delay(550)
            activeHighlightedGlyph = null
            delay(200)
        }
        isShowingSequence = false
    }

    // Trigger dictation whenever verse changes
    LaunchedEffect(currentVerseIndex, showIntroModal) {
        if (!showIntroModal && !isGameFinished) {
            playDictationSequence()
        }
    }

    // Handle user tap on glyph
    fun handleGlyphTap(glyphId: Int) {
        if (isShowingSequence || isGameFinished) return

        userTappedSequence.add(glyphId)
        SacredAudioEngine.playTempleBell()
        VibrationHelper.vibrate(context, 15)

        val stepIndex = userTappedSequence.size - 1
        val expectedGlyphId = currentVerse.sequenceGlyphIds.getOrNull(stepIndex)

        if (expectedGlyphId != glyphId) {
            // Mistake made
            if (tuskSacrificeAvailable) {
                // Save streak with Ganesha's broken tusk power!
                tuskSacrificeAvailable = false
                bannerMessage = "Quill Snapped! Lord Ganesha broke his Tusk to continue scribing!"
                SacredAudioEngine.playPowerUp()
                VibrationHelper.vibrateSuccess(context)
                userTappedSequence.removeAt(userTappedSequence.size - 1)
            } else {
                // Lose ink flow
                inkFlowGauge = (inkFlowGauge - 0.25f).coerceAtLeast(0f)
                comboCount = 0
                bannerMessage = "Focus slipped! Vyasa pauses dictation..."
                SacredAudioEngine.playObstacleThud()
                VibrationHelper.vibrate(context, 60)
                userTappedSequence.clear()
            }
        } else {
            // Correct tap!
            score += 25
            if (userTappedSequence.size == currentVerse.sequenceGlyphIds.size) {
                // Verse completed!
                comboCount++
                score += 150 * comboCount
                inkFlowGauge = (inkFlowGauge + 0.2f).coerceAtMost(1.0f)
                bannerMessage = "Verse Inscribed with Divine Precision! (+${150 * comboCount})"
                SacredAudioEngine.playPowerUp()
                VibrationHelper.vibrateSuccess(context)

                if (currentVerseIndex + 1 < challenges.size) {
                    currentVerseIndex++
                } else {
                    isGameFinished = true
                    SacredAudioEngine.playOmResonance()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF221133), TempleDark)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            // Top Navigation & Score Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RadiantGold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VYASA'S SCRIBE CHALLENGE", color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("The Unbroken Mahabharata Verse Scribe", color = SandalwoodCream.copy(alpha = 0.8f), fontSize = 11.sp)
                }

                Surface(
                    color = TempleCard,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Score: $score",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = DivineGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Ink Flow Gauge Bar & Tusk Power status
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Sacred Ink Flow", color = SandalwoodCream, fontSize = 12.sp)
                    Text(
                        text = if (tuskSacrificeAvailable) "🪶 Broken Tusk Ready" else "⚡ Devotion Active",
                        color = if (tuskSacrificeAvailable) RadiantGold else CelestialTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { inkFlowGauge },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = RadiantGold,
                    trackColor = TempleCard
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ancient Palm Leaf Scroll / Inscription Surface
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C223B)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DivineGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Verse header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = SaffronPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "EPIC MAHABHARATA CHAPTER ${currentVerseIndex + 1} / ${challenges.size}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = RadiantGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentVerse.shlokaSanskrit,
                            color = SandalwoodCream,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“${currentVerse.meaning}”",
                            color = DivineGold.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Sequence slots
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isShowingSequence) "Sage Vyasa is dictating... Listen carefully!" else "Inscribe the sacred glyphs in unbroken rhythm:",
                            color = if (isShowingSequence) RadiantGold else SandalwoodCream.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            currentVerse.sequenceGlyphIds.forEachIndexed { index, glyphId ->
                                val glyph = glyphs.first { it.id == glyphId }
                                val isHighlighted = activeHighlightedGlyph == glyphId
                                val isFilledByUser = userTappedSequence.size > index && userTappedSequence[index] == glyphId

                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isHighlighted) RadiantGold
                                            else if (isFilledByUser) SacredGreen.copy(alpha = 0.85f)
                                            else TempleDark
                                        )
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isHighlighted) Color.White else DivineGold.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isFilledByUser || isHighlighted) glyph.symbol else "•",
                                        color = if (isHighlighted) TempleDark else SandalwoodCream,
                                        fontSize = if (isFilledByUser || isHighlighted) 22.sp else 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Dynamic Notification / Feedback banner
                    AnimatedVisibility(
                        visible = bannerMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = bannerMessage ?: "",
                            color = RadiantGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glyphs Keyboard (The 5 sacred tools of transcription)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    glyphs.forEach { glyph ->
                        val isHighlighted = activeHighlightedGlyph == glyph.id

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(if (isHighlighted) RadiantGold else TempleCard)
                                    .border(2.dp, glyph.color, CircleShape)
                                    .clickable(enabled = !isShowingSequence) {
                                        handleGlyphTap(glyph.id)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = glyph.symbol,
                                    fontSize = 26.sp,
                                    color = if (isHighlighted) TempleDark else glyph.color
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = glyph.name, color = SandalwoodCream.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Story Intro Dialog
        if (showIntroModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(24.dp),
                    colors = CardDefaults.cardColors(containerColor = TempleDark),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("THE SACRED SCRIBE", color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Sage Vyasa composed the epic Mahabharata and needed a scribe who could write without pause.\n\nLord Ganesha took on the task with complete devotion. When his quill snapped, he broke his own right tusk to continue writing without dropping a single syllable!\n\nListen to Vyasa's dictation rhythm, and tap the sacred symbols in unbroken focus.",
                            color = SandalwoodCream,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                showIntroModal = false
                                SacredAudioEngine.playTempleBell()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("BEGIN INSCRIBING", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Victory Dialog
        if (isGameFinished) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(24.dp),
                    colors = CardDefaults.cardColors(containerColor = TempleDark),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("EPIC SCRIBED WITHOUT PAUSE!", color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sage Vyasa and Lord Ganesha bless Mushika for preserving the eternal wisdom of the Mahabharata.",
                            color = SandalwoodCream,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Final Score: $score", color = DivineGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Yatra Journey: +450m Progress", color = CelestialTeal, fontSize = 14.sp)
                        Text(text = "Prasad: +30 Modaks", color = RadiantGold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                onFinishGame(score, 30, 450)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("CLAIM EPIC MERIT", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
