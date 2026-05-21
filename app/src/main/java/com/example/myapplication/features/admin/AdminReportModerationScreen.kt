package com.example.myapplication.features.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.service.AiReportAnalysis
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportModerationScreen(
    viewModel: AdminReportModerationViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val aiAnalyses by viewModel.aiAnalyses.collectAsState()

    // Diálogo de motivo de rechazo
    val rejectReportId = uiState.rejectDialogReportId
    if (rejectReportId != null) {
        var reason by remember(rejectReportId) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.dismissRejectDialog() },
            title = { Text("Rechazar reporte", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Escribe el motivo del rechazo. El usuario recibirá esta explicación.", fontSize = 13.sp, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Motivo del rechazo") },
                        placeholder = { Text("Ej: Información insuficiente, imagen no corresponde...") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.rejectReport(rejectReportId, reason) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRejectDialog() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.admin_moderation_topbar_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.admin_moderation_back), tint = Color(0xFF7C3AED))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cabecera + filtros (estática)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    stringResource(R.string.admin_moderation_header),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.admin_moderation_subheader),
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Chips de filtro
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        label = "Todos",
                        count = uiState.counts.all,
                        selected = uiState.selectedFilter == null,
                        color = Color(0xFF7C3AED),
                        onClick = { viewModel.setFilter(null) }
                    )
                    FilterChipItem(
                        label = "Pendientes",
                        count = uiState.counts.pending,
                        selected = uiState.selectedFilter == ReportStatus.PENDING,
                        color = Color(0xFFEF4444),
                        onClick = { viewModel.setFilter(ReportStatus.PENDING) }
                    )
                    FilterChipItem(
                        label = "En revisión",
                        count = uiState.counts.inProgress,
                        selected = uiState.selectedFilter == ReportStatus.IN_PROGRESS,
                        color = Color(0xFFF59E0B),
                        onClick = { viewModel.setFilter(ReportStatus.IN_PROGRESS) }
                    )
                    FilterChipItem(
                        label = "Resueltos",
                        count = uiState.counts.resolved,
                        selected = uiState.selectedFilter == ReportStatus.RESOLVED,
                        color = Color(0xFF10B981),
                        onClick = { viewModel.setFilter(ReportStatus.RESOLVED) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lista de reportes
            if (uiState.filteredReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.padding(24.dp).fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(shape = CircleShape, color = Color(0xFFF3E8FF), modifier = Modifier.size(64.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.padding(16.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.admin_moderation_empty_title), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(R.string.admin_moderation_empty_subtitle), fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = uiState.filteredReports, key = { it.report.id }) { reportWithOwner ->
                        val aiAnalysis = aiAnalyses[reportWithOwner.report.id]
                        ModerationReportCard(
                            reportWithOwner = reportWithOwner,
                            aiAnalysis = aiAnalysis,
                            onVerify = { viewModel.verifyReport(reportWithOwner.report.id) },
                            onResolve = { viewModel.resolveReport(reportWithOwner.report.id) },
                            onReject = { viewModel.showRejectDialog(reportWithOwner.report.id) },
                            onReanalyze = { viewModel.analyzeReport(reportWithOwner.report) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    count: Int,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val bgColor = if (selected) color else Color.White
    val textColor = if (selected) Color.White else Color(0xFF64748B)
    val border = if (selected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = border,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = textColor)
            Surface(shape = CircleShape, color = if (selected) Color.White.copy(alpha = 0.25f) else color.copy(alpha = 0.12f)) {
                Text(
                    count.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ModerationReportCard(
    reportWithOwner: ReportWithOwner,
    aiAnalysis: AiReportAnalysis?,
    onVerify: () -> Unit,
    onResolve: () -> Unit,
    onReject: () -> Unit,
    onReanalyze: () -> Unit
) {
    val report = reportWithOwner.report

    val categoryColor = when {
        report.type.contains("Seguridad", ignoreCase = true) -> Color(0xFF7C3AED)
        report.type.contains("Infraestructura", ignoreCase = true) -> Color(0xFFF59E0B)
        report.type.contains("Vial", ignoreCase = true) -> Color(0xFFF59E0B)
        report.type.contains("Alumbrado", ignoreCase = true) -> Color(0xFF3B82F6)
        else -> Color(0xFF64748B)
    }

    val statusColor = when (report.status) {
        ReportStatus.PENDING -> Color(0xFFEF4444)
        ReportStatus.IN_PROGRESS -> Color(0xFFF59E0B)
        ReportStatus.RESOLVED -> Color(0xFF10B981)
        ReportStatus.DELETED -> Color(0xFF94A3B8)
    }
    val statusLabel = when (report.status) {
        ReportStatus.PENDING -> "Pendiente"
        ReportStatus.IN_PROGRESS -> "En revisión"
        ReportStatus.RESOLVED -> "Resuelto"
        ReportStatus.DELETED -> "Rechazado"
    }

    val minutesAgo = ((System.currentTimeMillis() - report.createdAt) / 60000).toInt()
    val timeStr = when {
        minutesAgo < 1 -> stringResource(R.string.admin_moderation_time_now)
        minutesAgo < 60 -> stringResource(R.string.admin_moderation_time_minutes, minutesAgo)
        minutesAgo < 1440 -> stringResource(R.string.admin_moderation_time_hours, minutesAgo / 60)
        else -> stringResource(R.string.admin_moderation_time_days, minutesAgo / 1440)
    }

    val aiRecommendationColor = when (aiAnalysis?.recommendation) {
        "VERIFICAR" -> Color(0xFF10B981)
        "RECHAZAR" -> Color(0xFFEF4444)
        else -> Color(0xFF64748B)
    }
    val aiRecommendationBg = when (aiAnalysis?.recommendation) {
        "VERIFICAR" -> Color(0xFFD1FAE5)
        "RECHAZAR" -> Color(0xFFFEE2E2)
        else -> Color(0xFFF1F5F9)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Barra lateral de color + tipo + estado
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 0.dp, end = 16.dp, top = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.width(4.dp).height(36.dp).clip(RoundedCornerShape(topStart = 20.dp)).background(categoryColor))
                Spacer(modifier = Modifier.width(12.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = categoryColor.copy(alpha = 0.1f), modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)) {
                    Text(text = report.type, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = categoryColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.1f), modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = statusLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = report.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.admin_moderation_by_reporter, timeStr, reportWithOwner.ownerName),
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Análisis IA — solo para pendientes
                if (report.status == ReportStatus.PENDING) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                        border = BorderStroke(1.dp, Color(0xFFF3E8FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.admin_moderation_ai_label), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF7C3AED), letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                                IconButton(onClick = onReanalyze, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Re-analizar", tint = Color(0xFF7C3AED), modifier = Modifier.size(14.dp))
                                }
                            }

                            if (aiAnalysis == null || aiAnalysis.isLoading) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFF7C3AED))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analizando con Gemini IA...", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            } else {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(stringResource(R.string.admin_moderation_key_points), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                aiAnalysis.keyPoints.forEach { point ->
                                    Row(modifier = Modifier.padding(bottom = 4.dp)) {
                                        Text("  •  ", fontSize = 11.sp, color = Color(0xFF7C3AED))
                                        Text(point, fontSize = 12.sp, color = Color(0xFF475569), lineHeight = 16.sp)
                                    }
                                }
                                if (aiAnalysis.reasoning.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(aiAnalysis.reasoning, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
                                }
                                if (!aiAnalysis.error.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("⚠ Error IA: ${aiAnalysis.error}", fontSize = 10.sp, color = Color(0xFFEF4444), lineHeight = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFF3E8FF))
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(stringResource(R.string.admin_moderation_relevance_label), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B), letterSpacing = 0.5.sp, lineHeight = 12.sp)
                                        if (aiAnalysis.confidence.isNotBlank()) {
                                            Text("Confianza: ${aiAnalysis.confidence}", fontSize = 9.sp, color = Color(0xFF94A3B8))
                                        }
                                    }
                                    if (aiAnalysis.recommendation.isNotBlank()) {
                                        Surface(shape = RoundedCornerShape(8.dp), color = aiRecommendationBg, border = BorderStroke(1.dp, aiRecommendationColor.copy(alpha = 0.3f))) {
                                            Text(text = "IA: ${aiAnalysis.recommendation}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = aiRecommendationColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Botones de acción según estado
                Text(stringResource(R.string.admin_moderation_actions_label), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(10.dp))

                when (report.status) {
                    ReportStatus.PENDING -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = onVerify,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.admin_moderation_btn_verify), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            OutlinedButton(
                                onClick = onReject,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF64748B))
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.admin_moderation_btn_reject), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    ReportStatus.IN_PROGRESS -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = onResolve,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Marcar Resuelto", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            OutlinedButton(
                                onClick = onReject,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF64748B))
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.admin_moderation_btn_reject), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    ReportStatus.RESOLVED -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFD1FAE5),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                    Text("Reporte resuelto satisfactoriamente", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
                                }
                            }
                        }
                    }
                    ReportStatus.DELETED -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    Text("Reporte rechazado", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEF4444))
                                }
                                if (report.rejectionReason.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Motivo: ${report.rejectionReason}", fontSize = 11.sp, color = Color(0xFF94A3B8), lineHeight = 15.sp)
                                }
                            }
                        }
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
