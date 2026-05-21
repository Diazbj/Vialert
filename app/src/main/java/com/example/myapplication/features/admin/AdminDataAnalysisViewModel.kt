package com.example.myapplication.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class TimeTab { WEEKLY, MONTHLY, YEARLY }

data class ZoneData(val rank: Int, val lat: Double, val lng: Double, val count: Int)

data class AdminDataUiState(
    val total: Int = 0,
    val resolved: Int = 0,
    val resolvedTrend: String = "0%",
    val criticalPct: Int = 0,
    val criticalTrend: String = "0%",
    val categoryData: List<Pair<String, Float>> = emptyList(),
    val topZones: List<ZoneData> = emptyList(),
    val periodReports: List<Report> = emptyList()
)

@HiltViewModel
class AdminDataAnalysisViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(TimeTab.WEEKLY)
    val selectedTab: StateFlow<TimeTab> = _selectedTab.asStateFlow()

    private val _uiState = MutableStateFlow(AdminDataUiState())
    val uiState: StateFlow<AdminDataUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(reportRepository.reports, _selectedTab) { reports, tab ->
                computeState(reports.filter { it.status != ReportStatus.DELETED }, tab)
            }.collect { _uiState.value = it }
        }
    }

    fun selectTab(tab: TimeTab) { _selectedTab.value = tab }

    private fun computeState(active: List<Report>, tab: TimeTab): AdminDataUiState {
        val now = System.currentTimeMillis()
        val (periodStart, prevStart, prevEnd) = periodBounds(now, tab)

        val current = active.filter { it.createdAt >= periodStart }
        val previous = active.filter { it.createdAt in prevStart until prevEnd }

        val total = current.size
        val resolved = current.count { it.status == ReportStatus.RESOLVED }
        val pending = current.count { it.status == ReportStatus.PENDING }

        val criticalPct = if (total > 0) pending * 100 / total else 0
        val prevCriticalPct = if (previous.isNotEmpty())
            previous.count { it.status == ReportStatus.PENDING } * 100 / previous.size else 0

        val categoryData = if (total == 0) emptyList() else
            current.groupingBy { it.type }.eachCount()
                .map { (type, count) -> type to count.toFloat() / total }
                .sortedByDescending { it.second }

        val topZones = current
            .filter { it.location.latitude != 0.0 || it.location.longitude != 0.0 }
            .groupBy { r ->
                val lat = Math.round(r.location.latitude * 100) / 100.0
                val lng = Math.round(r.location.longitude * 100) / 100.0
                lat to lng
            }
            .entries
            .sortedByDescending { it.value.size }
            .take(3)
            .mapIndexed { i, entry ->
                ZoneData(rank = i + 1, lat = entry.key.first, lng = entry.key.second, count = entry.value.size)
            }

        return AdminDataUiState(
            total = total,
            resolved = resolved,
            resolvedTrend = trend(resolved, previous.count { it.status == ReportStatus.RESOLVED }),
            criticalPct = criticalPct,
            criticalTrend = trend(criticalPct, prevCriticalPct),
            categoryData = categoryData,
            topZones = topZones,
            periodReports = current
        )
    }

    private fun periodBounds(now: Long, tab: TimeTab): Triple<Long, Long, Long> = when (tab) {
        TimeTab.WEEKLY -> {
            val start = now - 7L * 24 * 3600 * 1000
            Triple(start, start - 7L * 24 * 3600 * 1000, start)
        }
        TimeTab.MONTHLY -> {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            val monthStart = cal.timeInMillis
            cal.add(Calendar.MONTH, -1)
            Triple(monthStart, cal.timeInMillis, monthStart)
        }
        TimeTab.YEARLY -> {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_YEAR, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            val yearStart = cal.timeInMillis
            cal.add(Calendar.YEAR, -1)
            Triple(yearStart, cal.timeInMillis, yearStart)
        }
    }

    private fun trend(current: Int, previous: Int): String {
        if (previous == 0) return if (current > 0) "+100%" else "0%"
        val pct = ((current - previous) * 100.0 / previous).toInt()
        return if (pct >= 0) "+$pct%" else "$pct%"
    }
}
