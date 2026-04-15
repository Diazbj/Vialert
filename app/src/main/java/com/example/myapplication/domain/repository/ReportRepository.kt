package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Report
import kotlinx.coroutines.flow.StateFlow

interface ReportRepository {
    val reports: StateFlow<List<Report>>
    fun getAll(): List<Report>
    fun getById(id: String): Report?
    fun create(report: Report)
    fun update(report: Report)
    fun delete(id: String)
    fun getByUserId(userId: String): List<Report>
    fun incrementarImportancia(id: String)
}
