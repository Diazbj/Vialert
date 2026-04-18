package com.example.myapplication.features.homeuser

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeUserViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUserUiState(userName = "Juan"))
    val uiState: StateFlow<HomeUserUiState> = _uiState.asStateFlow()

    init {
        observeReports()
        startTimer()
    }

    private fun observeReports() {
        viewModelScope.launch {
            reportRepository.reports.collect { allReports ->
                // Filtrar reportes eliminados para el Home
                val activeReports = allReports.filter { it.status != ReportStatus.DELETED }
                
                _uiState.update { state ->
                    state.copy(
                        reports = activeReports,
                        reportTimes = generateTimes(activeReports)
                    )
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(60000)
                val currentReports = _uiState.value.reports
                if (currentReports.isNotEmpty()) {
                    _uiState.update { it.copy(reportTimes = generateTimes(currentReports)) }
                }
            }
        }
    }

    private fun generateTimes(reports: List<Report>): Map<String, String> {
        return reports.associate { report ->
            report.id to DateUtils.getRelativeTimeSpanString(
                report.createdAt,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        }
    }

    fun onImportantClick(reportId: String) {
        viewModelScope.launch {
            reportRepository.incrementarImportancia(reportId)
        }
    }

    fun onSupportAction() {
        // Reserved for future support center integration.
    }
}

data class HomeUserUiState(
    val userName: String = "",
    val reports: List<Report> = emptyList(),
    val reportTimes: Map<String, String> = emptyMap()
)
