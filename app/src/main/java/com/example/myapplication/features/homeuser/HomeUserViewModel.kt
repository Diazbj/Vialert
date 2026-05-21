package com.example.myapplication.features.homeuser

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeUserViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUserUiState())
    val uiState: StateFlow<HomeUserUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
        observeReports()
        startTimer()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull()
            _uiState.update { it.copy(currentUserId = session?.userId ?: "") }
        }
    }

    private fun observeReports() {
        viewModelScope.launch {
            reportRepository.reports.collect { allReports ->
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
        val userId = _uiState.value.currentUserId
        if (userId.isBlank()) return
        viewModelScope.launch {
            reportRepository.incrementarImportancia(reportId, userId)
        }
    }

    fun onSupportAction() {}
}

data class HomeUserUiState(
    val currentUserId: String = "",
    val reports: List<Report> = emptyList(),
    val reportTimes: Map<String, String> = emptyMap()
)
