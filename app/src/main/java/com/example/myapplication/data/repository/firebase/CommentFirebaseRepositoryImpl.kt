package com.example.myapplication.data.repository.firebase

import com.example.myapplication.domain.model.Comment
import com.example.myapplication.domain.repository.CommentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("comments")

    // Cache local para lectura sincrónica
    private var cachedComments: List<Comment> = emptyList()

    init {
        callbackFlow {
            val listener = collection
                .orderBy("createdAt")
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        val list = it.documents.mapNotNull { doc ->
                            try {
                                val timestamp = doc.getLong("createdAt") ?: System.currentTimeMillis()
                                val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                                Comment(
                                    id = doc.id,
                                    reportId = doc.getString("reportId") ?: "",
                                    userId = doc.getString("userId") ?: "",
                                    content = doc.getString("content") ?: "",
                                    createdAt = dateTime
                                )
                            } catch (e: Exception) { null }
                        }
                        trySend(list)
                    }
                }
            awaitClose { listener.remove() }
        }.onEach { cachedComments = it }.launchIn(scope)
    }

    override fun getAll(): List<Comment> = cachedComments

    override fun getById(id: String): Comment? = cachedComments.find { it.id == id }

    override suspend fun create(comment: Comment) {
        val data = mapOf(
            "reportId" to comment.reportId,
            "userId" to comment.userId,
            "content" to comment.content,
            "createdAt" to System.currentTimeMillis()
        )
        collection.add(data).await()
    }

    override fun update(comment: Comment) {
        collection.document(comment.id).update("content", comment.content)
    }

    override fun delete(id: String) {
        collection.document(id).delete()
    }

    override fun getByReportId(reportId: String): List<Comment> = cachedComments.filter { it.reportId == reportId }
}
