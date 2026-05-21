package com.example.myapplication.features.profile

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.data.service.ImageUploadService
import com.example.myapplication.domain.model.Report
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
    val pointsProgress: Float = 0f,
    val myReports: List<Report> = emptyList(),
    val allUsers: List<User> = emptyList(),
    val allReports: List<Report> = emptyList(),
    val isUploadingPhoto: Boolean = false,
    val photoUploadError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
    private val sessionDataStore: SessionDataStore,
    private val imageUploadService: ImageUploadService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Logros dinámicos
    val logros = mutableStateListOf<Achievement>()

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

                ObservedData(user, myReports, allUsers, allReports)
            }.collect { data ->
                val user = data.user
                val myReports = data.myReports
                val allUsers = data.allUsers
                val allReports = data.allReports
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
                            ),
                            myReports = myReports,
                            allUsers = allUsers,
                            allReports = allReports
                        )
                    }

                    // Actualizar logros basados en las stats
                    val hasFirstReport = myReports.isNotEmpty()
                    val hasVerified = myReports.count { it.status == ReportStatus.RESOLVED || it.status == ReportStatus.IN_PROGRESS } >= 10
                    
                    logros.clear()
                    logros.add(Achievement("Primer Reporte", "trophy", !hasFirstReport))
                    logros.add(Achievement("10 Verificados", "shield", !hasVerified))
                    logros.add(Achievement("Racha 7 días", "fire", true)) // Pendiente de implementar lógica temporal real
                    logros.add(Achievement("Colaborador", "handshake", user.score < 50))
                    logros.add(Achievement("Guardián Urbano", "lock", user.score < 200))
                    logros.add(Achievement("Influencer Vial", "lock", user.score < 500))
                }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
        }
    }

    fun saveProfile(fullName: String, phone: String, bio: String) {
        val currentUser = _uiState.value.user ?: return
        val parts = fullName.trim().split(" ", limit = 2)
        val firstName = parts.getOrNull(0) ?: ""
        val lastName = parts.getOrNull(1) ?: ""

        if (firstName.isNotBlank()) {
            val updatedUser = currentUser.copy(
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                bio = bio
            )
            viewModelScope.launch {
                userRepository.update(updatedUser)
            }
        }
    }

    fun uploadProfilePhoto(uri: Uri) {
        val currentUser = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, photoUploadError = null) }
            try {
                val url = imageUploadService.uploadProfileImage(uri, currentUser.id)
                userRepository.update(currentUser.copy(profilePictureUrl = url))
                _uiState.update { it.copy(isUploadingPhoto = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUploadingPhoto = false, photoUploadError = e.message) }
            }
        }
    }

    fun changePassword(currentPass: String, newPass: String): Boolean {
        // Con Firebase Auth la contraseña no se almacena localmente, siempre se puede cambiar
        if (newPass.length >= 6) {
            val currentUser = _uiState.value.user ?: return false
            val updatedUser = currentUser.copy(password = newPass)
            viewModelScope.launch {
                userRepository.update(updatedUser)
            }
            return true
        }
        return false
    }
}

data class Achievement(
    val title: String,
    val iconType: String,
    val isLocked: Boolean
)

private data class ObservedData(
    val user: User?,
    val myReports: List<Report>,
    val allUsers: List<User>,
    val allReports: List<Report>
)
