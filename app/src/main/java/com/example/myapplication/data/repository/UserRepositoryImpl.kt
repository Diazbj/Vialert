package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.model.Gender
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
        if (findByEmail(user.email) != null) {
            return
        }
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

    override fun findByEmailAndPassword(
        email: String,
        password: String
    ): User? {
        return _users.value.find { it.email == email && it.password == password }
    }

    private fun fetchUsers(): List<User> {
        return listOf(
            User(
                id = "1",
                firstName = "Juan",
                lastName = "Perez",
                email = "juan@vialert.com",
                userName = "juanp",
                password = "password123",
                role = UserRole.USER
            ),
            User(
                id = "2",
                firstName = "admin",
                lastName = "vialert",
                email = "admin@vialert.com",
                userName = "admin",
                password = "123456789",
                role = UserRole.ADMIN
            ),
            User(
                id = "3",
                firstName = "Maria",
                lastName = "Lopez",
                email = "maria@example.com",
                userName = "marial",
                password = "mypassword",
                role = UserRole.USER
            ),
            User(
                id = "4",
                firstName = "jordy",
                lastName = "diaz",
                email = "diaz.jordyb@gmail.com",
                userName = "diazjordy",
                password = "123456789",
                gender = Gender.MASCULINO,
                birthDate = "21/03/1993",
                role = UserRole.USER
            )
        )
    }

}
