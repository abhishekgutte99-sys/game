package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [YatraProgress::class], version = 1, exportSchema = false)
abstract class YatraDatabase : RoomDatabase() {
    abstract fun yatraDao(): YatraDao

    companion object {
        @Volatile
        private var INSTANCE: YatraDatabase? = null

        fun getDatabase(context: Context): YatraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YatraDatabase::class.java,
                    "mushika_yatra.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
