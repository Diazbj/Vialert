package com.example.myapplication.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.ReportRepository
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class SystemReportUiState(
    // Usuarios
    val totalUsers: Int = 0,
    val adminUsers: Int = 0,
    val activeUsersThisMonth: Int = 0,
    // Reportes
    val totalReports: Int = 0,
    val pendingReports: Int = 0,
    val inProgressReports: Int = 0,
    val resolvedReports: Int = 0,
    val deletedReports: Int = 0,
    val reportsThisMonth: Int = 0,
    val reportsThisWeek: Int = 0,
    val resolutionRate: Int = 0,
    val avgResolutionDays: Float = 0f,
    // Categorías más activas
    val topCategories: List<Pair<String, Int>> = emptyList(),
    // Usuarios más activos (por cantidad de reportes)
    val topReporters: List<Pair<String, Int>> = emptyList(),
    // Estado del sistema
    val isLoading: Boolean = true
)

@HiltViewModel
class AdminSystemReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemReportUiState())
    val uiState: StateFlow<SystemReportUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(reportRepository.reports, userRepository.users) { reports, users ->
                val now = System.currentTimeMillis()
                val weekAgo = now - 7L * 24 * 3600 * 1000

                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val monthStart = cal.timeInMillis

                val active = reports.filter { it.status != ReportStatus.DELETED }
                val deleted = reports.filter { it.status == ReportStatus.DELETED }
                val resolved = active.filter { it.status == ReportStatus.RESOLVED }
                val pending = active.filter { it.status == ReportStatus.PENDING }
                val inProgress = active.filter { it.status == ReportStatus.IN_PROGRESS }

                val thisMonth = active.filter { it.createdAt >= monthStart }
                val thisWeek = active.filter { it.createdAt >= weekAgo }

                val resolutionRate = if (active.isNotEmpty()) resolved.size * 100 / active.size else 0

                // Categorías más activas
                val topCategories = active.groupingBy { it.type }.eachCount()
                    .entries.sortedByDescending { it.value }.take(4)
                    .map { it.key to it.value }

                // Top reporteros (usuarios con más reportes activos)
                val topReporters = active.groupingBy { it.ownerId }.eachCount()
                    .entries.sortedByDescending { it.value }.take(3)
                    .map { (uid, count) ->
                        val u = users.find { it.id == uid }
                        val name = if (u != null) "${u.firstName} ${u.lastName}".trim() else "Desconocido"
                        name to count
                    }

                // Usuarios activos este mes (que tienen reportes este mes)
                val activeThisMonth = thisMonth.map { it.ownerId }.toSet().size

                SystemReportUiState(
                    totalUsers = users.size,
                    adminUsers = users.count { it.role == UserRole.ADMIN },
                    activeUsersThisMonth = activeThisMonth,
                    totalReports = active.size,
                    pendingReports = pending.size,
                    inProgressReports = inProgress.size,
                    resolvedReports = resolved.size,
                    deletedReports = deleted.size,
                    reportsThisMonth = thisMonth.size,
                    reportsThisWeek = thisWeek.size,
                    resolutionRate = resolutionRate,
                    topCategories = topCategories,
                    topReporters = topReporters,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }
}
