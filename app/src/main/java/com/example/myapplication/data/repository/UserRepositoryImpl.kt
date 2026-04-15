package com.example.myapplication.data.repository

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

    override fun getAll(): List<User> {
        return _users.value
    }

    override fun getById(id: String): User? {
        return _users.value.find { it.id == id }
    }

    override fun create(user: User) {
        val currentList = _users.value.toMutableList()
        currentList.add(user)
        _users.value = currentList
    }

    override fun update(user: User) {
        val currentList = _users.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == user.id }
        if (index != -1) {
            currentList[index] = user
            _users.value = currentList
        }
    }

    override fun delete(id: String) {
        val currentList = _users.value.toMutableList()
        currentList.removeAll { it.id == id }
        _users.value = currentList
    }

    override fun findByEmail(email: String): User? {
        return _users.value.find { it.email == email }
    }

    private fun fetchUsers(): List<User> {
        return listOf(
            User(
                id = "1",
                name = "Juan Perez",
                city = "Asunción",
                address = "Calle Palma 123",
                email = "juan@vialert.com",
                password = "password123",
                phoneNumber = "0981123456",
                role = UserRole.USER
            ),
            User(
                id = "2",
                name = "Admin Vialert",
                city = "Asunción",
                address = "Oficina Central",
                email = "admin@vialert.com",
                password = "adminpassword",
                phoneNumber = "0981999999",
                role = UserRole.ADMIN
            ),
            User(
                id = "3",
                name = "Maria Lopez",
                city = "Fernando de la Mora",
                address = "Avda. Mcal. Lopez 456",
                email = "maria@example.com",
                password = "mypassword",
                phoneNumber = "0971456789",
                role = UserRole.USER
            )
        )
    }
}
