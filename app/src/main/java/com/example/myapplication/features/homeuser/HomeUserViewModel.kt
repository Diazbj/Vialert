package com.example.myapplication.features.homeuser

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Location
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus

class HomeUserViewModel : ViewModel() {

    private val _uiState = mutableStateOf(
        HomeUserUiState(
            userName = "Juan",
            reports = mockReports,
            reportTimes = mockReportTimes,
            reportImportanceCounts = mockImportanceCounts
        )
    )

    val uiState: State<HomeUserUiState> = _uiState

    companion object {
        private val mockReports = listOf(
            Report(
                id = "report-1",
                title = "Semáforo fuera de servicio",
                description = "El semáforo de la intersección principal no cambia de luz desde hace horas.",
                location = Location(latitude = 4.6097, longitude = -74.0817),
                status = ReportStatus.PENDING,
                type = "Movilidad",
                photoUrl = "https://images.unsplash.com/photo-1457530378978-8bac673b8062?auto=format&fit=crop&w=1200&q=80",
                ownerId = "user-1"
            ),
            Report(
                id = "report-2",
                title = "Hueco en vía principal",
                description = "Hueco profundo en la calzada que genera riesgo para motos y ciclistas.",
                location = Location(latitude = 4.6486, longitude = -74.2479),
                status = ReportStatus.IN_PROGRESS,
                type = "Infraestructura",
                photoUrl = "https://images.unsplash.com/photo-1489515217757-5fd1be406fef?auto=format&fit=crop&w=1200&q=80",
                ownerId = "user-1"
            ),
            Report(
                id = "report-3",
                title = "Vehículo sospechoso",
                description = "Vehículo estacionado hace varios días sin placas visibles en zona residencial.",
                location = Location(latitude = 4.7110, longitude = -74.0721),
                status = ReportStatus.RESOLVED,
                type = "Seguridad",
                photoUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=80",
                ownerId = "user-1"
            )
        )

        private val mockReportTimes = mapOf(
            "report-1" to "Hace 12 min",
            "report-2" to "Hace 1 h",
            "report-3" to "Hace 3 h"
        )

        private val mockImportanceCounts = mapOf(
            "report-1" to 19,
            "report-2" to 11,
            "report-3" to 7
        )
    }

    fun onSupportAction() {
        // Reserved for future support center integration.
    }
}

data class HomeUserUiState(
    val userName: String = "",
    val reports: List<Report> = emptyList(),
    val reportTimes: Map<String, String> = emptyMap(),
    val reportImportanceCounts: Map<String, Int> = emptyMap()
)
