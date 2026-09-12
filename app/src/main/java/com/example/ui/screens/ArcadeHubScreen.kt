package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SacredAudioEngine
import com.example.audio.VibrationHelper
import com.example.data.Blessing
import com.example.data.StoryChapter
import com.example.data.YatraProgress
import com.example.data.YatraRepository
import com.example.ui.ArcadeScreen
import com.example.ui.HubTab
import com.example.ui.YatraViewModel
import com.example.ui.theme.CelestialTeal
import com.example.ui.theme.DivineGold
import com.example.ui.theme.LotusPink
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SacredGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SandalwoodCream
import com.example.ui.theme.TempleCard
import com.example.ui.theme.TempleCardBorder
import com.example.ui.theme.TempleDark

@Composable
fun ArcadeHubScreen(viewModel: YatraViewModel) {
    val progress by viewModel.progress.collectAsState()
    val currentTab by viewModel.currentHubTab.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val selectedBlessing by viewModel.selectedBlessing.collectAsState()
    val rewardBanner by viewModel.lastGameReward.collectAsState()

    Scaffold(
        containerColor = TempleDark,
        bottomBar = {
            NavigationBar(
                containerColor = TempleCard,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == HubTab.GAMES,
                    onClick = { viewModel.setHubTab(HubTab.GAMES) },
                    icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Games") },
                    label = { Text("Arcade", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TempleDark,
                        selectedTextColor = RadiantGold,
                        indicatorColor = RadiantGold,
                        unselectedIconColor = SandalwoodCream.copy(alpha = 0.6f),
                        unselectedTextColor = SandalwoodCream.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == HubTab.YATRA_MAP,
                    onClick = { viewModel.setHubTab(HubTab.YATRA_MAP) },
                    icon = { Icon(Icons.Default.DirectionsRun, contentDescription = "Yatra Map") },
                    label = { Text("Yatra Map", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TempleDark,
                        selectedTextColor = RadiantGold,
                        indicatorColor = RadiantGold,
                        unselectedIconColor = SandalwoodCream.copy(alpha = 0.6f),
                        unselectedTextColor = SandalwoodCream.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == HubTab.BHAKTI_GYM,
                    onClick = { viewModel.setHubTab(HubTab.BHAKTI_GYM) },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Bhakti Gym") },
                    label = { Text("Bhakti Gym", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TempleDark,
                        selectedTextColor = RadiantGold,
                        indicatorColor = RadiantGold,
                        unselectedIconColor = SandalwoodCream.copy(alpha = 0.6f),
                        unselectedTextColor = SandalwoodCream.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == HubTab.LORE_BLESSINGS,
                    onClick = { viewModel.setHubTab(HubTab.LORE_BLESSINGS) },
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = "Lore & Blessings") },
                    label = { Text("Lore & Boons", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TempleDark,
                        selectedTextColor = RadiantGold,
                        indicatorColor = RadiantGold,
                        unselectedIconColor = SandalwoodCream.copy(alpha = 0.6f),
                        unselectedTextColor = SandalwoodCream.copy(alpha = 0.6f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF261234), TempleDark)
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header & Resource Bar
                HubHeader(progress)

                // Reward Banner (if completed game)
                AnimatedVisibility(
                    visible = rewardBanner != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    rewardBanner?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            color = SaffronPrimary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearRewardNotification() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                                }
                            }
                        }
                    }
                }

                // Tab Content
                when (currentTab) {
                    HubTab.GAMES -> GamesTabContent(
                        progress = progress,
                        onPlayRunner = { viewModel.navigateTo(ArcadeScreen.RUNNER) },
                        onPlayPradakshina = { viewModel.navigateTo(ArcadeScreen.PRADAKSHINA) },
                        onPlayScribe = { viewModel.navigateTo(ArcadeScreen.SCRIBE) },
                        onEnterDarshan = { viewModel.navigateTo(ArcadeScreen.DARSHAN) }
                    )
                    HubTab.YATRA_MAP -> YatraMapTabContent(
                        progress = progress,
                        onSelectChapter = { viewModel.openChapter(it) }
                    )
                    HubTab.BHAKTI_GYM -> BhaktiGymTabContent(
                        progress = progress,
                        onTrainPower = { viewModel.trainMushikaPower() },
                        onEquipDhoti = { viewModel.equipCustomization("dhoti", it) },
                        onEquipTilak = { viewModel.equipCustomization("tilak", it) },
                        onEquipAccessory = { viewModel.equipCustomization("accessory", it) }
                    )
                    HubTab.LORE_BLESSINGS -> LoreBlessingsTabContent(
                        progress = progress,
                        onSelectBlessing = { viewModel.openBlessing(it) },
                        onSelectChapter = { viewModel.openChapter(it) }
                    )
                }
            }

            // Story Chapter Reader Modal
            selectedChapter?.let { chapter ->
                ChapterDialog(chapter = chapter, onClose = { viewModel.closeChapter() })
            }

            // Blessing Details Modal
            selectedBlessing?.let { blessing ->
                BlessingDialog(blessing = blessing, isUnlocked = progress.yatraMeters >= blessing.requiredDistance, onClose = { viewModel.closeBlessing() })
            }
        }
    }
}

@Composable
private fun HubHeader(progress: YatraProgress) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mushika's Mahayatra",
                    color = RadiantGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Ganesha Devotional Arcade",
                    color = SandalwoodCream.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            // Kailash Distance Pill
            Surface(
                color = TempleCard,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DivineGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏔️", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${progress.yatraMeters}m / 5000m",
                        color = DivineGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Offerings & Currencies Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ResourceBadge("🥟 Modaks", "${progress.totalModaks}", RadiantGold)
            ResourceBadge("🪷 Lotuses", "${progress.totalFlowers}", LotusPink)
            ResourceBadge("🪔 Diyas", "${progress.totalDiyas}", DivineGold)
            ResourceBadge("💪 Muscle", "Lvl ${progress.mushikaMusclePower}", SaffronSecondary)
        }
    }
}

