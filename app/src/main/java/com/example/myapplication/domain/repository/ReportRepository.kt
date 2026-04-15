package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Report

interface ReportRepository {
    fun getAll(): List<Report>
    fun getById(id: String): Report?
    fun create(report: Report)
    fun update(report: Report)
    fun delete(id: String)
    fun getByUserId(userId: String): List<Report>
}
