package com.example.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SacredAudioEngine
import com.example.audio.VibrationHelper
import com.example.ui.theme.DivineGold
import com.example.ui.theme.LotusPink
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SacredGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SandalwoodCream
import com.example.ui.theme.TempleCard
import com.example.ui.theme.TempleDark
import kotlin.math.sin
import kotlin.random.Random

enum class PathwayTheme(val title: String, val bgTop: Color, val bgBottom: Color, val pathColor: Color) {
    FOREST("Naimisha Forest", Color(0xFF0D2818), Color(0xFF1E3A1E), Color(0xFF5D4037)),
    MOUNTAIN("Vindhya Ascent", Color(0xFF1B263B), Color(0xFF415A77), Color(0xFF6B705C)),
    TEMPLE("Kailash Sanctum", Color(0xFF2E1065), Color(0xFF581C87), Color(0xFFB45309))
}

enum class ViceType(val displayName: String, val virtueLesson: String, val color: Color, val requiresDuck: Boolean = false) {
    EGO("Ahamkara (Ego)", "Humility brings divine strength", Color(0xFF64748B)),
    GREED("Lobha (Greed)", "Contentment is the greatest treasure", Color(0xFFD97706)),
    ANGER("Krodha (Anger)", "Patience and peace overcome all fire", Color(0xFFEF4444)),
    FEAR("Bhaya (Fear)", "Faith in Ganesha dispels darkness", Color(0xFF7C3AED)),
    CONFUSION("Moha (Confusion)", "Wisdom pierces through illusion", Color(0xFF0284C7), requiresDuck = true)
}

enum class CollectibleType {
    MODAK,
    LOTUS,
    DIYA,
    POWERUP_TRISHULA,
    POWERUP_MAGNET,
    POWERUP_SURGE
}

data class RunnerItem(
    val id: Long,
    val lane: Int, // -1: Left, 0: Mid, 1: Right
    var y: Float,  // 0f (horizon) to 1f (camera)
    val isObstacle: Boolean,
    val viceType: ViceType? = null,
    val collectibleType: CollectibleType? = null
)

