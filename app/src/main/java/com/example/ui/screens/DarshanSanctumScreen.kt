package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DarshanSanctumScreen(
    totalModaks: Int,
    onOfferModak: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var aartiOfferingsCount by remember { mutableIntStateOf(0) }
    var bellRungCount by remember { mutableIntStateOf(0) }
    var blessingMessage by remember {
        mutableStateOf("Welcome to the Kailash Sanctum. Lord Ganesha welcomes his loyal mount Mushika!")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_pulse"
    )

    val aartiAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aarti_rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF3B1212), Color(0xFF1E0E2B), TempleDark)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
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
                    Text("GRAND KAILASH SANCTUM", color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Divine Darshan of Lord Ganesha", color = SandalwoodCream.copy(alpha = 0.8f), fontSize = 11.sp)
                }

                Surface(
                    color = TempleCard,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪷 $totalModaks Modaks", color = RadiantGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Divine Darshan Canvas: Lord Ganesha on Lotus Throne & Devoted Mushika
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val width = constraints.maxWidth.toFloat()
                val height = constraints.maxHeight.toFloat()
                val center = Offset(width / 2f, height * 0.46f)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawGaneshaSanctum(center, width, height, haloPulse, aartiAngle)
                }
            }

            // Blessing dialogue banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = TempleCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DivineGold.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "॥ वक्रतुण्डाय हुम् ॥",
                        color = RadiantGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = blessingMessage,
                        color = SandalwoodCream,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sacred Devotional Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Ring Temple Bell
                DevotionalActionButton(
                    icon = "🔔",
                    title = "Ring Bell",
                    subtitle = "Sound of Om",
                    onClick = {
                        bellRungCount++
                        SacredAudioEngine.playTempleBell()
                        VibrationHelper.vibrate(context, 40)
                        blessingMessage = "The cosmic bell reverberates across Kailash. Obstacles are cleared!"
                    }
                )

                // Offer Modak Prasad
                DevotionalActionButton(
                    icon = "🥟",
                    title = "Offer Modak",
                    subtitle = if (totalModaks > 0) "-1 Modak" else "Need Modak",
                    enabled = totalModaks > 0,
                    onClick = {
                        onOfferModak()
                        aartiOfferingsCount++
                        SacredAudioEngine.playPowerUp()
                        VibrationHelper.vibrateSuccess(context)
                        val quotes = listOf(
                            "Ganesha smiles with pure delight! Mushika's devotion is rewarded with boundless energy.",
                            "Sacred Modak offered! Wisdom and sweet peace fill Mushika's heart.",
                            "Lord Ganesha gently strokes Mushika's head, granting him strength beyond measure!"
                        )
                        blessingMessage = quotes.random()
                    }
                )

                // Perform Aarti
                DevotionalActionButton(
                    icon = "🪔",
                    title = "Wave Aarti",
                    subtitle = "Dispels Darkness",
                    onClick = {
                        aartiOfferingsCount++
                        SacredAudioEngine.playOmResonance()
                        VibrationHelper.vibrate(context, 60)
                        blessingMessage = "The glowing flame of devotion purifies the mind, dissolving ego and fear."
                    }
                )
            }
        }
    }
}

@Composable
private fun DevotionalActionButton(
    icon: String,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = enabled) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (enabled) SaffronPrimary else TempleCard)
                .border(2.dp, DivineGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 26.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = SandalwoodCream, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text = subtitle, color = DivineGold.copy(alpha = 0.8f), fontSize = 10.sp)
    }
}

