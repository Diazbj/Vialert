package com.example.myapplication.features.explore

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ExploreViewModel : ViewModel() {

    val searchQuery = mutableStateOf("")
    val isSearching = mutableStateOf(false)

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onSearch() {
        isSearching.value = true
        // TODO: search reports/incidents by location or keyword via API
        isSearching.value = false
    }

    fun clearSearch() {
        searchQuery.value = ""
    }
}
