package com.example.myapplication.domain.model

import java.time.LocalDateTime

/**
 * Evento de notificacion originado por acciones relacionadas con un reporte.
 */
data class Notification(
    val id: String,
    val tipo: TipoNotificacion,
    val titulo: String,
    val mensaje: String,
    val reporteId: String,
    val creadoEn: LocalDateTime,
    val creadoPorId: String
)
