package com.example.myapplication.domain.model

import java.time.LocalDateTime

/**
 * Relacion de entrega y lectura entre una notificacion y un usuario destinatario.
 */
data class NotificacionUsuario(
    val id: Long,
    val notificacion: Notificacion,
    val usuario: Usuario,
    val leido: Boolean,
    val fechaLeido: LocalDateTime?,
    val enviadoPush: Boolean,
    val fechaEnvio: LocalDateTime?
)
