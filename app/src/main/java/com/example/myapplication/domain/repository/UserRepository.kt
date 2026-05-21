package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    fun getAll(): List<User>
    fun getById(id: String): User?
    suspend fun create(user: User)
    suspend fun update(user: User)
    fun delete(id: String)
    fun findByEmail(email: String): User?
    suspend fun findByEmailAndPassword(email: String, password: String): User?
    suspend fun sendPasswordReset(email: String)
    fun signOut()
    fun getCurrentUserId(): String?
    suspend fun deleteAccount(userId: String)
}
