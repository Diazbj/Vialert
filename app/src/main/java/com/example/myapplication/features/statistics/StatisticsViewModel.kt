package com.example.myapplication.features.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class StatisticsUiState(
    val totalReports: Int = 0,
    val resolvedReports: Int = 0,
    val inProgressReports: Int = 0,
    val pendingReports: Int = 0,
    val weeklyData: List<Int> = listOf(0, 0, 0, 0),
    val categoryData: List<Pair<String, Float>> = emptyList()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            reportRepository.reports.collect { reports ->
                val active = reports.filter { it.status != ReportStatus.DELETED }
                _uiState.value = StatisticsUiState(
                    totalReports = active.size,
                    resolvedReports = active.count { it.status == ReportStatus.RESOLVED },
                    inProgressReports = active.count { it.status == ReportStatus.IN_PROGRESS },
                    pendingReports = active.count { it.status == ReportStatus.PENDING },
                    weeklyData = calculateWeeklyData(active.map { it.createdAt }),
                    categoryData = calculateCategoryData(active.map { it.type })
                )
            }
        }
    }

    private fun calculateWeeklyData(timestamps: List<Long>): List<Int> {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)
        val weeks = IntArray(4)
        timestamps.forEach { ts ->
            val cal = Calendar.getInstance().apply { timeInMillis = ts }
            if (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
                val weekIndex = ((cal.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(3)
                weeks[weekIndex]++
            }
        }
        return weeks.toList()
    }

    private fun calculateCategoryData(types: List<String>): List<Pair<String, Float>> {
        if (types.isEmpty()) return emptyList()
        val grouped = types.groupingBy { it }.eachCount()
        return grouped.map { (type, count) ->
            type to count.toFloat() / types.size
        }.sortedByDescending { it.second }
    }
}
