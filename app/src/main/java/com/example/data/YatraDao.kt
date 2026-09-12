package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface YatraDao {
    @Query("SELECT * FROM yatra_progress WHERE id = 1 LIMIT 1")
    fun getYatraProgress(): Flow<YatraProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveYatraProgress(progress: YatraProgress)
}
