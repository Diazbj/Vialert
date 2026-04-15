package com.example.myapplication.features.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.core.navigation.ReportDetail
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.features.homeuser.components.MainLayout

@Composable
fun MyReportsScreen(
    viewModel: MyReportsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    MainLayout(
        navController = navController,
        showSupportFab = true,
        onSupportClick = {}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Reportes",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ReportsTabs(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::filterReports
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (uiState.contentState) {
                ReportsUiContentState.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                ReportsUiContentState.EMPTY -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay reportes para mostrar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                ReportsUiContentState.SUCCESS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.visibleReports, key = { it.id }) { report ->
                            ReportItemCard(
                                report = report,
                                date = uiState.reportDates[report.id].orEmpty(),
                                onViewDetails = {
                                    navController?.navigate(ReportDetail)
                                },
                                onEdit = {
                                    navController?.navigate(ReportDetail)
                                },
                                onDelete = {
                                    viewModel.deleteReport(report.id)
                                },
                                onMarkResolved = {
                                    viewModel.markAsResolved(report.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportsTabs(
    selectedTab: ReportsTab,
    onTabSelected: (ReportsTab) -> Unit
) {
    val tabs = listOf(
        ReportsTab.TODOS to "Todos",
        ReportsTab.ACTIVOS to "Activos",
        ReportsTab.FINALIZADOS to "Finalizados"
    )

    TabRow(selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab }) {
        tabs.forEachIndexed { index, (tab, title) ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(text = title) }
            )
        }
    }
}

@Composable
private fun ReportItemCard(
    report: Report,
    date: String,
    onViewDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkResolved: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetails),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = report.photoUrl,
                contentDescription = report.title,
                modifier = Modifier
                    .size(width = 96.dp, height = 84.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    StatusTag(status = report.status)

                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                }

                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${report.type} · $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (report.status == ReportStatus.IN_PROGRESS) {
                    Button(
                        onClick = onMarkResolved,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "RESUELTO")
                    }
                }

                if (report.status == ReportStatus.RESOLVED) {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "RESUELTO")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusTag(status: ReportStatus) {
    val (text, backgroundColor) = when (status) {
        ReportStatus.PENDING -> "PENDIENTE" to Color(0xFFFFF3CD)
        ReportStatus.IN_PROGRESS -> "VERIFICADO" to Color(0xFFE8E8FF)
        ReportStatus.RESOLVED -> "RESUELTO" to Color(0xFFDFF6DD)
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
