package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Notification

interface NotificationRepository {
    fun getAll(): List<Notification>
    fun getById(id: String): Notification?
    fun create(notification: Notification)
    fun update(notification: Notification)
    fun delete(id: String)
    fun getByReportId(reportId: String): List<Notification>
}
