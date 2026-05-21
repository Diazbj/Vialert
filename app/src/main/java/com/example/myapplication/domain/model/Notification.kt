package com.example.myapplication.domain.model

data class Notification(
    val id: String = "",
    val tipo: TipoNotificacion = TipoNotificacion.REPORTE_CREADO,
    val titulo: String = "",
    val mensaje: String = "",
    val reporteId: String = "",
    val destinatarioId: String = "",
    val creadoEn: Long = System.currentTimeMillis(),
    val leido: Boolean = false,
    val creadoPorId: String = ""
)
