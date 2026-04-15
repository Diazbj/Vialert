package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.NotificacionUsuario
import com.example.myapplication.domain.repository.NotificacionUsuarioRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionUsuarioRepositoryImpl @Inject constructor() : NotificacionUsuarioRepository {
    private val items = mutableListOf<NotificacionUsuario>()

    init {
        // Datos de prueba enlazando el usuario "1" con las notificaciones "n1" y "n2"
        items.addAll(listOf(
            NotificacionUsuario(
                id = "rel1",
                usuarioId = "1",
                notificacionId = "n1",
                leido = false,
                fechaLeido = null,
                enviadoPush = true,
                fechaEnvio = LocalDateTime.now().minusDays(1)
            ),
            NotificacionUsuario(
                id = "rel2",
                usuarioId = "1",
                notificacionId = "n2",
                leido = true,
                fechaLeido = LocalDateTime.now().minusMinutes(5),
                enviadoPush = true,
                fechaEnvio = LocalDateTime.now().minusMinutes(10)
            )
        ))
    }

    override fun getAll(): List<NotificacionUsuario> = items.toList()

    override fun getById(id: String): NotificacionUsuario? = items.find { it.id == id }

    override fun create(notificacionUsuario: NotificacionUsuario) {
        items.add(notificacionUsuario)
    }

    override fun update(notificacionUsuario: NotificacionUsuario) {
        val index = items.indexOfFirst { it.id == notificacionUsuario.id }
        if (index != -1) {
            items[index] = notificacionUsuario
        }
    }

    override fun delete(id: String) {
        items.removeAll { it.id == id }
    }

    override fun getByUserId(userId: String): List<NotificacionUsuario> {
        return items.filter { it.usuarioId == userId }
    }
}
