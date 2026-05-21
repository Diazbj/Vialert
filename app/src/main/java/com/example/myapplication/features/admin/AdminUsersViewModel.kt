package com.example.myapplication.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.UserRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsersUiState(
    val filteredUsers: List<User> = emptyList(),
    val searchQuery: String = "",
    val totalUsers: Int = 0,
    val adminCount: Int = 0,
    val userCount: Int = 0
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(userRepository.users, _searchQuery) { users, query ->
                val filtered = if (query.isBlank()) users
                else users.filter {
                    it.firstName.contains(query, ignoreCase = true) ||
                    it.lastName.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
                }
                AdminUsersUiState(
                    filteredUsers = filtered.sortedWith(compareBy({ it.role != UserRole.ADMIN }, { it.firstName })),
                    searchQuery = query,
                    totalUsers = users.size,
                    adminCount = users.count { it.role == UserRole.ADMIN },
                    userCount = users.count { it.role == UserRole.USER }
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onSearchChange(query: String) { _searchQuery.value = query }
}
