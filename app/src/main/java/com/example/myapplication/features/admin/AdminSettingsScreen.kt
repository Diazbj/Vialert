package com.example.myapplication.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(navController: NavController? = null) {

    // Estados de los toggles
    var notifNewReports by remember { mutableStateOf(true) }
    var notifUserActivity by remember { mutableStateOf(true) }
    var autoModerationAI by remember { mutableStateOf(true) }
    var showDeletedReports by remember { mutableStateOf(false) }
    var strictValidation by remember { mutableStateOf(true) }
    var maintenanceMode by remember { mutableStateOf(false) }

    var showMaintenanceDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración del Sistema", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1E293B)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Notificaciones ──────────────────────────────────
            SettingsSection(title = "Notificaciones", icon = Icons.Default.Notifications, iconColor = Color(0xFF7C3AED)) {
                SettingsToggleRow(
                    label = "Nuevos reportes ciudadanos",
                    description = "Recibir alerta cuando se crea un reporte",
                    checked = notifNewReports,
                    onCheckedChange = { notifNewReports = it }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingsToggleRow(
                    label = "Actividad de usuarios",
                    description = "Cambios de rol, registros y bajas",
                    checked = notifUserActivity,
                    onCheckedChange = { notifUserActivity = it }
                )
            }

            // ── Moderación ──────────────────────────────────────
            SettingsSection(title = "Moderación", icon = Icons.Default.AutoAwesome, iconColor = Color(0xFF9333EA)) {
                SettingsToggleRow(
                    label = "Análisis IA automático",
                    description = "Gemini analiza cada reporte pendiente al llegar",
                    checked = autoModerationAI,
                    onCheckedChange = { autoModerationAI = it }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingsToggleRow(
                    label = "Validación estricta",
                    description = "Requiere coordenadas válidas para publicar un reporte",
                    checked = strictValidation,
                    onCheckedChange = { strictValidation = it }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingsToggleRow(
                    label = "Mostrar reportes eliminados",
                    description = "Incluir reportes rechazados en la vista de moderación",
                    checked = showDeletedReports,
                    onCheckedChange = { showDeletedReports = it }
                )
            }

            // ── Sistema ─────────────────────────────────────────
            SettingsSection(title = "Sistema", icon = Icons.Default.Dns, iconColor = Color(0xFF3B82F6)) {
                SettingsToggleRow(
                    label = "Modo mantenimiento",
                    description = "Bloquea el acceso de usuarios mientras se realizan cambios",
                    checked = maintenanceMode,
                    onCheckedChange = {
                        if (it) showMaintenanceDialog = true else maintenanceMode = false
                    },
                    accentColor = Color(0xFFEF4444)
                )
            }

            // ── Información de la app ───────────────────────────
            SettingsSection(title = "Acerca de Vialert", icon = Icons.Default.Info, iconColor = Color(0xFF10B981)) {
                SettingsInfoRow("Versión de la app", "1.0.0 (build 24)")
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingsInfoRow("Base de datos", "Firebase Firestore")
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingsInfoRow("Almacenamiento", "Cloudinary CDN")
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingsInfoRow("IA", "Google Gemini 2.0 Flash")
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingsInfoRow("Mapas", "Mapbox Maps SDK v11")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Diálogo de confirmación para modo mantenimiento
    if (showMaintenanceDialog) {
        AlertDialog(
            onDismissRequest = { showMaintenanceDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Activar modo mantenimiento", fontWeight = FontWeight.Bold) },
            text = { Text("Los usuarios no podrán acceder a la aplicación mientras este modo esté activo. ¿Deseas continuar?", fontSize = 14.sp, color = Color(0xFF475569)) },
            confirmButton = {
                Button(
                    onClick = { maintenanceMode = true; showMaintenanceDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) { Text("Activar") }
            },
            dismissButton = {
                TextButton(onClick = { showMaintenanceDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.1f), modifier = Modifier.size(28.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(15.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), letterSpacing = 0.3.sp)
        }
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color = Color(0xFF7C3AED)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(2.dp))
            Text(description, fontSize = 11.sp, color = Color(0xFF94A3B8), lineHeight = 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color(0xFF1E293B))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF7C3AED))
    }
}
