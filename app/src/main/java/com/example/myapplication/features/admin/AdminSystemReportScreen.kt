package com.example.myapplication.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSystemReportScreen(
    viewModel: AdminSystemReportViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val state by viewModel.uiState.collectAsState()
    val generatedAt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte del Sistema", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF7C3AED))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7C3AED))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF7C3AED)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(52.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Reporte General del Sistema", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Generado: $generatedAt", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                                Surface(shape = CircleShape, color = Color(0xFF10B981), modifier = Modifier.size(8.dp)) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sistema operativo", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                            }
                        }
                    }
                }
            }

            // ── Usuarios ────────────────────────────────────────
            item {
                SectionTitle("Usuarios", Icons.Default.Group, Color(0xFF6366F1))
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Total usuarios", state.totalUsers.toString(), Color(0xFF6366F1), Icons.Default.People, Modifier.weight(1f))
                    MetricCard("Admins", state.adminUsers.toString(), Color(0xFF9333EA), Icons.Default.AdminPanelSettings, Modifier.weight(1f))
                    MetricCard("Activos (mes)", state.activeUsersThisMonth.toString(), Color(0xFF10B981), Icons.Default.TrendingUp, Modifier.weight(1f))
                }
            }

            // ── Reportes generales ───────────────────────────────
            item {
                SectionTitle("Reportes Ciudadanos", Icons.Default.Description, Color(0xFF7C3AED))
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Total activos", state.totalReports.toString(), Color(0xFF7C3AED), Icons.Default.Feed, Modifier.weight(1f))
                    MetricCard("Esta semana", state.reportsThisWeek.toString(), Color(0xFF3B82F6), Icons.Default.DateRange, Modifier.weight(1f))
                    MetricCard("Este mes", state.reportsThisMonth.toString(), Color(0xFF8B5CF6), Icons.Default.CalendarMonth, Modifier.weight(1f))
                }
            }

            // ── Estado de reportes ───────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Estado de Reportes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        StatusProgressRow("Pendientes", state.pendingReports, state.totalReports, Color(0xFFEF4444))
                        StatusProgressRow("En revisión", state.inProgressReports, state.totalReports, Color(0xFFF59E0B))
                        StatusProgressRow("Resueltos", state.resolvedReports, state.totalReports, Color(0xFF10B981))
                        StatusProgressRow("Rechazados", state.deletedReports, state.totalReports + state.deletedReports, Color(0xFF94A3B8))

                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tasa de resolución", fontSize = 13.sp, color = Color(0xFF475569))
                            Text("${state.resolutionRate}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        LinearProgressIndicator(
                            progress = { state.resolutionRate / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFFF1F5F9)
                        )
                    }
                }
            }

            // ── Categorías más activas ───────────────────────────
            item {
                SectionTitle("Categorías más activas", Icons.Default.Category, Color(0xFFF59E0B))
            }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (state.topCategories.isEmpty()) {
                            Text("Sin datos de categorías", fontSize = 13.sp, color = Color(0xFF94A3B8))
                        } else {
                            val catColors = listOf(Color(0xFF7C3AED), Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B))
                            state.topCategories.forEachIndexed { i, (name, count) ->
                                StatusProgressRow(name, count, state.totalReports, catColors[i % catColors.size])
                            }
                        }
                    }
                }
            }

            // ── Top reporteros ───────────────────────────────────
            item {
                SectionTitle("Usuarios más activos", Icons.Default.EmojiEvents, Color(0xFF10B981))
            }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val medals = listOf(Color(0xFFFFD700), Color(0xFFC0C0C0), Color(0xFFCD7F32))
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (state.topReporters.isEmpty()) {
                            Text("Sin datos de reporteros", fontSize = 13.sp, color = Color(0xFF94A3B8))
                        } else {
                            state.topReporters.forEachIndexed { i, (name, count) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = CircleShape, color = medals[i].copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("${i + 1}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = medals[i])
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(name, fontSize = 14.sp, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
                                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3E8FF)) {
                                        Text("$count reportes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Estado de servicios ──────────────────────────────
            item {
                SectionTitle("Estado de Servicios", Icons.Default.Cloud, Color(0xFF3B82F6))
            }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ServiceStatusRow("Firebase Firestore", "Operativo", true)
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ServiceStatusRow("Cloudinary CDN", "Operativo", true)
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ServiceStatusRow("Gemini IA", "Operativo", true)
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ServiceStatusRow("Mapbox Maps", "Operativo", true)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionTitle(title: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(28.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
    }
}

@Composable
private fun MetricCard(label: String, value: String, color: Color, icon: ImageVector, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatusProgressRow(label: String, count: Int, total: Int, color: Color) {
    val fraction = if (total > 0) count.toFloat() / total else 0f
    val pct = (fraction * 100).toInt()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, color = Color(0xFF475569))
            Text("$count  ($pct%)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
private fun ServiceStatusRow(name: String, status: String, isOnline: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444), modifier = Modifier.size(8.dp)) {}
        Spacer(modifier = Modifier.width(10.dp))
        Text(name, fontSize = 13.sp, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
        Text(status, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444))
    }
}
