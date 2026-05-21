package com.example.myapplication.features.admin

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.navigation.Notifications
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDataAnalysisScreen(
    viewModel: AdminDataAnalysisViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val state by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    fun flyToZone(zone: ZoneData) {
        mapViewRef?.mapboxMap?.flyTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(zone.lng, zone.lat))
                .zoom(15.0)
                .build(),
            MapAnimationOptions.mapAnimationOptions { duration(1200L) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.admin_data_topbar_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF7C3AED)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.admin_data_back), tint = Color(0xFF475569))
                    }
                },
                actions = {
                    IconButton(onClick = { navController?.navigate(Notifications) }) {
                        Box {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF475569))
                            Surface(
                                shape = CircleShape,
                                color = Color.Red,
                                modifier = Modifier.size(8.dp).align(Alignment.TopEnd)
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
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    TimeTab.WEEKLY to stringResource(R.string.admin_data_tab_weekly),
                    TimeTab.MONTHLY to stringResource(R.string.admin_data_tab_monthly),
                    TimeTab.YEARLY to stringResource(R.string.admin_data_tab_yearly)
                ).forEach { (tab, label) ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .then(
                                if (!isSelected) Modifier
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = { viewModel.selectTab(tab) },
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                label,
                                fontSize = 12.sp,
                                color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF64748B),
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta Principal Púrpura
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7C3AED)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(stringResource(R.string.admin_data_total_reports), color = Color(0xFFE2E8F0), fontSize = 11.sp)
                        Text(
                            state.total.toString(),
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF9333EA)) {
                            Text(
                                if (state.total > 0)
                                    "${state.resolved * 100 / state.total}% resueltos"
                                else
                                    stringResource(R.string.admin_data_vs_previous_month),
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF9333EA),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas Resueltos / Críticos
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AnalyticsCard(
                    title = stringResource(R.string.admin_data_resolved),
                    value = state.resolved.toString(),
                    percentage = state.resolvedTrend,
                    isPositive = !state.resolvedTrend.startsWith("-"),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsCard(
                    title = stringResource(R.string.admin_data_critical),
                    value = "${state.criticalPct}%",
                    percentage = state.criticalTrend,
                    isPositive = state.criticalTrend.startsWith("-"),
                    modifier = Modifier.weight(1f)
                )
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
                    if (state.categoryData.isEmpty()) {
                        Text("Sin datos para el período seleccionado", fontSize = 13.sp, color = Color(0xFF94A3B8))
                    } else {
                        state.categoryData.forEach { (type, fraction) ->
                            val pct = (fraction * 100).toInt()
                            CategoryProgressRow(title = type, percentageStr = "$pct%", progress = fraction)
                        }
                    }
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
                    // Mapa real
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    ) {
                        if (state.periodReports.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
                                Text("Sin reportes en este período", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            }
                        } else {
                            AdminHeatmapView(
                                reports = state.periodReports,
                                onMapViewCreated = { mapViewRef = it }
                            )
                        }
                    }

                    // Lista de zonas
                    if (state.topZones.isNotEmpty()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            state.topZones.forEach { zone ->
                                ZoneReportItem(
                                    rank = "${zone.rank}",
                                    title = "Zona ${zone.rank}",
                                    subtitle = "Lat ${String.format("%.2f", zone.lat)}, Lng ${String.format("%.2f", zone.lng)}",
                                    countStr = "${zone.count} reportes",
                                    onClick = { flyToZone(zone) }
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Sin zonas identificadas", fontSize = 13.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AdminHeatmapView(
    reports: List<Report>,
    onMapViewCreated: (MapView) -> Unit = {}
) {
    val validReports = remember(reports) {
        reports.filter { it.location.latitude != 0.0 || it.location.longitude != 0.0 }
    }
    val centerLat = if (validReports.isNotEmpty()) validReports.map { it.location.latitude }.average() else 4.5339
    val centerLng = if (validReports.isNotEmpty()) validReports.map { it.location.longitude }.average() else -75.6811

    AndroidView(
        factory = { ctx ->
            val mapView = MapView(ctx)
            onMapViewCreated(mapView)
            mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) { _ ->
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(centerLng, centerLat))
                        .zoom(12.0)
                        .build()
                )
                val manager = mapView.annotations.createCircleAnnotationManager()
                validReports.forEach { report ->
                    val color = when (report.status) {
                        ReportStatus.PENDING -> "#EF4444"
                        ReportStatus.IN_PROGRESS -> "#F59E0B"
                        ReportStatus.RESOLVED -> "#10B981"
                        ReportStatus.DELETED -> "#94A3B8"
                    }
                    manager.create(
                        CircleAnnotationOptions()
                            .withPoint(Point.fromLngLat(report.location.longitude, report.location.latitude))
                            .withCircleRadius(8.0)
                            .withCircleColor(color)
                            .withCircleStrokeColor("#FFFFFF")
                            .withCircleStrokeWidth(1.5)
                    )
                }
            }
            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
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
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
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
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = Color(0xFF7C3AED),
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
fun ZoneReportItem(rank: String, title: String, subtitle: String, countStr: String, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
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
