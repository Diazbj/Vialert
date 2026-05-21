package com.example.myapplication.features.homeuser

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.core.navigation.ReportDetail
import com.example.myapplication.features.assistant.VirtualAssistantBottomSheet
import com.example.myapplication.features.homeuser.components.MainLayout
import com.example.myapplication.features.homeuser.components.ReportList
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@Composable
fun HomeUserScreen(
    viewModel: HomeUserViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAssistant by remember { mutableStateOf(false) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Filtro pendiente hasta obtener permiso
    var pendingFilter by remember { mutableStateOf<LocationFilter?>(null) }

    suspend fun fetchAndApplyFilter(filter: LocationFilter) {
        isFetchingLocation = true
        locationError = null
        try {
            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()
            if (location != null) {
                viewModel.setLocationFilter(filter, location.latitude, location.longitude)
            } else {
                locationError = "No se pudo obtener tu ubicación"
                viewModel.setLocationFilter(LocationFilter.ALL, null, null)
            }
        } catch (e: Exception) {
            locationError = "Error al obtener ubicación"
            viewModel.setLocationFilter(LocationFilter.ALL, null, null)
        } finally {
            isFetchingLocation = false
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val filter = pendingFilter ?: return@rememberLauncherForActivityResult
        pendingFilter = null
        if (granted) {
            scope.launch { fetchAndApplyFilter(filter) }
        } else {
            locationError = "Permiso de ubicación denegado"
        }
    }

    fun applyFilter(filter: LocationFilter) {
        locationError = null
        if (filter == LocationFilter.ALL) {
            viewModel.setLocationFilter(LocationFilter.ALL, null, null)
            return
        }
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            scope.launch { fetchAndApplyFilter(filter) }
        } else {
            pendingFilter = filter
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (showAssistant) {
        VirtualAssistantBottomSheet(onDismiss = { showAssistant = false })
    }

    MainLayout(
        navController = navController,
        showSupportFab = true,
        onSupportClick = { showAssistant = true }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 8.dp)
        ) {
            // Chips de filtro por ubicación
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LocationFilter.entries.forEach { filter ->
                    val selected = uiState.locationFilter == filter
                    Surface(
                        onClick = { applyFilter(filter) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) Color(0xFF7C3AED) else Color.White,
                        border = if (selected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = if (selected) 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Color.White else Color(0xFF64748B)
                            )
                            if (selected && isFetchingLocation && filter != LocationFilter.ALL) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(10.dp),
                                    strokeWidth = 1.5.dp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Mensaje de error de ubicación
            if (locationError != null) {
                Text(
                    text = "⚠ $locationError",
                    fontSize = 11.sp,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Info de rango activo
            if (uiState.locationFilter != LocationFilter.ALL && uiState.userLat != null) {
                Text(
                    text = "Mostrando reportes en un radio de ${uiState.locationFilter.radiusKm?.toInt()} km desde tu ubicación",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }

            ReportList(
                reports = uiState.reports,
                reportTimes = uiState.reportTimes,
                currentUserId = uiState.currentUserId,
                onImportantClick = { reportId -> viewModel.onImportantClick(reportId) },
                onShareClick = {},
                onReportClick = { report -> navController?.navigate(ReportDetail(reportId = report.id)) }
            )
        }
    }
}
