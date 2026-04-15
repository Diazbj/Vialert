package com.example.myapplication.domain.model

import java.time.LocalDateTime

/**
 * Comentario realizado por un usuario en un reporte.
 */
data class Comment(
    val id: String,
    val reportId: String,
    val userId: String,
    val content: String,
    val createdAt: LocalDateTime
)
