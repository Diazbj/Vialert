package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Report
import kotlinx.coroutines.flow.StateFlow

interface ReportRepository {
    val reports: StateFlow<List<Report>>
    fun getAll(): List<Report>
    fun getById(id: String): Report?
    suspend fun create(report: Report)
    suspend fun update(report: Report)
    suspend fun delete(id: String)
    fun getByUserId(userId: String): List<Report>
    suspend fun incrementarImportancia(id: String, userId: String)
}
