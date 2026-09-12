package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.ArcadeScreen
import com.example.ui.YatraViewModel
import com.example.ui.games.PradakshinaScreen
import com.example.ui.games.TempleRunnerScreen
import com.example.ui.games.VyasaScribeScreen
import com.example.ui.screens.ArcadeHubScreen
import com.example.ui.screens.DarshanSanctumScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: YatraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    val progress by viewModel.progress.collectAsState()

                    when (currentScreen) {
                        ArcadeScreen.HUB -> {
                            ArcadeHubScreen(viewModel = viewModel)
                        }
                        ArcadeScreen.RUNNER -> {
                            TempleRunnerScreen(
                                mushikaMuscleLevel = progress.mushikaMusclePower,
                                equippedDhoti = progress.equippedDhoti,
                                onFinishRun = { dist, modaks, flowers, diyas, score ->
                                    viewModel.completeRunnerSession(dist, modaks, flowers, diyas, score)
                                },
                                onBack = { viewModel.navigateTo(ArcadeScreen.HUB) }
                            )
                        }
                        ArcadeScreen.PRADAKSHINA -> {
                            PradakshinaScreen(
                                onFinishGame = { score, modaks, bonusDist ->
                                    viewModel.completePradakshinaSession(score, modaks, bonusDist)
                                },
                                onBack = { viewModel.navigateTo(ArcadeScreen.HUB) }
                            )
                        }
                        ArcadeScreen.SCRIBE -> {
                            VyasaScribeScreen(
                                onFinishGame = { score, modaks, bonusDist ->
                                    viewModel.completeScribeSession(score, modaks, bonusDist)
                                },
                                onBack = { viewModel.navigateTo(ArcadeScreen.HUB) }
                            )
                        }
                        ArcadeScreen.DARSHAN -> {
                            DarshanSanctumScreen(
                                totalModaks = progress.totalModaks,
                                onOfferModak = { viewModel.performAartiOffering(1) },
                                onBack = { viewModel.navigateTo(ArcadeScreen.HUB) }
                            )
                        }
                    }
                }
            }
        }
    }
}

