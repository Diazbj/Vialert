package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Stub kept for Hilt graph consistency — replaced by NotificationFirebaseRepositoryImpl
@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    private val empty = MutableStateFlow<List<Notification>>(emptyList())
    override fun getForUser(userId: String): StateFlow<List<Notification>> = empty.asStateFlow()
    override suspend fun markAsRead(notifId: String) = Unit
    override suspend fun markAllAsRead(userId: String) = Unit
    override suspend fun create(notification: Notification) = Unit
}
