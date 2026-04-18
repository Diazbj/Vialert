package com.example.myapplication.features.profile

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserLevel
import com.example.myapplication.domain.repository.ReportRepository
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileStats(
    val pending: Int = 0,
    val verified: Int = 0,
    val resolved: Int = 0
)

data class ProfileUiState(
    val user: User? = null,
    val userLevel: UserLevel = UserLevel.NIVEL_1,
    val stats: ProfileStats = ProfileStats(),
    val pointsProgress: Float = 0f
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Logros (Mantenidos como estaban)
    val logros = mutableStateListOf(
        Achievement("Primer Reporte", "trophy", false),
        Achievement("10 Verificados", "shield", false),
        Achievement("Racha 7 días", "fire", false),
        Achievement("Colaborador", "handshake", false),
        Achievement("Guardián Urbano", "lock", true),
        Achievement("Influencer Vial", "lock", true)
    )

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                userRepository.users,
                reportRepository.reports,
                sessionDataStore.sessionFlow
            ) { allUsers, allReports, session ->
                val currentUserId = session?.userId ?: ""
                val user = allUsers.find { it.id == currentUserId }
                val myReports = allReports.filter { it.ownerId == currentUserId }
                
                Triple(user, myReports, currentUserId)
            }.collect { (user, myReports, _) ->
                if (user != null) {
                    val level = UserLevel.fromScore(user.score)
                    val pointsInLevel = user.score - level.minPoints
                    val range = level.maxPoints - level.minPoints
                    val progress = if (range > 0) pointsInLevel.toFloat() / range else 0f

                    _uiState.update { state ->
                        state.copy(
                            user = user,
                            userLevel = level,
                            pointsProgress = progress,
                            stats = ProfileStats(
                                pending = myReports.count { it.status == ReportStatus.PENDING },
                                verified = myReports.count { it.status == ReportStatus.IN_PROGRESS },
                                resolved = myReports.count { it.status == ReportStatus.RESOLVED }
                            )
                        )
                    }
                }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
        }
    }

    // Funcionalidad futura
    fun toggleEditing() {}
}

data class Achievement(
    val title: String,
    val iconType: String,
    val isLocked: Boolean
)
