package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Comment
import com.example.myapplication.domain.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Stub kept for Hilt graph consistency — replaced by CommentFirebaseRepositoryImpl
@Singleton
class CommentRepositoryImpl @Inject constructor() : CommentRepository {
    private val empty = MutableStateFlow<List<Comment>>(emptyList())
    override fun getByReportId(reportId: String): StateFlow<List<Comment>> = empty.asStateFlow()
    override suspend fun create(comment: Comment) = Unit
    override fun getAll(): List<Comment> = emptyList()
    override fun getById(id: String): Comment? = null
    override fun update(comment: Comment) = Unit
    override fun delete(id: String) = Unit
}
