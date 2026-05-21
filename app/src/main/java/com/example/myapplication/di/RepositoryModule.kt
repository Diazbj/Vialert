package com.example.myapplication.di

import com.example.myapplication.data.repository.CommentRepositoryImpl
import com.example.myapplication.data.repository.NotificacionUsuarioRepositoryImpl
import com.example.myapplication.data.repository.firebase.CommentFirebaseRepositoryImpl
import com.example.myapplication.data.repository.firebase.NotificationFirebaseRepositoryImpl
import com.example.myapplication.data.repository.firebase.ReportFirebaseRepositoryImpl
import com.example.myapplication.data.repository.firebase.UserFirebaseRepositoryImpl
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
        userFirebaseRepositoryImpl: UserFirebaseRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportFirebaseRepositoryImpl: ReportFirebaseRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        commentFirebaseRepositoryImpl: CommentFirebaseRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationFirebaseRepositoryImpl: NotificationFirebaseRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindNotificacionUsuarioRepository(
        notificacionUsuarioRepositoryImpl: NotificacionUsuarioRepositoryImpl
    ): NotificacionUsuarioRepository
}
