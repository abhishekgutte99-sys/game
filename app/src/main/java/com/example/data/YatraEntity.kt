package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yatra_progress")
data class YatraProgress(
    @PrimaryKey val id: Int = 1,
    val yatraMeters: Int = 0,
    val totalModaks: Int = 10,
    val totalFlowers: Int = 5,
    val totalDiyas: Int = 3,
    val unlockedChapters: Int = 1,
    val unlockedBlessings: Int = 1,
    val highestRunnerScore: Int = 0,
    val highestPradakshinaScore: Int = 0,
    val highestScribeScore: Int = 0,
    val mushikaMusclePower: Int = 1,
    val equippedDhoti: String = "Saffron Gold",
    val equippedTilak: String = "Tripundra Chandan",
    val equippedAccessory: String = "Rudraksha Mala"
)

data class StoryChapter(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val requiredDistanceMeters: Int,
    val moralValue: String
)

data class Blessing(
    val id: Int,
    val title: String,
    val temple: String,
    val boonEffect: String,
    val description: String,
    val requiredDistance: Int
)
