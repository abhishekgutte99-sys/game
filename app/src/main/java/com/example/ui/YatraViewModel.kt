package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SacredAudioEngine
import com.example.audio.VibrationHelper
import com.example.data.Blessing
import com.example.data.StoryChapter
import com.example.data.YatraDatabase
import com.example.data.YatraProgress
import com.example.data.YatraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ArcadeScreen {
    HUB,
    RUNNER,
    PRADAKSHINA,
    SCRIBE,
    DARSHAN
}

enum class HubTab {
    GAMES,
    YATRA_MAP,
    BHAKTI_GYM,
    LORE_BLESSINGS
}

class YatraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: YatraRepository
    val progress: StateFlow<YatraProgress>

    private val _currentScreen = MutableStateFlow(ArcadeScreen.HUB)
    val currentScreen: StateFlow<ArcadeScreen> = _currentScreen.asStateFlow()

    private val _currentHubTab = MutableStateFlow(HubTab.GAMES)
    val currentHubTab: StateFlow<HubTab> = _currentHubTab.asStateFlow()

    private val _selectedChapter = MutableStateFlow<StoryChapter?>(null)
    val selectedChapter: StateFlow<StoryChapter?> = _selectedChapter.asStateFlow()

    private val _selectedBlessing = MutableStateFlow<Blessing?>(null)
    val selectedBlessing: StateFlow<Blessing?> = _selectedBlessing.asStateFlow()

    // Last game results for celebration banner
    private val _lastGameReward = MutableStateFlow<String?>(null)
    val lastGameReward: StateFlow<String?> = _lastGameReward.asStateFlow()

    init {
        val database = YatraDatabase.getDatabase(application)
        repository = YatraRepository(database.yatraDao())
        progress = repository.progress.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = YatraProgress()
        )
    }

    fun navigateTo(screen: ArcadeScreen) {
        _currentScreen.value = screen
        if (screen == ArcadeScreen.DARSHAN) {
            SacredAudioEngine.playOmResonance()
        }
    }

    fun setHubTab(tab: HubTab) {
        _currentHubTab.value = tab
        SacredAudioEngine.playTempleBell()
    }

    fun openChapter(chapter: StoryChapter) {
        _selectedChapter.value = chapter
        SacredAudioEngine.playOmResonance()
    }

    fun closeChapter() {
        _selectedChapter.value = null
    }

    fun openBlessing(blessing: Blessing) {
        _selectedBlessing.value = blessing
        SacredAudioEngine.playTempleBell()
    }

    fun closeBlessing() {
        _selectedBlessing.value = null
    }

    fun clearRewardNotification() {
        _lastGameReward.value = null
    }

    fun completeRunnerSession(
        runDistanceMeters: Int,
        modaksCollected: Int,
        flowersCollected: Int,
        diyasCollected: Int,
        score: Int
    ) {
        viewModelScope.launch {
            val current = progress.value
            val newTotalDistance = current.yatraMeters + runDistanceMeters
            val newModaks = current.totalModaks + modaksCollected
            val newFlowers = current.totalFlowers + flowersCollected
            val newDiyas = current.totalDiyas + diyasCollected
            val newHighScore = maxOf(current.highestRunnerScore, score)

            // Check unlocked chapters
            var unlockedCh = current.unlockedChapters
            YatraRepository.CHAPTERS.forEach { ch ->
                if (newTotalDistance >= ch.requiredDistanceMeters && ch.id > unlockedCh) {
                    unlockedCh = ch.id
                }
            }

            // Check unlocked blessings
            var unlockedBl = current.unlockedBlessings
            YatraRepository.BLESSINGS.forEach { bl ->
                if (newTotalDistance >= bl.requiredDistance && bl.id > unlockedBl) {
                    unlockedBl = bl.id
                }
            }

            val updated = current.copy(
                yatraMeters = newTotalDistance,
                totalModaks = newModaks,
                totalFlowers = newFlowers,
                totalDiyas = newDiyas,
                highestRunnerScore = newHighScore,
                unlockedChapters = unlockedCh,
                unlockedBlessings = unlockedBl
            )
            repository.save(updated)
            _lastGameReward.value = "+$runDistanceMeters m Yatra Progress | +$modaksCollected Modaks | +$flowersCollected Lotuses"
            VibrationHelper.vibrateSuccess(getApplication())
            _currentScreen.value = ArcadeScreen.HUB
        }
    }

    fun completePradakshinaSession(score: Int, modaksEarned: Int, bonusDistance: Int) {
        viewModelScope.launch {
            val current = progress.value
            val newTotalDistance = current.yatraMeters + bonusDistance
            val newHighScore = maxOf(current.highestPradakshinaScore, score)

            var unlockedCh = current.unlockedChapters
            YatraRepository.CHAPTERS.forEach { ch ->
                if (newTotalDistance >= ch.requiredDistanceMeters && ch.id > unlockedCh) {
                    unlockedCh = ch.id
                }
            }

            var unlockedBl = current.unlockedBlessings
            YatraRepository.BLESSINGS.forEach { bl ->
                if (newTotalDistance >= bl.requiredDistance && bl.id > unlockedBl) {
                    unlockedBl = bl.id
                }
            }

            val updated = current.copy(
                yatraMeters = newTotalDistance,
                totalModaks = current.totalModaks + modaksEarned,
                highestPradakshinaScore = newHighScore,
                unlockedChapters = unlockedCh,
                unlockedBlessings = unlockedBl
            )
            repository.save(updated)
            _lastGameReward.value = "Pradakshina Complete! +$bonusDistance m Cosmic Yatra | +$modaksEarned Modaks"
            VibrationHelper.vibrateSuccess(getApplication())
            _currentScreen.value = ArcadeScreen.HUB
        }
    }

    fun completeScribeSession(score: Int, modaksEarned: Int, bonusDistance: Int) {
        viewModelScope.launch {
            val current = progress.value
            val newTotalDistance = current.yatraMeters + bonusDistance
            val newHighScore = maxOf(current.highestScribeScore, score)

            var unlockedCh = current.unlockedChapters
            YatraRepository.CHAPTERS.forEach { ch ->
                if (newTotalDistance >= ch.requiredDistanceMeters && ch.id > unlockedCh) {
                    unlockedCh = ch.id
                }
            }

            var unlockedBl = current.unlockedBlessings
            YatraRepository.BLESSINGS.forEach { bl ->
                if (newTotalDistance >= bl.requiredDistance && bl.id > unlockedBl) {
                    unlockedBl = bl.id
                }
            }

            val updated = current.copy(
                yatraMeters = newTotalDistance,
                totalModaks = current.totalModaks + modaksEarned,
                highestScribeScore = newHighScore,
                unlockedChapters = unlockedCh,
                unlockedBlessings = unlockedBl
            )
            repository.save(updated)
            _lastGameReward.value = "Mahabharata Verses Inscribed! +$bonusDistance m Yatra | +$modaksEarned Modaks"
            VibrationHelper.vibrateSuccess(getApplication())
            _currentScreen.value = ArcadeScreen.HUB
        }
    }

    fun trainMushikaPower() {
        val current = progress.value
        val cost = current.mushikaMusclePower * 15
        if (current.totalModaks >= cost) {
            viewModelScope.launch {
                val updated = current.copy(
                    totalModaks = current.totalModaks - cost,
                    mushikaMusclePower = current.mushikaMusclePower + 1
                )
                repository.save(updated)
                SacredAudioEngine.playPowerUp()
                VibrationHelper.vibrate(getApplication(), 80)
            }
        }
    }

    fun equipCustomization(type: String, value: String) {
        val current = progress.value
        viewModelScope.launch {
            val updated = when (type) {
                "dhoti" -> current.copy(equippedDhoti = value)
                "tilak" -> current.copy(equippedTilak = value)
                "accessory" -> current.copy(equippedAccessory = value)
                else -> current
            }
            repository.save(updated)
            SacredAudioEngine.playModakChime()
        }
    }

    fun performAartiOffering(modakCost: Int = 1) {
        val current = progress.value
        if (current.totalModaks >= modakCost) {
            viewModelScope.launch {
                val updated = current.copy(
                    totalModaks = current.totalModaks - modakCost,
                    totalFlowers = current.totalFlowers + 2,
                    totalDiyas = current.totalDiyas + 1
                )
                repository.save(updated)
                SacredAudioEngine.playTempleBell()
                VibrationHelper.vibrateSuccess(getApplication())
            }
        }
    }
}
