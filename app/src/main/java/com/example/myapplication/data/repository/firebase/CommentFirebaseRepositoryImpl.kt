package com.example.myapplication.data.repository.firebase

import com.example.myapplication.domain.model.Comment
import com.example.myapplication.domain.repository.CommentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentFirebaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    private val collection = firestore.collection("comments")
    private val reportFlows = mutableMapOf<String, MutableStateFlow<List<Comment>>>()

    override fun getByReportId(reportId: String): StateFlow<List<Comment>> {
        return reportFlows.getOrPut(reportId) {
            MutableStateFlow<List<Comment>>(emptyList()).also { flow ->
                collection
                    .whereEqualTo("reportId", reportId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null) return@addSnapshotListener
                        flow.value = snapshot.documents.mapNotNull { doc ->
                            runCatching {
                                val ts = doc.getLong("createdAt") ?: System.currentTimeMillis()
                                Comment(
                                    id = doc.id,
                                    reportId = doc.getString("reportId") ?: "",
                                    userId = doc.getString("userId") ?: "",
                                    content = doc.getString("content") ?: "",
                                    createdAt = LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(ts), ZoneId.systemDefault()
                                    )
                                )
                            }.getOrNull()
                        }.sortedByDescending { it.createdAt }
                    }
            }
        }
    }

    override suspend fun create(comment: Comment) {
        val data = mapOf(
            "reportId" to comment.reportId,
            "userId" to comment.userId,
            "content" to comment.content,
            "createdAt" to System.currentTimeMillis()
        )
        collection.add(data).await()
    }

    override fun getAll(): List<Comment> = reportFlows.values.flatMap { it.value }
    override fun getById(id: String): Comment? = getAll().find { it.id == id }
    override fun update(comment: Comment) { collection.document(comment.id).update("content", comment.content) }
    override fun delete(id: String) { collection.document(id).delete() }
}
