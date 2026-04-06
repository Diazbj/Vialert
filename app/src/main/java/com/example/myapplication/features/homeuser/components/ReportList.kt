package com.example.myapplication.features.homeuser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Report

@Composable
fun ReportList(
    reports: List<Report>,
    reportTimes: Map<String, String>,
    importanceCounts: Map<String, Int>,
    onImportantClick: (Report) -> Unit,
    onShareClick: (Report) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = reports, key = { it.id }) { report ->
            ReportCard(
                report = report,
                timeLabel = reportTimes[report.id].orEmpty(),
                importantCount = importanceCounts[report.id] ?: 0,
                onImportantClick = onImportantClick,
                onShareClick = onShareClick
            )
        }
    }
}
