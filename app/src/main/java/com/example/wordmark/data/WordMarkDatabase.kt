package com.example.wordmark.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudyRecord::class], version = 1)
abstract class WordMarkDatabase : RoomDatabase() {
    abstract fun studyRecordDao(): StudyRecordDao

    companion object {
        @Volatile
        private var instance: WordMarkDatabase? = null

        fun getInstance(context: Context): WordMarkDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WordMarkDatabase::class.java,
                    "wordmark.db"
                ).build().also { instance = it }
            }
        }
    }
}