private fun DrawScope.drawGaneshaSanctum(
    center: Offset,
    width: Float,
    height: Float,
    haloPulse: Float,
    aartiAngle: Float
) {
    // Temple sanctum stone pillars on both sides
    val pillarWidth = 36f
    drawRoundRect(
        color = Color(0xFF4C1D95).copy(alpha = 0.5f),
        topLeft = Offset(16f, 0f),
        size = Size(pillarWidth, height),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = Color(0xFF4C1D95).copy(alpha = 0.5f),
        topLeft = Offset(width - 16f - pillarWidth, 0f),
        size = Size(pillarWidth, height),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Radiant Divine Halo behind Lord Ganesha
    val haloRadius = 110f * haloPulse
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(RadiantGold.copy(alpha = 0.7f), SaffronPrimary.copy(alpha = 0.3f), Color.Transparent),
            center = center,
            radius = haloRadius
        ),
        radius = haloRadius,
        center = center
    )
    drawCircle(
        color = RadiantGold.copy(alpha = 0.6f),
        radius = haloRadius * 0.75f,
        center = center,
        style = Stroke(width = 2.5f)
    )

    // Lotus Throne Base (Pink & Gold Petals)
    val lotusWidth = 180f
    val lotusHeight = 44f
    val lotusTop = center.y + 65f

    drawOval(
        color = LotusPink,
        topLeft = Offset(center.x - lotusWidth / 2f, lotusTop),
        size = Size(lotusWidth, lotusHeight)
    )
    drawOval(
        color = RadiantGold,
        topLeft = Offset(center.x - lotusWidth * 0.4f, lotusTop + 6f),
        size = Size(lotusWidth * 0.8f, lotusHeight * 0.6f)
    )

    // Lord Ganesha Form (Iconic artistic silhouette with trunk, crown, ears, modak in left hand, blessing abhaya mudra right hand)
    // Seated Torso (Golden Sandalwood)
    drawRoundRect(
        color = Color(0xFFFED7AA),
        topLeft = Offset(center.x - 48f, center.y - 30f),
        size = Size(96f, 85f),
        cornerRadius = CornerRadius(24f, 24f)
    )
    // Saffron Dhoti
    drawRoundRect(
        color = SaffronPrimary,
        topLeft = Offset(center.x - 52f, center.y + 25f),
        size = Size(104f, 45f),
        cornerRadius = CornerRadius(16f, 16f)
    )

    // Large Majestic Ears
    drawOval(
        color = Color(0xFFFED7AA),
        topLeft = Offset(center.x - 82f, center.y - 75f),
        size = Size(46f, 55f)
    )
    drawOval(
        color = Color(0xFFFED7AA),
        topLeft = Offset(center.x + 36f, center.y - 75f),
        size = Size(46f, 55f)
    )

    // Ganesha Head
    drawCircle(
        color = Color(0xFFFED7AA),
        radius = 42f,
        center = Offset(center.x, center.y - 50f)
    )

    // Sacred Mukuta (Golden Royal Crown)
    val crownPath = Path().apply {
        moveTo(center.x, center.y - 120f)
        lineTo(center.x + 28f, center.y - 82f)
        lineTo(center.x - 28f, center.y - 82f)
        close()
    }
    drawPath(crownPath, color = RadiantGold)
    drawCircle(color = Color(0xFFDC2626), radius = 6f, center = Offset(center.x, center.y - 105f))

    // Red Tilak & Trishul Mark on Forehead
    drawLine(
        color = Color(0xFFDC2626),
        start = Offset(center.x, center.y - 74f),
        end = Offset(center.x, center.y - 58f),
        strokeWidth = 4f
    )
    drawCircle(color = RadiantGold, radius = 3.5f, center = Offset(center.x, center.y - 54f))

    // Curved Trunk (turning gracefully left towards modak bowl)
    val trunkPath = Path().apply {
        moveTo(center.x - 8f, center.y - 50f)
        cubicTo(
            center.x - 12f, center.y - 20f,
            center.x - 42f, center.y - 10f,
            center.x - 44f, center.y - 30f
        )
    }
    drawPath(trunkPath, color = Color(0xFFFED7AA), style = Stroke(width = 16f))

    // Modak in Ganesha's Hand
    drawCircle(color = RadiantGold, radius = 12f, center = Offset(center.x - 52f, center.y - 30f))

    // Gentle Eyes
    drawCircle(color = Color(0xFF1E293B), radius = 3f, center = Offset(center.x - 14f, center.y - 60f))
    drawCircle(color = Color(0xFF1E293B), radius = 3f, center = Offset(center.x + 14f, center.y - 60f))

    // Devoted Bodybuilder Mushika standing proudly beside Lord Ganesha's throne!
    val mushikaX = center.x + 85f
    val mushikaY = center.y + 80f

    // Mushika flexing bicep in reverence
    drawCircle(color = Color(0xFFFDE68A), radius = 18f, center = Offset(mushikaX, mushikaY - 14f))
    drawCircle(color = Color(0xFFFDE68A), radius = 10f, center = Offset(mushikaX - 10f, mushikaY - 30f))
    drawCircle(color = Color(0xFFFDE68A), radius = 10f, center = Offset(mushikaX + 10f, mushikaY - 30f))
    // Muscular body
    drawRoundRect(
        color = Color(0xFFFEF3C7),
        topLeft = Offset(mushikaX - 16f, mushikaY - 4f),
        size = Size(32f, 26f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    // Saffron Dhoti
    drawRoundRect(
        color = SaffronPrimary,
        topLeft = Offset(mushikaX - 14f, mushikaY + 12f),
        size = Size(28f, 16f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Aarti Lamp rotating in circular reverence
    val aartiRad = Math.toRadians(aartiAngle.toDouble()).toFloat()
    val aartiRadius = 140f
    val ax = center.x + aartiRadius * cos(aartiRad)
    val ay = (center.y + 20f) + aartiRadius * 0.4f * sin(aartiRad)

    drawCircle(color = RadiantGold.copy(alpha = 0.4f), radius = 16f, center = Offset(ax, ay))
    drawCircle(color = RadiantGold, radius = 9f, center = Offset(ax, ay))
    drawCircle(color = Color.White, radius = 4f, center = Offset(ax, ay))
}
