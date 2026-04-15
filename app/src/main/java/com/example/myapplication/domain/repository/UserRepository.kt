package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    fun getAll(): List<User>
    fun getById(id: String): User?
    fun create(user: User)
    fun update(user: User)
    fun delete(id: String)
    fun findByEmail(email: String): User?
}
