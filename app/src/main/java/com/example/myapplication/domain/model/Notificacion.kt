package com.example.myapplication.domain.model

import java.time.LocalDateTime

/**
 * Evento de notificacion originado por acciones relacionadas con un reporte.
 */
data class Notificacion(
    val id: Long,
    val tipo: TipoNotificacion,
    val titulo: String,
    val mensaje: String,
    val reporteId: Long,
    val creadoEn: LocalDateTime,
    val creadoPor: Usuario
)
