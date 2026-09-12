package com.example.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import kotlin.math.cos
import kotlin.math.sin

data class WisdomBindu(
    val id: Int,
    val ringIndex: Int,
    var angleRad: Float,
    val name: String,
    val color: Color
)

data class MayaObstacle(
    val ringIndex: Int,
    var angleRad: Float,
    val speed: Float
)

@Composable
fun PradakshinaScreen(
    onFinishGame: (score: Int, modaksEarned: Int, bonusDistance: Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Ring radii factors: 0: Inner, 1: Middle, 2: Outer
    var playerRing by remember { mutableIntStateOf(1) }
    var playerAngle by remember { mutableFloatStateOf(0f) }

    var completedRounds by remember { mutableFloatStateOf(0f) }
    var targetRounds by remember { mutableIntStateOf(3) }
    var collectedBindusCount by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }

    var isGameOver by remember { mutableStateOf(false) }
    var isVictory by remember { mutableStateOf(false) }
    var showStoryPrompt by remember { mutableStateOf(true) }

    val bindus = remember {
        mutableStateListOf(
            WisdomBindu(1, 0, 0.8f, "Satya (Truth)", DivineGold),
            WisdomBindu(2, 1, 2.2f, "Bhakti (Devotion)", LotusPink),
            WisdomBindu(3, 2, 3.8f, "Jnana (Wisdom)", CelestialTeal),
            WisdomBindu(4, 1, 5.0f, "Dharma (Righteousness)", RadiantGold),
            WisdomBindu(5, 0, 3.4f, "Shanti (Peace)", SacredGreen),
            WisdomBindu(6, 2, 1.4f, "Preeti (Love)", Color(0xFFF472B6)),
            WisdomBindu(7, 1, 0.3f, "Kripa (Grace)", Color(0xFFFDE047))
        )
    }

    val obstacles = remember {
        mutableStateListOf(
            MayaObstacle(0, 2.0f, -0.6f),
            MayaObstacle(1, 4.0f, 0.8f),
            MayaObstacle(1, 1.0f, -0.7f),
            MayaObstacle(2, 5.5f, 0.9f),
            MayaObstacle(2, 2.5f, -0.85f)
        )
    }

    // Toggle ring
    val switchRing = {
        playerRing = (playerRing + 1) % 3
        SacredAudioEngine.playJump()
        VibrationHelper.vibrate(context, 20)
    }

    // Animation Loop
    LaunchedEffect(isGameOver, isVictory, showStoryPrompt) {
        var lastNanos = 0L
        while (!isGameOver && !isVictory && !showStoryPrompt) {
            withFrameNanos { now ->
                if (lastNanos == 0L) lastNanos = now
                val dt = ((now - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                lastNanos = now

                // Player orbits clockwise
                val angularSpeed = 1.05f
                val oldAngle = playerAngle
                playerAngle = (playerAngle + angularSpeed * dt) % (2 * Math.PI.toFloat())

                // Check full round completion
                if (playerAngle < oldAngle) {
                    completedRounds += 1f
                    SacredAudioEngine.playTempleBell()
                    score += 150
                    if (completedRounds >= targetRounds && bindus.isEmpty()) {
                        isVictory = true
                        SacredAudioEngine.playPowerUp()
                        VibrationHelper.vibrateSuccess(context)
                    }
                }

                // Update obstacles
                obstacles.forEach { obs ->
                    obs.angleRad = (obs.angleRad + obs.speed * dt) % (2 * Math.PI.toFloat())
                    if (obs.angleRad < 0f) obs.angleRad += (2 * Math.PI.toFloat())

                    // Check collision
                    if (obs.ringIndex == playerRing) {
                        val angleDiff = kotlin.math.abs(obs.angleRad - playerAngle)
                        val normalizedDiff = kotlin.math.min(angleDiff, (2 * Math.PI.toFloat()) - angleDiff)
                        if (normalizedDiff < 0.22f) {
                            // Hit Maya obstacle!
                            SacredAudioEngine.playObstacleThud()
                            VibrationHelper.vibrate(context, 70)
                            lives--
                            // Push obstacle away to avoid multi-hit
                            obs.angleRad = (obs.angleRad + 1.2f) % (2 * Math.PI.toFloat())
                            if (lives <= 0) {
                                isGameOver = true
                            }
                        }
                    }
                }

                // Check Bindu pickups
                val it = bindus.iterator()
                while (it.hasNext()) {
                    val bindu = it.next()
                    if (bindu.ringIndex == playerRing) {
                        val angleDiff = kotlin.math.abs(bindu.angleRad - playerAngle)
                        val normalizedDiff = kotlin.math.min(angleDiff, (2 * Math.PI.toFloat()) - angleDiff)
                        if (normalizedDiff < 0.28f) {
                            collectedBindusCount++
                            score += 100
                            SacredAudioEngine.playModakChime()
                            VibrationHelper.vibrate(context, 30)
                            it.remove()

                            if (completedRounds >= targetRounds && bindus.isEmpty()) {
                                isVictory = true
                                SacredAudioEngine.playPowerUp()
                                VibrationHelper.vibrateSuccess(context)
                            }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF2A144E), TempleDark)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                    Text(
                        text = "PRADAKSHINA OF WISDOM",
                        color = RadiantGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Circumambulating the Divine Center",
                        color = SandalwoodCream.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

                Surface(
                    color = TempleCard,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "❤️ $lives",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = LotusPink,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pradakshina: ${completedRounds.toInt()} / $targetRounds",
                    color = DivineGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Wisdom Bindus: $collectedBindusCount / 7",
                    color = CelestialTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Score: $score",
                    color = SandalwoodCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Cosmic Orbit Canvas
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { switchRing() }
            ) {
                val width = constraints.maxWidth.toFloat()
                val height = constraints.maxHeight.toFloat()
                val center = Offset(width / 2f, height / 2f)
                val maxRadius = kotlin.math.min(width, height) * 0.44f

                val radii = listOf(
                    maxRadius * 0.42f, // Inner (Chakra 1)
                    maxRadius * 0.70f, // Middle (Chakra 2)
                    maxRadius * 0.98f  // Outer (Chakra 3)
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw divine star dust
                    drawCosmicField(center, maxRadius)

                    // Draw the 3 orbital rings
                    radii.forEachIndexed { index, r ->
                        val isCurrent = index == playerRing
                        drawCircle(
                            color = if (isCurrent) RadiantGold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f),
                            radius = r,
                            center = center,
                            style = Stroke(width = if (isCurrent) 3f else 1.5f)
                        )
                    }

                    // Center: Divine Parents Shiva & Parvati glowing lotus throne
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(RadiantGold, SaffronPrimary, Color.Transparent),
                            center = center,
                            radius = radii[0] * 0.7f
                        ),
                        radius = radii[0] * 0.7f,
                        center = center
                    )
                    drawCircle(
                        color = SandalwoodCream,
                        radius = 24f,
                        center = center
                    )
                    // Sacred Om symbol at center
                    drawCircle(color = SaffronPrimary, radius = 10f, center = center)

                    // Draw Bindus (Wisdom nodes)
                    bindus.forEach { bindu ->
                        val bRadius = radii[bindu.ringIndex]
                        val bx = center.x + bRadius * cos(bindu.angleRad)
                        val by = center.y + bRadius * sin(bindu.angleRad)

                        drawCircle(color = bindu.color.copy(alpha = 0.3f), radius = 20f, center = Offset(bx, by))
                        drawCircle(color = bindu.color, radius = 11f, center = Offset(bx, by))
                        drawCircle(color = Color.White, radius = 5f, center = Offset(bx, by))
                    }

                    // Draw Maya obstacles
                    obstacles.forEach { obs ->
                        val oRadius = radii[obs.ringIndex]
                        val ox = center.x + oRadius * cos(obs.angleRad)
                        val oy = center.y + oRadius * sin(obs.angleRad)

                        // Cosmic whirling void
                        drawCircle(color = Color(0xFF9333EA).copy(alpha = 0.4f), radius = 22f, center = Offset(ox, oy))
                        drawCircle(color = Color(0xFFEF4444), radius = 13f, center = Offset(ox, oy))
                        drawCircle(color = Color.Black, radius = 6f, center = Offset(ox, oy))
                    }

                    // Draw Mushika the bodybuilder mouse orbiting
                    val pRadius = radii[playerRing]
                    val px = center.x + pRadius * cos(playerAngle)
                    val py = center.y + pRadius * sin(playerAngle)

                    // Mushika Devotional Aura
                    drawCircle(
                        color = RadiantGold.copy(alpha = 0.4f),
                        radius = 24f,
                        center = Offset(px, py)
                    )
                    // Mouse body
                    drawCircle(
                        color = Color(0xFFFDE68A),
                        radius = 14f,
                        center = Offset(px, py)
                    )
                    // Golden dhoti accent
                    drawCircle(
                        color = SaffronPrimary,
                        radius = 7f,
                        center = Offset(px, py)
                    )
                    // Tiny ears
                    drawCircle(
                        color = SaffronPrimary,
                        radius = 5f,
                        center = Offset(px - 10f, py - 10f)
                    )
                    drawCircle(
                        color = SaffronPrimary,
                        radius = 5f,
                        center = Offset(px + 10f, py - 10f)
                    )
                }
            }

            // Bottom Ring Shift Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = switchRing,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "SWITCH ORBITAL RING (Current: ${if (playerRing == 0) "INNER" else if (playerRing == 1) "MIDDLE" else "OUTER"})",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap screen or button to jump between orbits. Avoid Maya debris!",
                    color = SandalwoodCream.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }

        // Story intro prompt
        if (showStoryPrompt) {
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
                        Text("THE PRADAKSHINA CHALLENGE", color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Sage Narada asked: 'Who can circle the universe first?'\n\nWhile Kartikeya flew around planets, Lord Ganesha wisely circled his parents Shiva and Parvati, for devotion recognizes that the parents embody all creation.\n\nGuide Mushika in 3 sacred circumambulations, gathering the 7 Wisdom Bindus while navigating past cosmic distractions.",
                            color = SandalwoodCream,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                showStoryPrompt = false
                                SacredAudioEngine.playTempleBell()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("BEGIN PRADAKSHINA", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Victory Dialog
        if (isVictory) {
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
                        Text("DIVINE WISDOM ATTAINED!", color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mushika has completed the sacred circumambulation! Shiva & Parvati bestow their boundless cosmic grace.",
                            color = SandalwoodCream,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Final Score: $score", color = DivineGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Cosmic Distance: +350m Yatra", color = CelestialTeal, fontSize = 14.sp)
                        Text(text = "Prasad: +25 Modaks", color = RadiantGold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                onFinishGame(score, 25, 350)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("CLAIM BLESSINGS", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Game Over Dialog
        if (isGameOver) {
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
                        Text("MAYA OVERWHELMED MUSHIKA", color = LotusPink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Patience is the foundation of wisdom. Mushika returns to meditate.",
                            color = SandalwoodCream,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = {
                                lives = 3
                                score = 0
                                completedRounds = 0f
                                collectedBindusCount = 0
                                isGameOver = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("TRY AGAIN", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onFinishGame(score, 5, 50) },
                            colors = ButtonDefaults.buttonColors(containerColor = TempleCard)
                        ) {
                            Text("RETURN TO HUB", color = SandalwoodCream)
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCosmicField(center: Offset, maxRadius: Float) {
    // Star dots
    for (i in 0..40) {
        val angle = (i * 0.157f)
        val dist = (i * 17f) % maxRadius
        val x = center.x + dist * cos(angle)
        val y = center.y + dist * sin(angle)
        drawCircle(
            color = Color.White.copy(alpha = 0.25f + ((i % 5) * 0.1f)),
            radius = 1.8f,
            center = Offset(x, y)
        )
    }
}
