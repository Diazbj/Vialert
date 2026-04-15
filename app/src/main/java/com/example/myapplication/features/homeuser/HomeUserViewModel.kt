package com.example.myapplication.features.homeuser

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeUserViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(
        HomeUserUiState(
            userName = "Juan",
            reports = reportRepository.getAll(),
            reportTimes = mockReportTimes,
            reportImportanceCounts = mockImportanceCounts
        )
    )

    val uiState: State<HomeUserUiState> = _uiState

    companion object {
        private val mockReportTimes = mapOf(
            "101" to "Hace 12 min",
            "102" to "Hace 1 h",
            "103" to "Hace 3 h"
        )

        private val mockImportanceCounts = mapOf(
            "101" to 19,
            "102" to 11,
            "103" to 7
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
