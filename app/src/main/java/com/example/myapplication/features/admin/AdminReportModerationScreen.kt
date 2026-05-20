package com.example.myapplication.features.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Refresh
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportModerationScreen(
    viewModel: AdminReportModerationViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val aiAnalyses by viewModel.aiAnalyses.collectAsState()

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                stringResource(R.string.admin_moderation_header),
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.admin_moderation_subheader),
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.pendingReports.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
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
            } else {
                uiState.pendingReports.forEach { reportWithOwner ->
                    var visible by remember { mutableStateOf(true) }
                    val aiAnalysis = aiAnalyses[reportWithOwner.report.id]

                    AnimatedVisibility(visible = visible, exit = shrinkVertically() + fadeOut()) {
                        ModerationReportCard(
                            reportWithOwner = reportWithOwner,
                            aiAnalysis = aiAnalysis,
                            onVerify = {
                                viewModel.verifyReport(reportWithOwner.report.id)
                                visible = false
                            },
                            onReject = {
                                viewModel.rejectReport(reportWithOwner.report.id)
                                visible = false
                            },
                            onReanalyze = { viewModel.analyzeReport(reportWithOwner.report) }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ModerationReportCard(
    reportWithOwner: ReportWithOwner,
    aiAnalysis: AiReportAnalysis?,
    onVerify: () -> Unit,
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

    val minutesAgo = ((System.currentTimeMillis() - report.createdAt) / 60000).toInt()
    val timeStr = when {
        minutesAgo < 1 -> stringResource(R.string.admin_moderation_time_now)
        minutesAgo < 60 -> stringResource(R.string.admin_moderation_time_minutes, minutesAgo)
        minutesAgo < 1440 -> stringResource(R.string.admin_moderation_time_hours, minutesAgo / 60)
        else -> stringResource(R.string.admin_moderation_time_days, minutesAgo / 1440)
    }

    // Color de recomendación IA
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
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.width(4.dp).height(28.dp).clip(RoundedCornerShape(topStart = 20.dp)).background(categoryColor))
                Spacer(modifier = Modifier.width(12.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = categoryColor.copy(alpha = 0.1f), modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = report.type, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = categoryColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = report.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(R.string.admin_moderation_by_reporter, timeStr, reportWithOwner.ownerName), fontSize = 11.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(16.dp))

                // Tarjeta de análisis IA
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
                                        Text(
                                            text = "IA: ${aiAnalysis.recommendation}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = aiRecommendationColor,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.admin_moderation_actions_label), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(10.dp))

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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
