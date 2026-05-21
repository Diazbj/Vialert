package com.example.myapplication.features.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.theme.VialertPurple
import com.example.myapplication.core.theme.VialertPurpleLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    navController: NavController?
) {
    val state by viewModel.uiState.collectAsState()

    val maxBarHeight = 160.dp
    val maxWeekCount = state.weeklyData.maxOrNull()?.takeIf { it > 0 } ?: 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.statistics_topbar_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.statistics_back), tint = Color(0xFF1E293B))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.statistics_options), tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // --- Gráfica mensual ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(stringResource(R.string.statistics_reports_by_month), fontSize = 12.sp, color = Color(0xFF64748B))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            state.totalReports.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                        if (state.totalReports > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            val resolvedPct = (state.resolvedReports * 100 / state.totalReports)
                            Text(
                                "$resolvedPct% resueltos",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                    Text(stringResource(R.string.statistics_total_accumulated), fontSize = 11.sp, color = Color(0xFF94A3B8))

                    Spacer(modifier = Modifier.height(32.dp))

                    val weekLabels = listOf(
                        stringResource(R.string.statistics_week_1),
                        stringResource(R.string.statistics_week_2),
                        stringResource(R.string.statistics_week_3),
                        stringResource(R.string.statistics_week_4)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        state.weeklyData.forEachIndexed { index, count ->
                            val barHeight: Dp = if (count == 0) 6.dp
                            else (maxBarHeight * count / maxWeekCount)
                            ChartBar(
                                height = barHeight,
                                label = weekLabels[index],
                                count = count
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.statistics_activity_summary), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            ActivitySummaryItem(
                icon = Icons.Default.Assignment,
                iconColor = VialertPurple,
                title = stringResource(R.string.statistics_total_reports),
                value = state.totalReports.toString(),
                badgeText = if (state.totalReports > 0) "este mes" else "sin datos",
                badgeColor = Color(0xFF6A1B9A),
                badgeBg = Color(0xFFF3E8FF)
            )

            ActivitySummaryItem(
                icon = Icons.Default.CheckCircle,
                iconColor = Color(0xFF10B981),
                title = stringResource(R.string.statistics_resolved),
                value = state.resolvedReports.toString(),
                badgeText = if (state.totalReports > 0)
                    "${state.resolvedReports * 100 / state.totalReports}%"
                else "0%",
                badgeColor = Color(0xFF10B981),
                badgeBg = Color(0xFFD1FAE5)
            )

            ActivitySummaryItem(
                icon = Icons.Default.HourglassEmpty,
                iconColor = Color(0xFF3B82F6),
                title = stringResource(R.string.statistics_in_progress),
                value = state.inProgressReports.toString(),
                badgeText = if (state.totalReports > 0)
                    "${state.inProgressReports * 100 / state.totalReports}%"
                else "0%",
                badgeColor = Color(0xFF3B82F6),
                badgeBg = Color(0xFFDBEAFE)
            )

            ActivitySummaryItem(
                icon = Icons.Default.Pending,
                iconColor = Color(0xFFF59E0B),
                title = "Pendientes",
                value = state.pendingReports.toString(),
                badgeText = if (state.totalReports > 0)
                    "${state.pendingReports * 100 / state.totalReports}%"
                else "0%",
                badgeColor = Color(0xFFF59E0B),
                badgeBg = Color(0xFFFEF3C7)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.statistics_by_category), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            if (state.categoryData.isEmpty()) {
                Text("Sin datos de categorías aún", fontSize = 13.sp, color = Color(0xFF94A3B8))
            } else {
                val categoryColors = listOf(VialertPurple, VialertPurpleLight, Color(0xFF10B981), Color(0xFFF59E0B))
                state.categoryData.forEachIndexed { index, (name, fraction) ->
                    val pct = (fraction * 100).toInt()
                    CategoryProgress(
                        title = name,
                        percentage = "$pct%",
                        progress = fraction,
                        color = categoryColors[index % categoryColors.size]
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ChartBar(height: Dp, label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        if (count > 0) {
            Text(count.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(if (count > 0) VialertPurple else Color(0xFFE2E8F0))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
    }
}

@Composable
fun ActivitySummaryItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    badgeText: String,
    badgeColor: Color,
    badgeBg: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, color = Color(0xFF64748B))
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = badgeBg) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryProgress(title: String, percentage: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 14.sp, color = Color(0xFF475569))
            Text(percentage, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}
