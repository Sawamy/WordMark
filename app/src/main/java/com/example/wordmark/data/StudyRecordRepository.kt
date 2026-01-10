package com.example.wordmark.data

import kotlinx.coroutines.flow.Flow

class StudyRecordRepository(private val dao: StudyRecordDao) {
    val records: Flow<List<StudyRecord>> = dao.observeAll()

    suspend fun add(record: StudyRecord) {
        dao.insert(record)
    }
}