@Composable
private fun ResourceBadge(label: String, value: String, color: Color) {
    Surface(
        color = TempleCard.copy(alpha = 0.9f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = SandalwoodCream.copy(alpha = 0.8f), fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GamesTabContent(
    progress: YatraProgress,
    onPlayRunner: () -> Unit,
    onPlayPradakshina: () -> Unit,
    onPlayScribe: () -> Unit,
    onEnterDarshan: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GameCard(
                title = "Mushika's Mahayatra Runner",
                subtitle = "Endless Temple Runner • 3 Lanes • Divine Power-ups",
                description = "Navigate through sacred forests, mountains, and temple steps. Collect Modaks and Diyas, while overcoming inner vices: Ego, Greed, Anger, Fear, and Confusion!",
                iconTag = "🏃‍♂️",
                accentColor = SaffronPrimary,
                highScoreLabel = "Best: ${progress.highestRunnerScore} pts",
                onPlay = onPlayRunner
            )
        }

        item {
            GameCard(
                title = "Pradakshina of Wisdom",
                subtitle = "Cosmic Orbital Puzzle • Shiva & Parvati • 7 Wisdom Bindus",
                description = "Re-enact Ganesha's wisdom! Hop between cosmic orbits around Lord Shiva & Goddess Parvati, aligning planetary chakras and avoiding Maya debris.",
                iconTag = "🪐",
                accentColor = CelestialTeal,
                highScoreLabel = "Best: ${progress.highestPradakshinaScore} pts",
                onPlay = onPlayPradakshina
            )
        }

        item {
            GameCard(
                title = "Vyasa's Scribe Challenge",
                subtitle = "Mahabharata Sacred Shloka Rhythm & Memory",
                description = "Transcribe the epic Mahabharata with Sage Vyasa! Keep the unbroken rhythm of sacred glyphs and invoke Ganesha's broken tusk sacrifice when ink runs low.",
                iconTag = "🪶",
                accentColor = DivineGold,
                highScoreLabel = "Best: ${progress.highestScribeScore} pts",
                onPlay = onPlayScribe
            )
        }

        item {
            val darshanUnlocked = progress.yatraMeters >= 4000
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (darshanUnlocked) Color(0xFF3B1D4A) else TempleCard.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = if (darshanUnlocked) RadiantGold else TempleCardBorder
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🕉️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Grand Kailash Darshan",
                                    color = RadiantGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = if (darshanUnlocked) "Sanctum Open • Meet Lord Ganesha" else "Unlocks at 4000m (${4000 - progress.yatraMeters}m to go)",
                                    color = SandalwoodCream.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = onEnterDarshan,
                            enabled = darshanUnlocked,
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(if (darshanUnlocked) "ENTER" else "LOCKED", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameCard(
    title: String,
    subtitle: String,
    description: String,
    iconTag: String,
    accentColor: Color,
    highScoreLabel: String,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TempleCard),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconTag, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = title, color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = subtitle, color = SandalwoodCream.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = highScoreLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                color = SandalwoodCream.copy(alpha = 0.85f),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPlay,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "PLAY MINI-GAME", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun YatraMapTabContent(
    progress: YatraProgress,
    onSelectChapter: (StoryChapter) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TempleCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("YATRA EXPEDITION PROGRESS", color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${(progress.yatraMeters * 100f / 5000f).toInt()}% to Kailash", color = DivineGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (progress.yatraMeters / 5000f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = SaffronPrimary,
                        trackColor = TempleDark
                    )
                }
            }
        }

        items(YatraRepository.CHAPTERS) { chapter ->
            val isUnlocked = progress.yatraMeters >= chapter.requiredDistanceMeters
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isUnlocked) { onSelectChapter(chapter) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) TempleCard else TempleDark.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isUnlocked) RadiantGold.copy(alpha = 0.5f) else TempleCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) SaffronPrimary else Color.Gray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${chapter.id}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = chapter.title,
                                color = if (isUnlocked) RadiantGold else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = chapter.subtitle,
                                color = if (isUnlocked) SandalwoodCream.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (isUnlocked) {
                        Surface(
                            color = SacredGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "READ",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = SacredGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${chapter.requiredDistanceMeters}m",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BhaktiGymTabContent(
    progress: YatraProgress,
    onTrainPower: () -> Unit,
    onEquipDhoti: (String) -> Unit,
    onEquipTilak: (String) -> Unit,
    onEquipAccessory: (String) -> Unit
) {
    val trainCost = progress.mushikaMusclePower * 15
    val canTrain = progress.totalModaks >= trainCost

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Character Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TempleCard),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DivineGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MUSHIKA • BODYBUILDER VAHANA",
                        color = RadiantGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Strength forged in unwavering Devotion (Bhakti)",
                        color = SandalwoodCream.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Canvas animated preview of muscular mouse
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(RadiantGold.copy(alpha = 0.3f), TempleDark)
                                )
                            )
                            .border(2.dp, RadiantGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawMushikaGymPreview(
                                center = Offset(size.width / 2f, size.height * 0.58f),
                                muscleLevel = progress.mushikaMusclePower,
                                dhoti = progress.equippedDhoti
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Muscle upgrade bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Muscle Power: Level ${progress.mushikaMusclePower}", color = DivineGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "+Jump Hangtime • +Trishula Duration", color = SandalwoodCream.copy(alpha = 0.7f), fontSize = 11.sp)
                        }

                        Button(
                            onClick = onTrainPower,
                            enabled = canTrain,
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "TRAIN ($trainCost 🥟)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Customization Options: Dhoti
        item {
            CustomizerCategory(
                title = "Sacred Dhoti Attire",
                options = listOf("Saffron Gold", "Royal Crimson", "Temple Peacock", "Ascetic White"),
                selected = progress.equippedDhoti,
                onSelect = onEquipDhoti
            )
        }

        // Customization Options: Sacred Tilak
        item {
            CustomizerCategory(
                title = "Forehead Tilak",
                options = listOf("Tripundra Chandan", "Sindoor Bindi", "Kasturi Pundra"),
                selected = progress.equippedTilak,
                onSelect = onEquipTilak
            )
        }

        // Customization Options: Devotional Adornments
        item {
            CustomizerCategory(
                title = "Sacred Adornments",
                options = listOf("Rudraksha Mala", "Golden Wrist Cuffs", "Modak Mace"),
                selected = progress.equippedAccessory,
                onSelect = onEquipAccessory
            )
        }
    }
}

