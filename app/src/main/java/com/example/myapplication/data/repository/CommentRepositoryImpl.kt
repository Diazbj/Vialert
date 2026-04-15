package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Comment
import com.example.myapplication.domain.repository.CommentRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor() : CommentRepository {
    private val items = mutableListOf<Comment>()

    init {
        items.addAll(listOf(
            Comment(
                id = "c1",
                reportId = "101",
                userId = "2",
                content = "Estamos revisando este bache, gracias por reportar.",
                createdAt = LocalDateTime.now().minusDays(1)
            ),
            Comment(
                id = "c2",
                reportId = "101",
                userId = "1",
                content = "Gracias, es muy peligroso de noche.",
                createdAt = LocalDateTime.now().minusMinutes(30)
            ),
            Comment(
                id = "c3",
                reportId = "102",
                userId = "2",
                content = "Técnicos en camino para la reparación.",
                createdAt = LocalDateTime.now().minusHours(2)
            )
        ))
    }

    override fun getAll(): List<Comment> = items.toList()

    override fun getById(id: String): Comment? = items.find { it.id == id }

    override fun create(comment: Comment) {
        items.add(comment)
    }

    override fun update(comment: Comment) {
        val index = items.indexOfFirst { it.id == comment.id }
        if (index != -1) {
            items[index] = comment
        }
    }

    override fun delete(id: String) {
        items.removeAll { it.id == id }
    }

    override fun getByReportId(reportId: String): List<Comment> {
        return items.filter { it.reportId == reportId }
    }
}
