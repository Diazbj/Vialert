package com.example.myapplication.features.myreports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReportsUiContentState {
    LOADING,
    SUCCESS,
    EMPTY
}

data class ReportsUiState(
    val visibleReports: List<Report> = emptyList(),
    val selectedStatus: ReportStatus? = null, // null representa "TODOS"
    val contentState: ReportsUiContentState = ReportsUiContentState.LOADING,
    val reportDates: Map<String, String> = emptyMap()
)

@HiltViewModel
class MyReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow<ReportStatus?>(null)
    
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        observeReports()
    }

    private fun observeReports() {
        viewModelScope.launch {
            combine(
                reportRepository.reports,
                sessionDataStore.sessionFlow,
                _selectedStatus
            ) { allReports, session, status ->
                val userId = session?.userId ?: ""
                val myReports = allReports.filter { it.ownerId == userId }
                
                val filteredByStatus = if (status == null) {
                    myReports
                } else {
                    myReports.filter { it.status == status }
                }

                Triple(filteredByStatus, status, myReports.isEmpty())
            }.collect { (filteredReports, status, isEmpty) ->
                _uiState.update { state ->
                    state.copy(
                        visibleReports = filteredReports,
                        selectedStatus = status,
                        contentState = if (filteredReports.isEmpty()) ReportsUiContentState.EMPTY else ReportsUiContentState.SUCCESS,
                        reportDates = generateMockDates(filteredReports)
                    )
                }
            }
        }
    }

    fun filterByStatus(status: ReportStatus?) {
        _selectedStatus.value = status
    }

    fun deleteReport(id: String) {
        viewModelScope.launch {
            reportRepository.delete(id)
        }
    }

    fun markAsResolved(reportId: String) {
        viewModelScope.launch {
            val report = reportRepository.getById(reportId)
            if (report != null && report.status == ReportStatus.IN_PROGRESS) {
                reportRepository.update(report.copy(status = ReportStatus.RESOLVED))
            }
        }
    }

    private fun generateMockDates(reports: List<Report>): Map<String, String> {
        return reports.associate { it.id to "Recién actualizado" }
    }
}