@Composable
private fun CustomizerCategory(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TempleCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = RadiantGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { opt ->
                    val isSelected = opt == selected
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelect(opt) },
                        color = if (isSelected) SaffronPrimary else TempleDark,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) RadiantGold else TempleCardBorder
                        )
                    ) {
                        Text(
                            text = opt,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            color = if (isSelected) Color.White else SandalwoodCream,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoreBlessingsTabContent(
    progress: YatraProgress,
    onSelectBlessing: (Blessing) -> Unit,
    onSelectChapter: (StoryChapter) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "ASHTAVINAYAKA DIVINE BLESSINGS",
                color = RadiantGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
            Text(
                text = "Unlocking divine boons through dedicated Yatra distance",
                color = SandalwoodCream.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }

        items(YatraRepository.BLESSINGS) { blessing ->
            val isUnlocked = progress.yatraMeters >= blessing.requiredDistance
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectBlessing(blessing) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) TempleCard else TempleDark.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUnlocked) RadiantGold.copy(alpha = 0.5f) else TempleCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = if (isUnlocked) "🌟" else "🔒", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${blessing.title} (${blessing.temple})",
                                color = if (isUnlocked) RadiantGold else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = blessing.boonEffect,
                                color = if (isUnlocked) SandalwoodCream else Color.Gray.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "${blessing.requiredDistance}m",
                        color = if (isUnlocked) DivineGold else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterDialog(chapter: StoryChapter, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = TempleDark),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DivineGold)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = chapter.title,
                    color = RadiantGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = chapter.subtitle,
                    color = SandalwoodCream.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = chapter.description,
                    color = SandalwoodCream,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = SaffronPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Moral Lesson: ${chapter.moralValue}",
                        modifier = Modifier.padding(12.dp),
                        color = RadiantGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CLOSE STORY", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BlessingDialog(blessing: Blessing, isUnlocked: Boolean, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = TempleDark),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DivineGold)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = blessing.title,
                    color = RadiantGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Ashtavinayaka Temple • ${blessing.temple}",
                    color = SandalwoodCream.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = blessing.description,
                    color = SandalwoodCream,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = DivineGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Boon Effect: ${blessing.boonEffect}",
                        modifier = Modifier.padding(12.dp),
                        color = RadiantGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ACCEPT BLESSING", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun DrawScope.drawMushikaGymPreview(
    center: Offset,
    muscleLevel: Int,
    dhoti: String
) {
    val dhotiColor = when (dhoti) {
        "Royal Crimson" -> Color(0xFFB91C1C)
        "Temple Peacock" -> Color(0xFF0369A1)
        "Ascetic White" -> Color(0xFFF8FAFC)
        else -> SaffronPrimary
    }

    // Muscle mouse chest & abs
    val bicepBonus = (muscleLevel * 1.5f).coerceAtMost(8f)
    drawRoundRect(
        color = Color(0xFFFEF3C7),
        topLeft = Offset(center.x - 22f - bicepBonus / 2f, center.y - 18f),
        size = Size(44f + bicepBonus, 32f),
        cornerRadius = CornerRadius(10f, 10f)
    )

    // Dhoti
    drawRoundRect(
        color = dhotiColor,
        topLeft = Offset(center.x - 20f, center.y + 12f),
        size = Size(40f, 22f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Bicep flexing holding golden dumbbell
    drawCircle(color = Color(0xFFFEF3C7), radius = 12f + bicepBonus * 0.6f, center = Offset(center.x - 26f, center.y - 12f))
    drawCircle(color = Color(0xFFFEF3C7), radius = 12f + bicepBonus * 0.6f, center = Offset(center.x + 26f, center.y - 12f))

    // Golden Dumbbell in hand
    drawLine(
        color = RadiantGold,
        start = Offset(center.x - 38f, center.y - 16f),
        end = Offset(center.x - 14f, center.y - 16f),
        strokeWidth = 3f
    )
    drawCircle(color = RadiantGold, radius = 5f, center = Offset(center.x - 38f, center.y - 16f))
    drawCircle(color = RadiantGold, radius = 5f, center = Offset(center.x - 14f, center.y - 16f))

    // Head
    drawCircle(color = Color(0xFFFDE68A), radius = 18f, center = Offset(center.x, center.y - 30f))
    // Ears
    drawCircle(color = Color(0xFFFDE68A), radius = 11f, center = Offset(center.x - 14f, center.y - 44f))
    drawCircle(color = Color(0xFFF59E0B), radius = 7f, center = Offset(center.x - 14f, center.y - 44f))
    drawCircle(color = Color(0xFFFDE68A), radius = 11f, center = Offset(center.x + 14f, center.y - 44f))
    drawCircle(color = Color(0xFFF59E0B), radius = 7f, center = Offset(center.x + 14f, center.y - 44f))

    // Sacred Tilak
    drawLine(
        color = Color(0xFFDC2626),
        start = Offset(center.x, center.y - 38f),
        end = Offset(center.x, center.y - 32f),
        strokeWidth = 3f
    )
    drawCircle(color = RadiantGold, radius = 2f, center = Offset(center.x, center.y - 30f))

    // Eyes
    drawCircle(color = Color(0xFF1E293B), radius = 2.5f, center = Offset(center.x - 5f, center.y - 31f))
    drawCircle(color = Color(0xFF1E293B), radius = 2.5f, center = Offset(center.x + 5f, center.y - 31f))
}