@Composable
fun TempleRunnerScreen(
    mushikaMuscleLevel: Int = 1,
    equippedDhoti: String = "Saffron Gold",
    onFinishRun: (distanceMeters: Int, modaks: Int, flowers: Int, diyas: Int, score: Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Game state
    var isPlaying by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var distanceMeters by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var hearts by remember { mutableIntStateOf(3) }
    var modaksCollected by remember { mutableIntStateOf(0) }
    var flowersCollected by remember { mutableIntStateOf(0) }
    var diyasCollected by remember { mutableIntStateOf(0) }

    // Lane positioning: -1 (left), 0 (center), 1 (right)
    var currentLane by remember { mutableIntStateOf(0) }
    val animatedLaneX = remember { Animatable(0f) }

    // Mushika vertical state (Jump / Slide)
    var isJumping by remember { mutableStateOf(false) }
    var jumpProgress by remember { mutableFloatStateOf(0f) }
    var isDucking by remember { mutableStateOf(false) }
    var duckProgress by remember { mutableFloatStateOf(0f) }

    // Power-up states
    var trishulaActiveTime by remember { mutableFloatStateOf(0f) }
    var magnetActiveTime by remember { mutableFloatStateOf(0f) }
    var surgeActiveTime by remember { mutableFloatStateOf(0f) }

    // Obstacles and collectibles stream
    val items = remember { mutableStateListOf<RunnerItem>() }
    var nextItemId by remember { mutableLongStateOf(1L) }
    var spawnTimer by remember { mutableFloatStateOf(0f) }
    var gameTimeSeconds by remember { mutableFloatStateOf(0f) }

    // Current pathway theme shifts with distance
    val currentTheme = when {
        distanceMeters > 1600 -> PathwayTheme.TEMPLE
        distanceMeters > 700 -> PathwayTheme.MOUNTAIN
        else -> PathwayTheme.FOREST
    }

    // Lane transition animation
    LaunchedEffect(currentLane) {
        animatedLaneX.animateTo(
            targetValue = currentLane.toFloat(),
            animationSpec = tween(durationMillis = 140)
        )
    }

    // Touch actions
    val moveLeft = {
        if (currentLane > -1 && isPlaying && !isPaused) {
            currentLane--
            VibrationHelper.vibrate(context, 15)
        }
    }

    val moveRight = {
        if (currentLane < 1 && isPlaying && !isPaused) {
            currentLane++
            VibrationHelper.vibrate(context, 15)
        }
    }

    val jump = {
        if (!isJumping && !isDucking && isPlaying && !isPaused) {
            isJumping = true
            jumpProgress = 0f
            SacredAudioEngine.playJump()
            VibrationHelper.vibrate(context, 25)
        }
    }

    val slide = {
        if (!isJumping && !isDucking && isPlaying && !isPaused) {
            isDucking = true
            duckProgress = 0f
            VibrationHelper.vibrate(context, 25)
        }
    }

    // Main 60 FPS Game Loop
    LaunchedEffect(isPlaying, isPaused, isGameOver) {
        var lastTime = 0L
        while (isPlaying && !isPaused && !isGameOver) {
            withFrameNanos { now ->
                if (lastTime == 0L) lastTime = now
                val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0f, 0.05f)
                lastTime = now

                gameTimeSeconds += dt

                // Base speed increases with distance, boosts with Prasad Surge
                val speedMultiplier = if (surgeActiveTime > 0f) 1.6f else 1.0f
                val currentSpeed = (0.45f + (distanceMeters / 3000f) * 0.25f) * speedMultiplier

                distanceMeters += (dt * currentSpeed * 28).toInt()
                score += (dt * 15 * speedMultiplier).toInt()

                // Decrement powerups
                if (trishulaActiveTime > 0f) trishulaActiveTime = (trishulaActiveTime - dt).coerceAtLeast(0f)
                if (magnetActiveTime > 0f) magnetActiveTime = (magnetActiveTime - dt).coerceAtLeast(0f)
                if (surgeActiveTime > 0f) surgeActiveTime = (surgeActiveTime - dt).coerceAtLeast(0f)

                // Handle jump physics (muscle power extends jump hang-time)
                if (isJumping) {
                    val jumpDuration = 0.55f + (mushikaMuscleLevel * 0.03f)
                    jumpProgress += dt / jumpDuration
                    if (jumpProgress >= 1f) {
                        isJumping = false
                        jumpProgress = 0f
                    }
                }

                // Handle duck / slide physics
                if (isDucking) {
                    duckProgress += dt / 0.5f
                    if (duckProgress >= 1f) {
                        isDucking = false
                        duckProgress = 0f
                    }
                }

                // Item Spawning
                spawnTimer += dt * speedMultiplier
                if (spawnTimer > 1.1f) {
                    spawnTimer = 0f
                    val lane = Random.nextInt(-1, 2)
                    val isObs = Random.nextFloat() < 0.55f

                    if (isObs) {
                        val vice = ViceType.values().random()
                        items.add(RunnerItem(nextItemId++, lane, 0f, true, viceType = vice))
                    } else {
                        val colType = when {
                            Random.nextFloat() < 0.12f -> CollectibleType.POWERUP_TRISHULA
                            Random.nextFloat() < 0.15f -> CollectibleType.POWERUP_MAGNET
                            Random.nextFloat() < 0.18f -> CollectibleType.POWERUP_SURGE
                            Random.nextFloat() < 0.40f -> CollectibleType.DIYA
                            Random.nextFloat() < 0.65f -> CollectibleType.LOTUS
                            else -> CollectibleType.MODAK
                        }
                        items.add(RunnerItem(nextItemId++, lane, 0f, false, collectibleType = colType))
                    }
                }

                // Update items along highway perspective
                val playerY = 0.82f
                val iterator = items.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    item.y += dt * currentSpeed

                    // Magnet pulling items toward player lane
                    if (!item.isObstacle && magnetActiveTime > 0f && item.y > 0.35f) {
                        // pull into player's lane
                    }

                    // Collision check with player
                    if (item.y in (playerY - 0.08f)..(playerY + 0.08f)) {
                        val laneMatches = (item.lane == currentLane) ||
                                (!item.isObstacle && magnetActiveTime > 0f)

                        if (laneMatches) {
                            if (item.isObstacle) {
                                val requiresDuck = item.viceType?.requiresDuck == true
                                val evadedByJump = isJumping && !requiresDuck && jumpProgress in 0.15f..0.85f
                                val evadedByDuck = isDucking && requiresDuck

                                if (trishulaActiveTime > 0f) {
                                    // Smashed by Trishula Shield!
                                    SacredAudioEngine.playPowerUp()
                                    VibrationHelper.vibrateSuccess(context)
                                    score += 50
                                    iterator.remove()
                                } else if (evadedByJump || evadedByDuck) {
                                    // Evaded successfully
                                    score += 20
                                } else {
                                    // Hit obstacle!
                                    SacredAudioEngine.playObstacleThud()
                                    VibrationHelper.vibrate(context, 80)
                                    hearts--
                                    iterator.remove()
                                    if (hearts <= 0) {
                                        isGameOver = true
                                        isPlaying = false
                                    }
                                }
                            } else {
                                // Collectible picked up!
                                when (item.collectibleType) {
                                    CollectibleType.MODAK -> {
                                        modaksCollected++
                                        score += 15
                                        SacredAudioEngine.playModakChime()
                                        VibrationHelper.vibrate(context, 20)
                                    }
                                    CollectibleType.LOTUS -> {
                                        flowersCollected++
                                        score += 30
                                        SacredAudioEngine.playTempleBell()
                                        VibrationHelper.vibrate(context, 25)
                                    }
                                    CollectibleType.DIYA -> {
                                        diyasCollected++
                                        score += 50
                                        SacredAudioEngine.playTempleBell()
                                        VibrationHelper.vibrate(context, 35)
                                    }
                                    CollectibleType.POWERUP_TRISHULA -> {
                                        trishulaActiveTime = 7.5f
                                        SacredAudioEngine.playPowerUp()
                                        VibrationHelper.vibrateSuccess(context)
                                    }
                                    CollectibleType.POWERUP_MAGNET -> {
                                        magnetActiveTime = 8.0f
                                        SacredAudioEngine.playPowerUp()
                                        VibrationHelper.vibrateSuccess(context)
                                    }
                                    CollectibleType.POWERUP_SURGE -> {
                                        surgeActiveTime = 6.0f
                                        SacredAudioEngine.playPowerUp()
                                        VibrationHelper.vibrateSuccess(context)
                                    }
                                    null -> {}
                                }
                                iterator.remove()
                            }
                        }
                    } else if (item.y > 1.1f) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.bgTop)
    ) {
        // Highway Canvas
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var totalDragX = 0f
                    var totalDragY = 0f
                    detectDragGestures(
                        onDragStart = {
                            totalDragX = 0f
                            totalDragY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y
                        },
                        onDragEnd = {
                            if (kotlin.math.abs(totalDragX) > kotlin.math.abs(totalDragY)) {
                                if (totalDragX > 50f) moveRight()
                                else if (totalDragX < -50f) moveLeft()
                            } else {
                                if (totalDragY < -50f) jump()
                                else if (totalDragY > 50f) slide()
                            }
                        }
                    )
                }
        ) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawTempleHighway(
                    width = width,
                    height = height,
                    theme = currentTheme,
                    gameTime = gameTimeSeconds
                )

                // Draw items
                for (item in items) {
                    drawRunnerItem(
                        item = item,
                        width = width,
                        height = height,
                        gameTime = gameTimeSeconds
                    )
                }

                // Draw Mushika the bodybuilder mouse
                val playerY = height * 0.82f
                val playerX = getLaneCenterX(animatedLaneX.value, width, 0.82f)

                // Jump height curve
                val jumpOffset = if (isJumping) {
                    sin(jumpProgress * Math.PI).toFloat() * 120f
                } else 0f

                val isDuckingNow = isDucking

                drawMushikaHero(
                    x = playerX,
                    y = playerY - jumpOffset,
                    isDucking = isDuckingNow,
                    isInvincible = trishulaActiveTime > 0f,
                    isSurging = surgeActiveTime > 0f,
                    gameTime = gameTimeSeconds,
                    equippedDhoti = equippedDhoti,
                    mushikaMuscleLevel = mushikaMuscleLevel
                )
            }
        }

        // Top HUD Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (isGameOver) onFinishRun(distanceMeters, modaksCollected, flowersCollected, diyasCollected, score)
                        else isPaused = !isPaused
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = TempleCard.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = RadiantGold
                    )
                }

                // Distance & Theme
                Surface(
                    color = TempleCard.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${distanceMeters}m",
                            color = DivineGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•  ${currentTheme.title}",
                            color = SandalwoodCream,
                            fontSize = 12.sp
                        )
                    }
                }

                // Hearts
                Row(
                    modifier = Modifier
                        .background(TempleCard.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i ->
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Heart",
                            tint = if (i < hearts) LotusPink else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score & Offerings Counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OfferingBadge(label = "Modaks", value = modaksCollected, color = RadiantGold)
                    OfferingBadge(label = "Lotuses", value = flowersCollected, color = LotusPink)
                    OfferingBadge(label = "Diyas", value = diyasCollected, color = DivineGold)
                }

                Surface(
                    color = SaffronPrimary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Score: $score",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Power-up status indicators
            if (trishulaActiveTime > 0f || magnetActiveTime > 0f || surgeActiveTime > 0f) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (trishulaActiveTime > 0f) {
                        PowerUpPill("Trishula Shield", trishulaActiveTime, RadiantGold)
                    }
                    if (magnetActiveTime > 0f) {
                        PowerUpPill("Prasad Magnet", magnetActiveTime, SacredGreen)
                    }
                    if (surgeActiveTime > 0f) {
                        PowerUpPill("Prasad Surge 2x", surgeActiveTime, SaffronSecondary)
                    }
                }
            }
        }

        // Bottom Controls: Big ergonomic buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left & Right Lane Switch Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ArcadeControlButton(
                    icon = Icons.Default.ArrowLeft,
                    label = "LEFT",
                    onClick = moveLeft
                )
                ArcadeControlButton(
                    icon = Icons.Default.ArrowRight,
                    label = "RIGHT",
                    onClick = moveRight
                )
            }

            // Jump & Duck Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ArcadeControlButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "JUMP",
                    highlight = true,
                    onClick = jump
                )
                ArcadeControlButton(
                    icon = Icons.Default.ArrowDownward,
                    label = "SLIDE",
                    onClick = slide
                )
            }
        }

        // Game Over Overlay
        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TempleDark),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "YATRA CHECKPOINT",
                            color = RadiantGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mushika made brave progress!",
                            color = SandalwoodCream,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        SummaryStatRow("Distance Journeyed", "${distanceMeters}m")
                        SummaryStatRow("Modaks Collected", "$modaksCollected")
                        SummaryStatRow("Lotus Offerings", "$flowersCollected")
                        SummaryStatRow("Sacred Diyas", "$diyasCollected")
                        SummaryStatRow("Total Run Score", "$score")

                        Spacer(modifier = Modifier.height(22.dp))

                        Button(
                            onClick = {
                                onFinishRun(distanceMeters, modaksCollected, flowersCollected, diyasCollected, score)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("SAVE TO YATRA MAP", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                // Restart run
                                hearts = 3
                                score = 0
                                distanceMeters = 0
                                modaksCollected = 0
                                flowersCollected = 0
                                diyasCollected = 0
                                items.clear()
                                isGameOver = false
                                isPlaying = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TempleCard),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = DivineGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RUN AGAIN", color = DivineGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Pause Dialog
        if (isPaused && !isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    colors = CardDefaults.cardColors(containerColor = TempleDark),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Game Paused", color = DivineGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { isPaused = false },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("RESUME YATRA", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onFinishRun(distanceMeters, modaksCollected, flowersCollected, diyasCollected, score)
                            },
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

@Composable
private fun OfferingBadge(label: String, value: Int, color: Color) {
    Surface(
        color = TempleCard.copy(alpha = 0.85f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "$value",
                color = SandalwoodCream,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun PowerUpPill(name: String, timeLeft: Float, color: Color) {
    Surface(
        color = color.copy(alpha = 0.25f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "$name (${String.format("%.1f", timeLeft)}s)",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ArcadeControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (highlight) SaffronPrimary else TempleCard
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (highlight) Color.White else RadiantGold,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = SandalwoodCream.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SummaryStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = SandalwoodCream.copy(alpha = 0.8f), fontSize = 14.sp)
        Text(text = value, color = DivineGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

// -------------------------------------------------------------
// Canvas Drawing Functions for Temple Runner
// -------------------------------------------------------------

private fun DrawScope.drawTempleHighway(
    width: Float,
    height: Float,
    theme: PathwayTheme,
    gameTime: Float
) {
    // Sky / Atmosphere gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(theme.bgTop, theme.bgBottom),
            startY = 0f,
            endY = height * 0.45f
        ),
        size = Size(width, height * 0.45f)
    )

    // Horizon line
    val horizonY = height * 0.35f
    val roadTopWidth = width * 0.22f
    val roadBottomWidth = width * 0.94f

    val roadTopLeft = (width - roadTopWidth) / 2f
    val roadTopRight = roadTopLeft + roadTopWidth
    val roadBottomLeft = (width - roadBottomWidth) / 2f
    val roadBottomRight = roadBottomLeft + roadBottomWidth

    // Mountain/Forest silhouettes on horizon
    val mountainPath = Path().apply {
        moveTo(0f, horizonY)
        for (i in 0..10) {
            val px = (width / 10f) * i
            val py = horizonY - 40f - (if (i % 2 == 0) 30f else 0f)
            lineTo(px, py)
        }
        lineTo(width, horizonY)
        close()
    }
    drawPath(mountainPath, color = theme.bgTop.copy(alpha = 0.6f))

    // Main 3D Perspective Pathway
    val roadPath = Path().apply {
        moveTo(roadTopLeft, horizonY)
        lineTo(roadTopRight, horizonY)
        lineTo(roadBottomRight, height)
        lineTo(roadBottomLeft, height)
        close()
    }
    drawPath(roadPath, color = theme.pathColor)

    // Temple borders / stone curbs with marigold garland pattern
    val curbWidth = 14f
    drawLine(
        color = RadiantGold.copy(alpha = 0.7f),
        start = Offset(roadTopLeft, horizonY),
        end = Offset(roadBottomLeft, height),
        strokeWidth = curbWidth
    )
    drawLine(
        color = RadiantGold.copy(alpha = 0.7f),
        start = Offset(roadTopRight, horizonY),
        end = Offset(roadBottomRight, height),
        strokeWidth = curbWidth
    )

    // Lane dividing dashed lines
    val lane1TopX = roadTopLeft + roadTopWidth / 3f
    val lane1BottomX = roadBottomLeft + roadBottomWidth / 3f
    val lane2TopX = roadTopLeft + 2 * roadTopWidth / 3f
    val lane2BottomX = roadBottomLeft + 2 * roadBottomWidth / 3f

    // Animated dashed lines moving toward player
    val dashPhase = (gameTime * 4f) % 1f
    for (step in 0..12) {
        val t = ((step / 12f) + dashPhase / 12f).coerceIn(0f, 1f)
        val segY = horizonY + t * (height - horizonY)
        val segLen = 10f + t * 40f

        val x1 = lane1TopX + t * (lane1BottomX - lane1TopX)
        val x2 = lane2TopX + t * (lane2BottomX - lane2TopX)

        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(x1, segY),
            end = Offset(x1, segY + segLen),
            strokeWidth = 2f + t * 4f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(x2, segY),
            end = Offset(x2, segY + segLen),
            strokeWidth = 2f + t * 4f
        )
    }
}

private fun getLaneCenterX(lane: Float, width: Float, yFactor: Float): Float {
    val roadTopWidth = width * 0.22f
    val roadBottomWidth = width * 0.94f
    val currentRoadWidth = roadTopWidth + yFactor * (roadBottomWidth - roadTopWidth)
    val laneWidth = currentRoadWidth / 3f
    val centerX = width / 2f
    return centerX + (lane * laneWidth)
}

private fun DrawScope.drawRunnerItem(
    item: RunnerItem,
    width: Float,
    height: Float,
    gameTime: Float
) {
    val horizonY = height * 0.35f
    val itemY = horizonY + item.y * (height - horizonY)
    val itemX = getLaneCenterX(item.lane.toFloat(), width, item.y)

    // Scale item based on perspective distance (item.y from 0 to 1)
    val scale = (0.25f + item.y * 0.85f).coerceIn(0.2f, 1.2f)

    if (item.isObstacle) {
        val vice = item.viceType ?: ViceType.EGO
        val baseSize = 56f * scale

        if (vice.requiresDuck) {
            // Low stone arch / Confusion illusion barrier (requires ducking)
            drawRoundRect(
                color = vice.color,
                topLeft = Offset(itemX - baseSize * 1.3f, itemY - baseSize * 0.8f),
                size = Size(baseSize * 2.6f, baseSize * 0.5f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Hanging vines
            drawLine(
                color = Color.Cyan.copy(alpha = 0.6f),
                start = Offset(itemX, itemY - baseSize * 0.4f),
                end = Offset(itemX, itemY + baseSize * 0.2f),
                strokeWidth = 3f * scale
            )
        } else {
            // Upright obstacle (Stone monolith / Fire / Thorns)
            drawRoundRect(
                color = vice.color,
                topLeft = Offset(itemX - baseSize / 2f, itemY - baseSize),
                size = Size(baseSize, baseSize),
                cornerRadius = CornerRadius(10f * scale, 10f * scale)
            )
            // Emblem on obstacle
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                radius = baseSize * 0.25f,
                center = Offset(itemX, itemY - baseSize / 2f)
            )
        }
    } else {
        // Collectible items
        val baseRadius = 22f * scale
        when (item.collectibleType) {
            CollectibleType.MODAK -> {
                // Sacred Golden Modak (teardrop shape)
                val modakPath = Path().apply {
                    moveTo(itemX, itemY - baseRadius * 1.5f)
                    cubicTo(
                        itemX - baseRadius, itemY - baseRadius * 0.5f,
                        itemX - baseRadius * 1.2f, itemY + baseRadius * 0.4f,
                        itemX, itemY + baseRadius * 0.5f
                    )
                    cubicTo(
                        itemX + baseRadius * 1.2f, itemY + baseRadius * 0.4f,
                        itemX + baseRadius, itemY - baseRadius * 0.5f,
                        itemX, itemY - baseRadius * 1.5f
                    )
                    close()
                }
                drawPath(modakPath, color = RadiantGold)
                // Modak top saffron tip
                drawCircle(
                    color = SaffronPrimary,
                    radius = baseRadius * 0.2f,
                    center = Offset(itemX, itemY - baseRadius * 1.5f)
                )
            }
            CollectibleType.LOTUS -> {
                // Sacred Pink Lotus
                drawCircle(color = LotusPink, radius = baseRadius, center = Offset(itemX, itemY))
                drawCircle(color = Color.White, radius = baseRadius * 0.5f, center = Offset(itemX, itemY))
                drawCircle(color = RadiantGold, radius = baseRadius * 0.25f, center = Offset(itemX, itemY))
            }
            CollectibleType.DIYA -> {
                // Sacred Golden Diya with radiant flickering flame
                drawArc(
                    color = Color(0xFFD97706),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(itemX - baseRadius, itemY - baseRadius * 0.3f),
                    size = Size(baseRadius * 2f, baseRadius * 1.2f)
                )
                // Flame
                val flameFlicker = sin(gameTime * 12f) * 2f
                drawCircle(
                    color = RadiantGold,
                    radius = baseRadius * 0.45f + flameFlicker,
                    center = Offset(itemX, itemY - baseRadius * 0.6f)
                )
            }
            CollectibleType.POWERUP_TRISHULA -> {
                // Trishula Powerup
                drawCircle(color = RadiantGold.copy(alpha = 0.3f), radius = baseRadius * 1.4f, center = Offset(itemX, itemY))
                drawCircle(color = SaffronPrimary, radius = baseRadius, center = Offset(itemX, itemY))
                // Trident prong
                drawLine(
                    color = Color.White,
                    start = Offset(itemX, itemY + baseRadius * 0.7f),
                    end = Offset(itemX, itemY - baseRadius * 0.7f),
                    strokeWidth = 3f * scale
                )
            }
            CollectibleType.POWERUP_MAGNET -> {
                drawCircle(color = SacredGreen, radius = baseRadius, center = Offset(itemX, itemY))
                drawCircle(color = Color.White, radius = baseRadius * 0.4f, center = Offset(itemX, itemY))
            }
            CollectibleType.POWERUP_SURGE -> {
                drawCircle(color = SaffronSecondary, radius = baseRadius, center = Offset(itemX, itemY))
                drawCircle(color = RadiantGold, radius = baseRadius * 0.5f, center = Offset(itemX, itemY))
            }
            null -> {}
        }
    }
}

private fun DrawScope.drawMushikaHero(
    x: Float,
    y: Float,
    isDucking: Boolean,
    isInvincible: Boolean,
    isSurging: Boolean,
    gameTime: Float,
    equippedDhoti: String,
    mushikaMuscleLevel: Int
) {
    // Stride bobbing animation
    val bob = sin(gameTime * 14f) * 4f
    val currentY = y + bob

    // Divine Aura if powerup active
    if (isInvincible || isSurging) {
        val auraColor = if (isInvincible) RadiantGold else SaffronSecondary
        drawCircle(
            color = auraColor.copy(alpha = 0.35f + sin(gameTime * 8f) * 0.15f),
            radius = 65f,
            center = Offset(x, currentY - 30f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = 80f,
            center = Offset(x, currentY - 30f)
        )
    }

    if (isDucking) {
        // Sliding / Ducking pose (low to ground, muscles flexed flat)
        // Shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.4f),
            topLeft = Offset(x - 45f, currentY + 5f),
            size = Size(90f, 20f)
        )
        // Mouse body low
        drawRoundRect(
            color = Color(0xFFFDE68A),
            topLeft = Offset(x - 35f, currentY - 24f),
            size = Size(70f, 26f),
            cornerRadius = CornerRadius(14f, 14f)
        )
        // Dhoti
        drawRoundRect(
            color = SaffronPrimary,
            topLeft = Offset(x - 15f, currentY - 22f),
            size = Size(30f, 22f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Mouse Ears
        drawCircle(color = Color(0xFFF59E0B), radius = 10f, center = Offset(x + 24f, currentY - 24f))
        return
    }

    // Upright Running Bodybuilder Mushika
    // Ground Shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.35f),
        topLeft = Offset(x - 30f, currentY + 8f),
        size = Size(60f, 16f)
    )

    // Dhoti color lookup
    val dhotiColor = when (equippedDhoti) {
        "Royal Crimson" -> Color(0xFFB91C1C)
        "Temple Peacock" -> Color(0xFF0369A1)
        "Ascetic White" -> Color(0xFFF8FAFC)
        else -> SaffronPrimary
    }

    // Muscular Legs in running stride
    val legPhase = sin(gameTime * 14f)
    drawLine(
        color = Color(0xFFFDE68A),
        start = Offset(x - 12f, currentY - 10f),
        end = Offset(x - 18f - legPhase * 8f, currentY + 12f),
        strokeWidth = 9f
    )
    drawLine(
        color = Color(0xFFFDE68A),
        start = Offset(x + 12f, currentY - 10f),
        end = Offset(x + 18f + legPhase * 8f, currentY + 12f),
        strokeWidth = 9f
    )

    // Dhoti wrap
    val dhotiPath = Path().apply {
        moveTo(x - 18f, currentY - 20f)
        lineTo(x + 18f, currentY - 20f)
        lineTo(x + 14f, currentY)
        lineTo(x - 14f, currentY)
        close()
    }
    drawPath(dhotiPath, color = dhotiColor)
    // Golden waist belt
    drawLine(
        color = RadiantGold,
        start = Offset(x - 18f, currentY - 20f),
        end = Offset(x + 18f, currentY - 20f),
        strokeWidth = 4f
    )

    // Broad Muscular Chest (Mouse Pecs & Abs)
    val chestWidth = 36f + (mushikaMuscleLevel * 1.5f).coerceAtMost(10f)
    drawRoundRect(
        color = Color(0xFFFEF3C7),
        topLeft = Offset(x - chestWidth / 2f, currentY - 44f),
        size = Size(chestWidth, 26f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    // Pectoral definition lines
    drawLine(
        color = Color(0xFFF59E0B),
        start = Offset(x, currentY - 42f),
        end = Offset(x, currentY - 24f),
        strokeWidth = 2f
    )

    // Huge Bodybuilder Biceps (Arms flexing proudly)
    val bicepSize = 14f + (mushikaMuscleLevel * 1.2f).coerceAtMost(8f)
    // Left arm flexed up holding miniature golden modak
    drawCircle(color = Color(0xFFFEF3C7), radius = bicepSize * 0.7f, center = Offset(x - 24f, currentY - 38f))
    drawCircle(color = Color(0xFFFEF3C7), radius = bicepSize * 0.6f, center = Offset(x - 26f, currentY - 50f))
    // Miniature modak in hand
    drawCircle(color = RadiantGold, radius = 7f, center = Offset(x - 28f, currentY - 58f))

    // Right arm flexed
    drawCircle(color = Color(0xFFFEF3C7), radius = bicepSize * 0.7f, center = Offset(x + 24f, currentY - 38f))
    drawCircle(color = Color(0xFFFEF3C7), radius = bicepSize * 0.6f, center = Offset(x + 26f, currentY - 48f))

    // Mouse Head
    drawCircle(color = Color(0xFFFDE68A), radius = 18f, center = Offset(x, currentY - 56f))

    // Big Cute Mouse Ears
    drawCircle(color = Color(0xFFFDE68A), radius = 12f, center = Offset(x - 16f, currentY - 70f))
    drawCircle(color = Color(0xFFF59E0B), radius = 8f, center = Offset(x - 16f, currentY - 70f))
    drawCircle(color = Color(0xFFFDE68A), radius = 12f, center = Offset(x + 16f, currentY - 70f))
    drawCircle(color = Color(0xFFF59E0B), radius = 8f, center = Offset(x + 16f, currentY - 70f))

    // Sacred Tilak on Mushika's Forehead
    drawLine(
        color = Color(0xFFDC2626),
        start = Offset(x, currentY - 65f),
        end = Offset(x, currentY - 58f),
        strokeWidth = 3f
    )
    drawCircle(color = RadiantGold, radius = 2.5f, center = Offset(x, currentY - 55f))

    // Eyes (Determined look)
    drawCircle(color = Color(0xFF1E293B), radius = 2.5f, center = Offset(x - 6f, currentY - 57f))
    drawCircle(color = Color(0xFF1E293B), radius = 2.5f, center = Offset(x + 6f, currentY - 57f))

    // Snout / Cute Nose
    drawCircle(color = Color(0xFFEF4444), radius = 2f, center = Offset(x, currentY - 51f))
}
