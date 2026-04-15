package com.example.myapplication.features.newreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.domain.model.Location
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
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
    val title: String = "",
    val category: ReportCategory? = null,
    val description: String = "",
    val location: Location? = null,
    val address: String = "Ubicación no seleccionada",
    val images: List<String> = emptyList()
) {
    val isFormValid: Boolean
        get() = title.isNotBlank() && category != null && description.isNotBlank()
}

@HiltViewModel
class NewReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewReportUiState())
    val uiState: StateFlow<NewReportUiState> = _uiState.asStateFlow()

    private val _submitResult = MutableStateFlow<RequestResult?>(null)
    val submitResult: StateFlow<RequestResult?> = _submitResult.asStateFlow()

    fun updateTitle(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun updateCategory(category: ReportCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun updateLocation(location: Location, address: String) {
        _uiState.update {
            it.copy(
                location = location,
                address = address
            )
        }
    }

    fun addImage(image: String) {
        _uiState.update { state ->
            state.copy(images = state.images + image)
        }
    }

    fun removeImage(image: String) {
        _uiState.update { state ->
            state.copy(images = state.images.filterNot { it == image })
        }
    }

    fun createReport() {
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

                val newReport = Report(
                    id = UUID.randomUUID().toString(),
                    title = current.title,
                    description = current.description,
                    // Implementación futura de location
                    location = current.location ?: Location(0.0, 0.0),
                    status = ReportStatus.PENDING,
                    type = current.category?.displayName ?: "Otros",
                    // Implementación futura de photoUrl
                    photoUrl = if (current.images.isNotEmpty()) current.images.first() else "",
                    ownerId = ownerId
                )

                reportRepository.create(newReport)
                _submitResult.value = RequestResult.Success("Reporte publicado exitosamente")
            } catch (e: Exception) {
                _submitResult.value = RequestResult.Failure(e.message ?: "Error al publicar el reporte")
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
