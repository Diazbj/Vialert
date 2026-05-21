package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Comment
import kotlinx.coroutines.flow.StateFlow

interface CommentRepository {
    fun getByReportId(reportId: String): StateFlow<List<Comment>>
    suspend fun create(comment: Comment)
    fun getAll(): List<Comment>
    fun getById(id: String): Comment?
    fun update(comment: Comment)
    fun delete(id: String)
}
