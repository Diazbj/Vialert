package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.NotificacionUsuario

interface NotificacionUsuarioRepository {
    fun getAll(): List<NotificacionUsuario>
    fun getById(id: String): NotificacionUsuario?
    fun create(notificacionUsuario: NotificacionUsuario)
    fun update(notificacionUsuario: NotificacionUsuario)
    fun delete(id: String)
    fun getByUserId(userId: String): List<NotificacionUsuario>
}
