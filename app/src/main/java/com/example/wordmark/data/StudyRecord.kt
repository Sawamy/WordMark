package com.example.wordmark.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_records")
data class StudyRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val unitName: String,
    val startNumber: Int,
    val endNumber: Int,
    val wordCount: Int
)
