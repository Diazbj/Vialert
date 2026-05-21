package com.example.myapplication.features.newreport

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myapplication.core.navigation.NewReport
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.data.service.ImageUploadService
import com.example.myapplication.domain.model.Location
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.ReportRepository
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class NewReportUiState(
    val reportId: String? = null,
    val title: String = "",
    val category: ReportCategory? = null,
    val description: String = "",
    val location: Location? = null,
    val address: String = "Ubicación no seleccionada",
    val images: List<String> = emptyList(),
    val status: ReportStatus = ReportStatus.PENDING,
    val selectedImageUri: Uri? = null
) {
    val isFormValid: Boolean
        get() = title.isNotBlank() && category != null && description.isNotBlank()
    val isEditMode: Boolean
        get() = reportId != null
}

@HiltViewModel
class NewReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionDataStore: SessionDataStore,
    private val imageUploadService: ImageUploadService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<NewReport>()

    private val _uiState = MutableStateFlow(NewReportUiState(reportId = route.reportId))
    val uiState: StateFlow<NewReportUiState> = _uiState.asStateFlow()

    private val _submitResult = MutableStateFlow<RequestResult?>(null)
    val submitResult: StateFlow<RequestResult?> = _submitResult.asStateFlow()

    init {
        checkIfEditMode()
    }

    private fun checkIfEditMode() {
        val id = _uiState.value.reportId
        if (id != null) {
            viewModelScope.launch {
                val report = reportRepository.getById(id)
                if (report != null) {
                    val category = ReportCategory.entries.find { it.displayName == report.type }
                    _uiState.update {
                        it.copy(
                            title = report.title,
                            category = category,
                            description = report.description,
                            location = report.location,
                            status = report.status
                        )
                    }
                }
            }
        }
    }

    fun updateTitle(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun updateCategory(category: ReportCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun onLocationSelected(lat: Double, lng: Double) {
        _uiState.update {
            it.copy(
                location = Location(lat, lng),
                address = "Lat: ${"%.5f".format(lat)}, Lng: ${"%.5f".format(lng)}"
            )
        }
    }

    fun onSubmit() {
        val current = _uiState.value
        if (!current.isFormValid) {
            _submitResult.value = RequestResult.Failure("Completa todos los campos obligatorios")
            return
        }

        viewModelScope.launch {
            _submitResult.value = RequestResult.Loading

            try {
                val session = sessionDataStore.sessionFlow.firstOrNull()
                val ownerId = session?.userId ?: "anonymous"

                val photoUrl = if (current.selectedImageUri != null) {
                    imageUploadService.uploadReportImage(current.selectedImageUri, ownerId)
                } else ""

                if (current.isEditMode) {
                    val existingReport = reportRepository.getById(current.reportId!!)
                    if (existingReport != null) {
                        val updatedReport = existingReport.copy(
                            title = current.title,
                            description = current.description,
                            type = current.category?.displayName ?: existingReport.type,
                            location = current.location ?: existingReport.location,
                            photoUrl = if (photoUrl.isNotBlank()) photoUrl else existingReport.photoUrl
                        )
                        reportRepository.update(updatedReport)
                        _submitResult.value = RequestResult.Success("Reporte actualizado exitosamente")
                    }
                } else {
                    val newReport = Report(
                        id = UUID.randomUUID().toString(),
                        title = current.title,
                        description = current.description,
                        location = current.location ?: Location(0.0, 0.0),
                        status = ReportStatus.PENDING,
                        type = current.category?.displayName ?: "Otros",
                        photoUrl = photoUrl,
                        ownerId = ownerId
                    )
                    reportRepository.create(newReport)

                    if (ownerId != "anonymous") {
                        val user = userRepository.getById(ownerId)
                        if (user != null) {
                            userRepository.update(user.copy(score = user.score + 10))
                        }
                        notificationRepository.create(
                            Notification(
                                tipo = TipoNotificacion.REPORTE_CREADO,
                                titulo = "Reporte publicado",
                                mensaje = "Tu reporte \"${current.title}\" fue publicado exitosamente.",
                                reporteId = newReport.id,
                                destinatarioId = ownerId,
                                creadoEn = System.currentTimeMillis(),
                                creadoPorId = ownerId
                            )
                        )
                    }

                    _submitResult.value = RequestResult.Success("Reporte publicado exitosamente (+10 pts)")
                }
            } catch (e: Exception) {
                _submitResult.value = RequestResult.Failure(e.message ?: "Error al procesar el reporte")
            }
        }
    }

    fun clearResult() {
        _submitResult.value = null
    }

    fun resetForm() {
        _uiState.value = NewReportUiState()
        clearResult()
    }
}
