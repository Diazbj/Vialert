package com.example.myapplication.features.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDataAnalysisScreen(navController: NavController? = null) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_data_topbar_title), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF7C3AED)) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.admin_data_back), tint = Color(0xFF475569))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Box {
                            Icon(Icons.Default.Notifications, contentDescription = stringResource(R.string.admin_data_notifications), tint = Color(0xFF475569))
                            Surface(
                                shape = CircleShape,
                                color = Color.Red,
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                            ) {}
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAFC))
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
            // Tabs Simples
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.admin_data_tab_weekly), fontSize = 12.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.SemiBold)
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.admin_data_tab_monthly), fontSize = 12.sp, color = Color(0xFF64748B))
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.admin_data_tab_yearly), fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta Principal Púrpura
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7C3AED)),
                modifier = Modifier.fillMaxWidth().height(140.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(stringResource(R.string.admin_data_total_reports), color = Color(0xFFE2E8F0), fontSize = 11.sp)
                        Text("1,284", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF9333EA),
                        ) {
                            Text(stringResource(R.string.admin_data_vs_previous_month), color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF9333EA),
                        modifier = Modifier.align(Alignment.TopEnd).size(36.dp)
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas Inferiores
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AnalyticsCard(stringResource(R.string.admin_data_resolved), "856", "+12%", true, Modifier.weight(1f))
                AnalyticsCard(stringResource(R.string.admin_data_critical), "12%", "↓2%", false, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Desglose por Categoría
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PieChart, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.admin_data_category_breakdown), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CategoryProgressRow(stringResource(R.string.admin_data_cat_security), "45%", 0.45f)
                    CategoryProgressRow(stringResource(R.string.admin_data_cat_infrastructure), "30%", 0.30f)
                    CategoryProgressRow(stringResource(R.string.admin_data_cat_health), "15%", 0.15f)
                    CategoryProgressRow(stringResource(R.string.admin_data_cat_others), "10%", 0.10f)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Zonas con más reportes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.admin_data_zones_title), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.admin_map_placeholder),
                        contentDescription = stringResource(R.string.admin_data_map_description),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ZoneReportItem("1", "Centro Histórico", "Alta densidad", "342 reportes")
                        ZoneReportItem("2", "Zona Norte", "En crecimiento", "215 reportes")
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String, percentage: String, isPositive: Boolean, modifier: Modifier) {
    val percentageColor = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontSize = 11.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text(percentage, fontSize = 12.sp, color = percentageColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CategoryProgressRow(title: String, percentageStr: String, progress: Float) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 12.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
            Text(percentageStr, fontSize = 12.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = Color(0xFF7C3AED),
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
fun ZoneReportItem(rank: String, title: String, subtitle: String, countStr: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFD700),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(rank, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text(subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
            }
            Text(countStr, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
        }
    }
}
