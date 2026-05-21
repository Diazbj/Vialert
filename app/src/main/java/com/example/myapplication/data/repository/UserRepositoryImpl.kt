package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Gender
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {
    private val _users = MutableStateFlow<List<User>>(fetchUsers())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    override fun getAll(): List<User> = _users.value

    override fun getById(id: String): User? = _users.value.find { it.id == id }

    override suspend fun create(user: User) {
        if (findByEmail(user.email) != null) return
        _users.value = _users.value + user
    }

    override suspend fun update(user: User) {
        _users.value = _users.value.map { if (it.id == user.id) user else it }
    }

    override fun delete(id: String) {
        _users.value = _users.value.filter { it.id != id }
    }

    override fun findByEmail(email: String): User? = _users.value.find { it.email == email }

    override suspend fun findByEmailAndPassword(email: String, password: String): User? =
        _users.value.find { it.email == email && it.password == password }

    override suspend fun sendPasswordReset(email: String) {
        // In-memory: no-op
    }

    override fun signOut() {
        // In-memory: no-op
    }

    override fun getCurrentUserId(): String? = null

    override suspend fun deleteAccount(userId: String) {
        delete(userId)
    }

    private fun fetchUsers(): List<User> = listOf(
        User(id = "1", firstName = "Juan", lastName = "Perez", email = "juan@vialert.com", userName = "juanp", password = "password123", role = UserRole.USER),
        User(id = "2", firstName = "admin", lastName = "vialert", email = "admin@vialert.com", userName = "admin", password = "123456789", role = UserRole.ADMIN),
        User(id = "3", firstName = "Maria", lastName = "Lopez", email = "maria@example.com", userName = "marial", password = "mypassword", role = UserRole.USER),
        User(id = "4", firstName = "jordy", lastName = "diaz", email = "diaz.jordyb@gmail.com", userName = "diazjordy", password = "123456789", gender = Gender.MASCULINO, birthDate = "21/03/1993", role = UserRole.USER)
    )
}
