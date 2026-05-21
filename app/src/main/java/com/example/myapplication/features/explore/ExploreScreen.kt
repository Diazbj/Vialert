package com.example.myapplication.features.explore

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.components.StatusCategoryChip
import com.example.myapplication.core.navigation.ReportDetail
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.features.homeuser.components.MainLayout
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnCircleAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import kotlin.math.abs

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val filteredReports by viewModel.filteredReports.collectAsState()
    val context = LocalContext.current
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var selectedReport by remember { mutableStateOf<Report?>(null) }
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) centerMapOnMyLocation(locationClient, mapViewRef)
    }

    fun onMyLocationClick() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            centerMapOnMyLocation(locationClient, mapViewRef)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    MainLayout(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            ReportLegend()
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                MapboxReportMap(
                    reports = filteredReports,
                    onMapViewCreated = { mapViewRef = it },
                    onReportClick = { selectedReport = it }
                )

                IconButton(
                    onClick = { onMyLocationClick() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación",
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Card de preview — en ColumnScope para que AnimatedVisibility compile correctamente
            AnimatedVisibility(
                visible = selectedReport != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                selectedReport?.let { report ->
                    ReportPreviewCard(
                        report = report,
                        onDismiss = { selectedReport = null },
                        onViewDetails = { navController?.navigate(ReportDetail(reportId = report.id)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${filteredReports.size} reportes en el mapa",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ReportPreviewCard(
    report: Report,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusCategoryChip(
                        text = report.status.displayName,
                        baseColor = report.status.color
                    )
                    val cat = ReportCategory.entries.find { it.displayName == report.type }
                    StatusCategoryChip(
                        text = report.type,
                        baseColor = cat?.color ?: Color.Gray
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(16.dp), tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = report.description,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            if (report.important > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "★ ${report.important} personas marcaron esto como importante",
                    fontSize = 11.sp,
                    color = Color(0xFF7C3AED)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                Text("Ver detalles", fontSize = 13.sp)
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun centerMapOnMyLocation(
    locationClient: com.google.android.gms.location.FusedLocationProviderClient,
    mapView: MapView?
) {
    mapView ?: return
    val cts = CancellationTokenSource()
    locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
        .addOnSuccessListener { location ->
            val loc = location ?: return@addOnSuccessListener
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(loc.longitude, loc.latitude))
                    .zoom(16.0)
                    .build()
            )
        }
}

@Composable
private fun ReportLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LegendItem(color = Color(0xFFEF4444), label = "Pendiente")
        LegendItem(color = Color(0xFFF59E0B), label = "En progreso")
        LegendItem(color = Color(0xFF10B981), label = "Resuelto")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = color) {}
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun MapboxReportMap(
    reports: List<Report>,
    onMapViewCreated: (MapView) -> Unit = {},
    onReportClick: (Report) -> Unit = {}
) {
    val defaultLat = 4.5339
    val defaultLng = -75.6811
    val defaultZoom = 13.0

    val validReports = reports.filter { it.location.latitude != 0.0 || it.location.longitude != 0.0 }
    val reportsRef = rememberUpdatedState(validReports)
    val onClickRef = rememberUpdatedState(onReportClick)

    val centerLat = if (validReports.isNotEmpty()) validReports.map { it.location.latitude }.average() else defaultLat
    val centerLng = if (validReports.isNotEmpty()) validReports.map { it.location.longitude }.average() else defaultLng

    AndroidView(
        factory = { ctx ->
            val mapView = MapView(ctx)
            onMapViewCreated(mapView)
            mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) { _ ->
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(centerLng, centerLat))
                        .zoom(defaultZoom)
                        .build()
                )
                val manager = mapView.annotations.createCircleAnnotationManager()

                manager.addClickListener(OnCircleAnnotationClickListener { annotation ->
                    val clicked = reportsRef.value.find { report ->
                        abs(report.location.latitude - annotation.point.latitude()) < 0.0001 &&
                        abs(report.location.longitude - annotation.point.longitude()) < 0.0001
                    }
                    clicked?.let { onClickRef.value(it) }
                    true
                })

                mapView.tag = manager
                addMarkersToManager(manager, validReports)
            }
            mapView
        },
        update = { mapView ->
            val manager = mapView.tag as? CircleAnnotationManager ?: return@AndroidView
            manager.deleteAll()
            addMarkersToManager(manager, reportsRef.value)
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun addMarkersToManager(manager: CircleAnnotationManager, reports: List<Report>) {
    reports.forEach { report ->
        val color = when (report.status) {
            ReportStatus.PENDING -> "#EF4444"
            ReportStatus.IN_PROGRESS -> "#F59E0B"
            ReportStatus.RESOLVED -> "#10B981"
            ReportStatus.DELETED -> "#94A3B8"
        }
        manager.create(
            CircleAnnotationOptions()
                .withPoint(Point.fromLngLat(report.location.longitude, report.location.latitude))
                .withCircleRadius(10.0)
                .withCircleColor(color)
                .withCircleStrokeColor("#FFFFFF")
                .withCircleStrokeWidth(2.5)
        )
    }
}
