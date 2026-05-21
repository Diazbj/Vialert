package com.example.myapplication.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.service.AiReportAnalysis
import com.example.myapplication.data.service.GeminiAiService
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.ReportRepository
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatusCounts(
    val all: Int = 0,
    val pending: Int = 0,
    val inProgress: Int = 0,
    val resolved: Int = 0
)

data class ModerationUiState(
    val filteredReports: List<ReportWithOwner> = emptyList(),
    val selectedFilter: ReportStatus? = null,
    val counts: StatusCounts = StatusCounts(),
    val rejectDialogReportId: String? = null
)

data class ReportWithOwner(
    val report: Report,
    val ownerName: String
)

@HiltViewModel
class AdminReportModerationViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val geminiAiService: GeminiAiService
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<ReportStatus?>(null)

    private val _uiState = MutableStateFlow(ModerationUiState())
    val uiState: StateFlow<ModerationUiState> = _uiState.asStateFlow()

    private val _aiAnalyses = MutableStateFlow<Map<String, AiReportAnalysis>>(emptyMap())
    val aiAnalyses: StateFlow<Map<String, AiReportAnalysis>> = _aiAnalyses.asStateFlow()

    init {
        observeData()
    }

    fun setFilter(status: ReportStatus?) {
        _selectedFilter.value = status
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                reportRepository.reports,
                userRepository.users,
                _selectedFilter
            ) { reports, users, filter ->
                val active = reports
                    .filter { it.status != ReportStatus.DELETED }
                    .sortedByDescending { it.createdAt }

                val counts = StatusCounts(
                    all = active.size,
                    pending = active.count { it.status == ReportStatus.PENDING },
                    inProgress = active.count { it.status == ReportStatus.IN_PROGRESS },
                    resolved = active.count { it.status == ReportStatus.RESOLVED }
                )

                val filtered = (if (filter == null) active else active.filter { it.status == filter })
                    .map { report ->
                        val owner = users.find { it.id == report.ownerId }
                        ReportWithOwner(
                            report = report,
                            ownerName = if (owner != null) "${owner.firstName} ${owner.lastName}" else "Desconocido"
                        )
                    }

                ModerationUiState(
                    filteredReports = filtered,
                    selectedFilter = filter,
                    counts = counts
                )
            }.collect { state ->
                _uiState.value = state

                // Solo analizar con IA los reportes pendientes nuevos
                val pendingNew = state.filteredReports
                    .filter { it.report.status == ReportStatus.PENDING && !_aiAnalyses.value.containsKey(it.report.id) }
                if (pendingNew.isNotEmpty()) {
                    viewModelScope.launch {
                        pendingNew.forEachIndexed { index, item ->
                            if (index > 0) delay(4000L)
                            analyzeReportSuspend(item.report)
                        }
                    }
                }
            }
        }
    }

    fun analyzeReport(report: Report) {
        viewModelScope.launch { analyzeReportSuspend(report) }
    }

    private suspend fun analyzeReportSuspend(report: Report) {
        _aiAnalyses.update { current ->
            current + (report.id to AiReportAnalysis(
                recommendation = "", confidence = "", keyPoints = emptyList(),
                reasoning = "", isLoading = true
            ))
        }
        val analysis = geminiAiService.analyzeReport(report)
        _aiAnalyses.update { current -> current + (report.id to analysis) }
    }

    fun verifyReport(reportId: String) {
        val report = reportRepository.getById(reportId) ?: return
        viewModelScope.launch {
            reportRepository.update(report.copy(status = ReportStatus.IN_PROGRESS))
            notificationRepository.create(
                Notification(
                    tipo = TipoNotificacion.REPORTE_ACTUALIZADO,
                    titulo = "Reporte verificado ✓",
                    mensaje = "Tu reporte \"${report.title}\" fue verificado y está en proceso de atención.",
                    reporteId = report.id,
                    destinatarioId = report.ownerId,
                    creadoEn = System.currentTimeMillis(),
                    creadoPorId = userRepository.getCurrentUserId() ?: ""
                )
            )
        }
    }

    fun resolveReport(reportId: String) {
        val report = reportRepository.getById(reportId) ?: return
        viewModelScope.launch {
            reportRepository.update(report.copy(status = ReportStatus.RESOLVED))
            notificationRepository.create(
                Notification(
                    tipo = TipoNotificacion.REPORTE_CERRADO,
                    titulo = "Reporte resuelto ✓",
                    mensaje = "Tu reporte \"${report.title}\" ha sido resuelto satisfactoriamente.",
                    reporteId = report.id,
                    destinatarioId = report.ownerId,
                    creadoEn = System.currentTimeMillis(),
                    creadoPorId = userRepository.getCurrentUserId() ?: ""
                )
            )
        }
    }

    fun showRejectDialog(reportId: String) {
        _uiState.update { it.copy(rejectDialogReportId = reportId) }
    }

    fun dismissRejectDialog() {
        _uiState.update { it.copy(rejectDialogReportId = null) }
    }

    fun rejectReport(reportId: String, reason: String) {
        val report = reportRepository.getById(reportId) ?: return
        dismissRejectDialog()
        viewModelScope.launch {
            reportRepository.update(report.copy(status = ReportStatus.DELETED, rejectionReason = reason.trim()))
            notificationRepository.create(
                Notification(
                    tipo = TipoNotificacion.REPORTE_CERRADO,
                    titulo = "Reporte rechazado",
                    mensaje = "Tu reporte \"${report.title}\" fue rechazado. Motivo: ${reason.trim().ifBlank { "No cumple los criterios de la plataforma." }}",
                    reporteId = report.id,
                    destinatarioId = report.ownerId,
                    creadoEn = System.currentTimeMillis(),
                    creadoPorId = userRepository.getCurrentUserId() ?: ""
                )
            )
        }
    }
}
