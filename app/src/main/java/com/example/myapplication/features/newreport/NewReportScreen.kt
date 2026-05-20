package com.example.myapplication.features.newreport

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.core.components.ResultDialog
import com.example.myapplication.core.theme.VialertPurple
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.domain.model.ReportCategory
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
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    viewModel: NewReportViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val submitResult by viewModel.submitResult.collectAsState()
    var isCategoryExpanded by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) viewModel.onImageSelected(uri)
    }

    ResultDialog(
        result = submitResult,
        onDismiss = {
            if (submitResult is RequestResult.Success) {
                viewModel.resetForm()
                navController?.popBackStack()
            } else {
                viewModel.clearResult()
            }
        }
    )

    MainLayout(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (uiState.isEditMode) stringResource(R.string.new_report_title_edit) else stringResource(R.string.new_report_title_create),
                style = MaterialTheme.typography.headlineSmall,
                color = VialertPurple,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Título
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text(stringResource(R.string.new_report_label_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VialertPurple,
                    focusedLabelColor = VialertPurple
                )
            )

            // Categoría
            ExposedDropdownMenuBox(
                expanded = isCategoryExpanded,
                onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.category?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.new_report_label_category)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VialertPurple,
                        focusedLabelColor = VialertPurple
                    )
                )

                ExposedDropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false },
                    containerColor = Color.White
                ) {
                    ReportCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.displayName) },
                            onClick = {
                                viewModel.updateCategory(category)
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Descripción
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text(stringResource(R.string.new_report_label_description)) },
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VialertPurple,
                    focusedLabelColor = VialertPurple
                )
            )

            // SECCIÓN UBICACIÓN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = VialertPurple, modifier = Modifier.size(18.dp))
                        Text(
                            text = stringResource(R.string.new_report_location_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.DarkGray
                        )
                    }

                    Text(
                        text = "Toca el mapa para marcar la ubicación del problema",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    LocationPickerMap(
                        selectedLat = uiState.location?.latitude,
                        selectedLng = uiState.location?.longitude,
                        onLocationSelected = { lat, lng -> viewModel.onLocationSelected(lat, lng) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    if (uiState.location != null) {
                        Text(
                            text = uiState.address,
                            fontSize = 11.sp,
                            color = VialertPurple,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // SECCIÓN FOTOS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = VialertPurple, modifier = Modifier.size(18.dp))
                        Text(
                            text = stringResource(R.string.new_report_photos_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.DarkGray
                        )
                    }

                    if (uiState.selectedImageUri != null) {
                        AsyncImage(
                            model = uiState.selectedImageUri,
                            contentDescription = "Foto seleccionada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, VialertPurple),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VialertPurple)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (uiState.selectedImageUri != null) "Cambiar foto" else "Seleccionar foto")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Acción
            Button(
                onClick = viewModel::onSubmit,
                enabled = uiState.isFormValid && submitResult !is RequestResult.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VialertPurple,
                    contentColor = Color.White
                )
            ) {
                if (submitResult is RequestResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(if (uiState.isEditMode) stringResource(R.string.new_report_btn_update) else stringResource(R.string.new_report_btn_publish))
                }
            }

            // Botón Cancelar
            OutlinedButton(
                onClick = { navController?.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VialertPurple
                ),
                border = BorderStroke(1.dp, VialertPurple)
            ) {
                Text(stringResource(R.string.new_report_btn_cancel))
            }
        }
    }
}

@Composable
private fun LocationPickerMap(
    selectedLat: Double?,
    selectedLng: Double?,
    onLocationSelected: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val defaultLat = 4.5339
    val defaultLng = -75.6811
    val onLocationSelectedState by rememberUpdatedState(onLocationSelected)
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) selectMyLocation(locationClient, mapViewRef, onLocationSelectedState)
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                val mapView = MapView(ctx)
                mapViewRef = mapView
                mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) { _ ->
                    mapView.mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(selectedLng ?: defaultLng, selectedLat ?: defaultLat))
                            .zoom(13.0)
                            .build()
                    )
                    val manager = mapView.annotations.createCircleAnnotationManager()
                    mapView.tag = manager
                    if (selectedLat != null && selectedLng != null) {
                        manager.create(buildMarkerOptions(selectedLng, selectedLat))
                    }
                }
                mapView.gestures.addOnMapClickListener { point ->
                    onLocationSelectedState(point.latitude(), point.longitude())
                    true
                }
                mapView
            },
            update = { mapView ->
                val manager = mapView.tag as? CircleAnnotationManager ?: return@AndroidView
                manager.deleteAll()
                if (selectedLat != null && selectedLng != null) {
                    manager.create(buildMarkerOptions(selectedLng, selectedLat))
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    selectMyLocation(locationClient, mapViewRef, onLocationSelectedState)
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Mi ubicación",
                tint = VialertPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun selectMyLocation(
    locationClient: com.google.android.gms.location.FusedLocationProviderClient,
    mapView: MapView?,
    onLocationSelected: (Double, Double) -> Unit
) {
    mapView ?: return
    val cts = CancellationTokenSource()
    locationClient
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
        .addOnSuccessListener { location ->
            val loc = location ?: return@addOnSuccessListener
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(loc.longitude, loc.latitude))
                    .zoom(16.0)
                    .build()
            )
            onLocationSelected(loc.latitude, loc.longitude)
        }
}

private fun buildMarkerOptions(lng: Double, lat: Double): CircleAnnotationOptions =
    CircleAnnotationOptions()
        .withPoint(Point.fromLngLat(lng, lat))
        .withCircleRadius(10.0)
        .withCircleColor("#7C3AED")
        .withCircleStrokeColor("#FFFFFF")
        .withCircleStrokeWidth(2.5)
