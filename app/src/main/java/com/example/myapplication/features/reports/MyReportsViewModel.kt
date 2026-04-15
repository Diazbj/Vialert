package com.example.myapplication.features.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class MyReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

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

            val reports = reportRepository.getAll()
            
            _uiState.update {
                it.copy(
                    allReports = reports,
                    reportDates = mockDates, // Manteniendo los mocks de fechas por ahora
                    selectedTab = ReportsTab.TODOS
                )
            }

            filterReports(ReportsTab.TODOS)
        }
    }

    fun deleteReport(id: String) {
        viewModelScope.launch {
            reportRepository.delete(id)
            _uiState.update { state ->
                state.copy(allReports = state.allReports.filterNot { it.id == id })
            }
            filterReports(_uiState.value.selectedTab)
        }
    }

    fun updateReport(report: Report) {
        viewModelScope.launch {
            reportRepository.update(report)
            _uiState.update { state ->
                state.copy(
                    allReports = state.allReports.map { current ->
                        if (current.id == report.id) report else current
                    }
                )
            }
            filterReports(_uiState.value.selectedTab)
        }
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
        val mockDates = mapOf(
            "101" to "12 Oct 2023",
            "102" to "23 Nov 2023",
            "103" to "15 Ene 2024"
        )
    }
}
