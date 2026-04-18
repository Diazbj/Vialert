package com.example.myapplication.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.NotificacionUsuario
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.NotificacionUsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationFilter {
    TODAS,
    NO_LEIDAS
}

enum class NotificationsUiContentState {
    LOADING,
    SUCCESS,
    EMPTY
}

/**
 * Modelo de UI que combina la relación del usuario con los detalles de la notificación.
 */
data class NotificationDisplayItem(
    val userNotification: NotificacionUsuario,
    val notification: Notification
) {
    val id: String get() = userNotification.id
    val leido: Boolean get() = userNotification.leido
    val notificacion: Notification get() = notification
}

data class NotificationsUiState(
    val allNotifications: List<NotificationDisplayItem> = emptyList(),
    val visibleNotifications: List<NotificationDisplayItem> = emptyList(),
    val selectedFilter: NotificationFilter = NotificationFilter.TODAS,
    val contentState: NotificationsUiContentState = NotificationsUiContentState.LOADING
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userNotificationRepository: NotificacionUsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        getNotifications()
    }

    fun getNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(contentState = NotificationsUiContentState.LOADING) }
            
            // En una app real, aquí obtendríamos el ID del usuario actual. Usaremos "1" por ahora.
            val currentUserId = "1"
            val userRelations = userNotificationRepository.getByUserId(currentUserId)
            
            val displayItems = userRelations.mapNotNull { relation ->
                notificationRepository.getById(relation.notificacionId)?.let { detail ->
                    NotificationDisplayItem(relation, detail)
                }
            }

            _uiState.update {
                it.copy(
                    allNotifications = displayItems,
                    contentState = if (displayItems.isEmpty()) NotificationsUiContentState.EMPTY else NotificationsUiContentState.SUCCESS
                )
            }
            filterNotifications(_uiState.value.selectedFilter)
        }
    }

    fun markAsRead(notificationId: String) {
        _uiState.update { state ->
            val updatedAll = state.allNotifications.map { item ->
                if (item.notification.id == notificationId && !item.leido) {
                    val updatedRelation = item.userNotification.copy(
                        leido = true,
                        fechaLeido = LocalDateTime.now()
                    )
                    item.copy(userNotification = updatedRelation)
                } else {
                    item
                }
            }
            state.copy(allNotifications = updatedAll)
        }
        filterNotifications(_uiState.value.selectedFilter)
    }

    fun markAllAsRead() {
        _uiState.update { state ->
            val updatedAll = state.allNotifications.map { item ->
                if (!item.leido) {
                    val updatedRelation = item.userNotification.copy(
                        leido = true,
                        fechaLeido = LocalDateTime.now()
                    )
                    item.copy(userNotification = updatedRelation)
                } else {
                    item
                }
            }
            state.copy(allNotifications = updatedAll)
        }
        filterNotifications(_uiState.value.selectedFilter)
    }

    fun filterNotifications(filter: NotificationFilter) {
        val source = _uiState.value.allNotifications
        val filtered = when (filter) {
            NotificationFilter.TODAS -> source
            NotificationFilter.NO_LEIDAS -> source.filter { !it.leido }
        }
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                visibleNotifications = filtered,
                contentState = if (filtered.isEmpty()) {
                    NotificationsUiContentState.EMPTY
                } else {
                    NotificationsUiContentState.SUCCESS
                }
            )
        }
    }
}
