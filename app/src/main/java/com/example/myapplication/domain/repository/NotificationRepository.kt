package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Notification
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    fun getForUser(userId: String): StateFlow<List<Notification>>
    suspend fun markAsRead(notifId: String)
    suspend fun markAllAsRead(userId: String)
    suspend fun create(notification: Notification)
}
