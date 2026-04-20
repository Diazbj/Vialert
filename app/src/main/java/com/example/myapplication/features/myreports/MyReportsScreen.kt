package com.example.myapplication.features.myreports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.core.components.StatusCategoryChip
import com.example.myapplication.core.navigation.ReportDetail
import com.example.myapplication.core.navigation.NewReport
import com.example.myapplication.core.theme.VialertPurple
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.features.homeuser.components.MainLayout

@Composable
fun MyReportsScreen(
    viewModel: MyReportsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    // Diálogo de confirmación para eliminación lógica
    if (uiState.reportToDelete != null) {
        AlertDialog(
            onDismissRequest = viewModel::hideDeleteConfirmation,
            title = { Text(stringResource(R.string.my_reports_delete_dialog_title)) },
            text = { Text(stringResource(R.string.my_reports_delete_dialog_text)) },
            confirmButton = {
                TextButton(
                    onClick = viewModel::deleteReport,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text(stringResource(R.string.my_reports_delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideDeleteConfirmation) {
                    Text(stringResource(R.string.my_reports_cancel))
                }
            }
        )
    }

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
                text = stringResource(R.string.my_reports_title),
                style = MaterialTheme.typography.headlineSmall,
                color = VialertPurple,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ReportsTabs(
                selectedStatus = uiState.selectedStatus,
                onStatusSelected = viewModel::filterByStatus
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (uiState.contentState) {
                ReportsUiContentState.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VialertPurple)
                    }
                }

                ReportsUiContentState.EMPTY -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.my_reports_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                ReportsUiContentState.SUCCESS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.visibleReports, key = { it.id }) { report ->
                            ReportItemCard(
                                report = report,
                                dateLabel = uiState.reportDates[report.id].orEmpty(),
                                onViewDetails = {
                                    navController?.navigate(ReportDetail(reportId = report.id))
                                },
                                onEdit = {
                                    navController?.navigate(NewReport(reportId = report.id))
                                },
                                onDelete = {
                                    viewModel.showDeleteConfirmation(report)
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
    selectedStatus: ReportStatus?,
    onStatusSelected: (ReportStatus?) -> Unit
) {
    val tabs = listOf(null) + ReportStatus.entries
    val selectedIndex = tabs.indexOf(selectedStatus)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex.coerceAtLeast(0),
        edgePadding = 0.dp,
        divider = {},
        containerColor = Color.White,
        contentColor = VialertPurple,
        indicator = { tabPositions ->
            if (selectedIndex != -1 && selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = VialertPurple
                )
            }
        }
    ) {
        tabs.forEach { status ->
            val label = status?.displayName ?: stringResource(R.string.my_reports_tab_all)
            
            Tab(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                text = { 
                    Text(
                        text = label,
                        color = if (selectedStatus == status) VialertPurple else Color.Gray
                    ) 
                }
            )
        }
    }
}

@Composable
private fun ReportItemCard(
    report: Report,
    dateLabel: String,
    onViewDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkResolved: () -> Unit
) {
    val isDeleted = report.status == ReportStatus.DELETED
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isDeleted, onClick = onViewDetails),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDeleted) Color(0xFFF5F5F5) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDeleted) 0.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = if (isDeleted) 0.6f else 1.0f }
                .padding(12.dp)
        ) {
            // Imagen a la izquierda
            AsyncImage(
                model = report.photoUrl,
                contentDescription = report.title,
                modifier = Modifier
                    .size(width = 110.dp, height = 120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Columna de información a la derecha
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 1. Estado del reporte
                StatusCategoryChip(
                    text = report.status.displayName,
                    baseColor = report.status.color
                )

                // 2. Título
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDeleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                // 3. Categoría
                val categoryEnum = ReportCategory.entries.find { it.displayName == report.type }
                StatusCategoryChip(
                    text = categoryEnum?.displayName ?: report.type,
                    baseColor = if (isDeleted) Color.LightGray else (categoryEnum?.color ?: Color.Gray)
                )

                // 4. Fecha de creación
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 5. Botón RESOLVER
                if (report.status == ReportStatus.IN_PROGRESS) {
                    Button(
                        onClick = onMarkResolved,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = stringResource(R.string.my_reports_btn_resolve))
                    }
                }

                // 6. Acciones
                if (!isDeleted) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, stringResource(R.string.my_reports_icon_edit), tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, stringResource(R.string.my_reports_icon_delete), tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
