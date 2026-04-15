package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.domain.repository.NotificationRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    private val items = mutableListOf<Notification>()

    init {
        items.addAll(listOf(
            Notification(
                id = "n1",
                tipo = TipoNotificacion.REPORTE_CREADO,
                titulo = "Nuevo reporte",
                mensaje = "Se ha creado un nuevo reporte en tu zona.",
                reporteId = "101",
                creadoEn = LocalDateTime.now().minusDays(1),
                creadoPorId = "1"
            ),
            Notification(
                id = "n2",
                tipo = TipoNotificacion.REPORTE_COMENTARIO,
                titulo = "Nuevo comentario",
                mensaje = "Alguien comentó en tu reporte.",
                reporteId = "101",
                creadoEn = LocalDateTime.now().minusMinutes(10),
                creadoPorId = "2"
            )
        ))
    }

    override fun getAll(): List<Notification> = items.toList()

    override fun getById(id: String): Notification? = items.find { it.id == id }

    override fun create(notification: Notification) {
        items.add(notification)
    }

    override fun update(notification: Notification) {
        val index = items.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            items[index] = notification
        }
    }

    override fun delete(id: String) {
        items.removeAll { it.id == id }
    }

    override fun getByReportId(reportId: String): List<Notification> {
        return items.filter { it.reporteId == reportId }
    }
}
