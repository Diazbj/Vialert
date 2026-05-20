package com.example.myapplication.features.explore

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    val searchQuery = mutableStateOf("")
    val isSearching = mutableStateOf(false)

    private val _filteredReports = MutableStateFlow<List<Report>>(emptyList())
    val filteredReports: StateFlow<List<Report>> = _filteredReports.asStateFlow()

    init {
        viewModelScope.launch {
            reportRepository.reports.collect { all ->
                _filteredReports.value = all
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        filterReports(query)
    }

    fun onSearch() {
        filterReports(searchQuery.value)
    }

    private fun filterReports(query: String) {
        val all = reportRepository.getAll()
        _filteredReports.value = if (query.isBlank()) all
        else all.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true) ||
            it.type.contains(query, ignoreCase = true)
        }
    }

    fun clearSearch() {
        searchQuery.value = ""
        _filteredReports.value = reportRepository.getAll()
    }
}
