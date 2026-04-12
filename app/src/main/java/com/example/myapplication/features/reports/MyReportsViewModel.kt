package com.example.myapplication.features.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Location
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ReportsTab {
    TODOS,
    ACTIVOS,
    FINALIZADOS
}

enum class ReportsUiContentState {
    LOADING,
    SUCCESS,
    EMPTY
}

data class ReportsUiState(
    val allReports: List<Report> = emptyList(),
    val visibleReports: List<Report> = emptyList(),
    val selectedTab: ReportsTab = ReportsTab.TODOS,
    val contentState: ReportsUiContentState = ReportsUiContentState.LOADING,
    val reportDates: Map<String, String> = emptyMap()
)

class MyReportsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        getReports()
    }

    fun getReports() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(contentState = ReportsUiContentState.LOADING)
            }

            _uiState.update {
                it.copy(
                    allReports = mockReports,
                    reportDates = mockDates,
                    selectedTab = ReportsTab.TODOS
                )
            }

            filterReports(ReportsTab.TODOS)
        }
    }

    fun deleteReport(id: String) {
        _uiState.update { state ->
            state.copy(allReports = state.allReports.filterNot { it.id == id })
        }
        filterReports(_uiState.value.selectedTab)
    }

    fun updateReport(report: Report) {
        _uiState.update { state ->
            state.copy(
                allReports = state.allReports.map { current ->
                    if (current.id == report.id) report else current
                }
            )
        }
        filterReports(_uiState.value.selectedTab)
    }

    fun filterReports(tab: ReportsTab) {
        val state = _uiState.value
        val filtered = when (tab) {
            ReportsTab.TODOS -> state.allReports
            ReportsTab.ACTIVOS -> state.allReports.filter {
                it.status == ReportStatus.PENDING || it.status == ReportStatus.IN_PROGRESS
            }
            ReportsTab.FINALIZADOS -> state.allReports.filter { it.status == ReportStatus.RESOLVED }
        }

        _uiState.update {
            it.copy(
                selectedTab = tab,
                visibleReports = filtered,
                contentState = if (filtered.isEmpty()) ReportsUiContentState.EMPTY else ReportsUiContentState.SUCCESS
            )
        }
    }

    fun markAsResolved(reportId: String) {
        val target = _uiState.value.allReports.firstOrNull { it.id == reportId } ?: return
        if (target.status != ReportStatus.IN_PROGRESS) return
        updateReport(target.copy(status = ReportStatus.RESOLVED))
    }

    private companion object {
        val mockReports = listOf(
            Report(
                id = "r-001",
                title = "Poste de luz apagado",
                description = "Luminaria sin funcionamiento en la avenida principal.",
                location = Location(latitude = 4.6483, longitude = -74.2479),
                status = ReportStatus.PENDING,
                type = "Infraestructura",
                photoUrl = "https://images.unsplash.com/photo-1457530378978-8bac673b8062?auto=format&fit=crop&w=800&q=80",
                ownerId = "user-1"
            ),
            Report(
                id = "r-002",
                title = "Daño en señal de tránsito",
                description = "Señal parcialmente caída y obstruyendo el andén.",
                location = Location(latitude = 4.7110, longitude = -74.0721),
                status = ReportStatus.IN_PROGRESS,
                type = "Seguridad Vial",
                photoUrl = "https://images.unsplash.com/photo-1489515217757-5fd1be406fef?auto=format&fit=crop&w=800&q=80",
                ownerId = "user-1"
            ),
            Report(
                id = "r-003",
                title = "Bache profundo",
                description = "Hueco en carril central con riesgo para motociclistas.",
                location = Location(latitude = 4.6097, longitude = -74.0817),
                status = ReportStatus.RESOLVED,
                type = "Infraestructura",
                photoUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80",
                ownerId = "user-1"
            )
        )

        val mockDates = mapOf(
            "r-001" to "12 Oct 2023",
            "r-002" to "23 Nov 2023",
            "r-003" to "15 Ene 2024"
        )
    }
}