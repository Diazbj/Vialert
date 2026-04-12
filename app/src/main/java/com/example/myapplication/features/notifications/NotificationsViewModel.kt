package com.example.myapplication.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Notificacion
import com.example.myapplication.domain.model.NotificacionUsuario
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.domain.model.Usuario
import com.example.myapplication.domain.model.UserRole
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationFilter {
	TODAS,
	NO_LEIDAS
}

enum class NotificationsUiContentState {
	LOADING,
	SUCCESS,
	EMPTY
}

data class NotificationsUiState(
	val allNotifications: List<NotificacionUsuario> = emptyList(),
	val visibleNotifications: List<NotificacionUsuario> = emptyList(),
	val selectedFilter: NotificationFilter = NotificationFilter.TODAS,
	val contentState: NotificationsUiContentState = NotificationsUiContentState.LOADING
)

class NotificationsViewModel : ViewModel() {

	private val _uiState = MutableStateFlow(NotificationsUiState())
	val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

	init {
		getNotifications()
	}

	fun getNotifications() {
		viewModelScope.launch {
			_uiState.update { it.copy(contentState = NotificationsUiContentState.LOADING) }
			_uiState.update {
				it.copy(
					allNotifications = mockNotifications,
					selectedFilter = NotificationFilter.TODAS
				)
			}
			filterNotifications(NotificationFilter.TODAS)
		}
	}

	fun markAsRead(notificationId: Long) {
		_uiState.update { state ->
			state.copy(
				allNotifications = state.allNotifications.map { notificationUser ->
					if (notificationUser.notificacion.id == notificationId && !notificationUser.leido) {
						notificationUser.copy(
							leido = true,
							fechaLeido = LocalDateTime.now()
						)
					} else {
						notificationUser
					}
				}
			)
		}
		filterNotifications(_uiState.value.selectedFilter)
	}

	fun markAllAsRead() {
		_uiState.update { state ->
			state.copy(
				allNotifications = state.allNotifications.map { notificationUser ->
					if (!notificationUser.leido) {
						notificationUser.copy(
							leido = true,
							fechaLeido = LocalDateTime.now()
						)
					} else {
						notificationUser
					}
				}
			)
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

	private companion object {
		val now: LocalDateTime = LocalDateTime.now()

		val sampleUser = Usuario(
			id = "user-1",
			name = "Juan Perez",
			city = "Bogota",
			address = "Calle 123",
			email = "juan@example.com",
			password = "secret",
			role = UserRole.USER
		)

		val mockNotifications = listOf(
			NotificacionUsuario(
				id = 1L,
				notificacion = Notificacion(
					id = 1001L,
					tipo = TipoNotificacion.REPORTE_CERRADO,
					titulo = "Reporte Resuelto",
					mensaje = "Tu reporte de infraestructura fue marcado como resuelto.",
					reporteId = 301L,
					creadoEn = now.minusMinutes(5),
					creadoPor = sampleUser
				),
				usuario = sampleUser,
				leido = false,
				fechaLeido = null,
				enviadoPush = true,
				fechaEnvio = now.minusMinutes(5)
			),
			NotificacionUsuario(
				id = 2L,
				notificacion = Notificacion(
					id = 1002L,
					tipo = TipoNotificacion.REPORTE_COMENTARIO,
					titulo = "Nuevo comentario",
					mensaje = "Un moderador agrego un comentario a tu reporte.",
					reporteId = 302L,
					creadoEn = now.minusHours(1),
					creadoPor = sampleUser
				),
				usuario = sampleUser,
				leido = false,
				fechaLeido = null,
				enviadoPush = true,
				fechaEnvio = now.minusHours(1)
			),
			NotificacionUsuario(
				id = 3L,
				notificacion = Notificacion(
					id = 1003L,
					tipo = TipoNotificacion.REPORTE_ACTUALIZADO,
					titulo = "Reporte actualizado",
					mensaje = "El estado de tu reporte cambio a verificado.",
					reporteId = 303L,
					creadoEn = now.minusDays(1),
					creadoPor = sampleUser
				),
				usuario = sampleUser,
				leido = true,
				fechaLeido = now.minusHours(20),
				enviadoPush = true,
				fechaEnvio = now.minusDays(1)
			)
		)
	}
}