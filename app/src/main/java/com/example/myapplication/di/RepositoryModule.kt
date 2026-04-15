package com.example.myapplication.di

import com.example.myapplication.data.repository.CommentRepositoryImpl
import com.example.myapplication.data.repository.NotificationRepositoryImpl
import com.example.myapplication.data.repository.ReportRepositoryImpl
import com.example.myapplication.data.repository.UserRepositoryImpl
import com.example.myapplication.data.repository.NotificacionUsuarioRepositoryImpl
import com.example.myapplication.domain.repository.CommentRepository
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.ReportRepository
import com.example.myapplication.domain.repository.UserRepository
import com.example.myapplication.domain.repository.NotificacionUsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindNotificacionUsuarioRepository(
        notificacionUsuarioRepositoryImpl: NotificacionUsuarioRepositoryImpl
    ): NotificacionUsuarioRepository
}
