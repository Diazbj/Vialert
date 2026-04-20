package com.example.myapplication.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.model.User
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

data class ModerationUiState(
    val pendingReports: List<ReportWithOwner> = emptyList()
)

data class ReportWithOwner(
    val report: Report,
    val ownerName: String
)

@HiltViewModel
class AdminReportModerationViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModerationUiState())
    val uiState: StateFlow<ModerationUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                reportRepository.reports,
                userRepository.users
            ) { reports, users ->
                val pending = reports
                    .filter { it.status == ReportStatus.PENDING }
                    .sortedByDescending { it.createdAt }
                    .map { report ->
                        val owner = users.find { it.id == report.ownerId }
                        ReportWithOwner(
                            report = report,
                            ownerName = if (owner != null) "${owner.firstName} ${owner.lastName}" else "Desconocido"
                        )
                    }
                ModerationUiState(pendingReports = pending)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun verifyReport(reportId: String) {
        val report = reportRepository.getById(reportId) ?: return
        reportRepository.update(report.copy(status = ReportStatus.IN_PROGRESS))
    }

    fun rejectReport(reportId: String) {
        val report = reportRepository.getById(reportId) ?: return
        reportRepository.update(report.copy(status = ReportStatus.DELETED))
    }
}
