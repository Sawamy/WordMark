package com.example.wordmark.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyRecordDao {
    @Insert
    suspend fun insert(record: StudyRecord)

    @Delete
    suspend fun delete(record: StudyRecord)

    @Query("SELECT * FROM study_records ORDER BY timestamp DESC, id DESC")
    fun observeAll(): Flow<List<StudyRecord>>
}


