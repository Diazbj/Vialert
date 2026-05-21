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
import kotlin.math.*

enum class LocationFilter(val label: String, val radiusKm: Double?) {
    ALL("Todos", null),
    NEARBY("Cercanos", 5.0),
    CITY("En la ciudad", 30.0)
}

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
                val active = allReports.filter { it.status != ReportStatus.DELETED }
                _uiState.update { state ->
                    val filtered = applyLocationFilter(active, state.locationFilter, state.userLat, state.userLng)
                    state.copy(reports = filtered, reportTimes = generateTimes(filtered))
                }
            }
        }
    }

    fun setLocationFilter(filter: LocationFilter, userLat: Double?, userLng: Double?) {
        val allActive = reportRepository.reports.value.filter { it.status != ReportStatus.DELETED }
        val filtered = applyLocationFilter(allActive, filter, userLat, userLng)
        _uiState.update { it.copy(locationFilter = filter, userLat = userLat, userLng = userLng, reports = filtered, reportTimes = generateTimes(filtered)) }
    }

    private fun applyLocationFilter(reports: List<Report>, filter: LocationFilter, userLat: Double?, userLng: Double?): List<Report> {
        val radius = filter.radiusKm ?: return reports
        if (userLat == null || userLng == null) return reports
        return reports.filter { haversineKm(userLat, userLng, it.location.latitude, it.location.longitude) <= radius }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
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
    val reportTimes: Map<String, String> = emptyMap(),
    val locationFilter: LocationFilter = LocationFilter.ALL,
    val userLat: Double? = null,
    val userLng: Double? = null
)
