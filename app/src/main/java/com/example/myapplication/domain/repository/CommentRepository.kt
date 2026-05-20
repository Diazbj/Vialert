package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Comment

interface CommentRepository {
    fun getAll(): List<Comment>
    fun getById(id: String): Comment?
    suspend fun create(comment: Comment)
    fun update(comment: Comment)
    fun delete(id: String)
    fun getByReportId(reportId: String): List<Comment>
}
