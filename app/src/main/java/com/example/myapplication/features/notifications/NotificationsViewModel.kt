package com.example.myapplication.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationFilter { TODAS, NO_LEIDAS }

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val currentUserId = userRepository.getCurrentUserId() ?: ""

    private val _filter = MutableStateFlow(NotificationFilter.TODAS)
    val filter: StateFlow<NotificationFilter> = _filter

    private val allNotifications: StateFlow<List<Notification>> =
        notificationRepository.getForUser(currentUserId)

    val visibleNotifications: StateFlow<List<Notification>> =
        combine(allNotifications, _filter) { list, f ->
            when (f) {
                NotificationFilter.TODAS -> list
                NotificationFilter.NO_LEIDAS -> list.filter { !it.leido }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val hasUnread: StateFlow<Boolean> = allNotifications
        .map { list -> list.any { !it.leido } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setFilter(filter: NotificationFilter) {
        _filter.value = filter
    }

    fun markAsRead(notifId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notifId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead(currentUserId)
        }
    }
}
