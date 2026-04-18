package com.example.myapplication.features.newreport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.core.components.ResultDialog
import com.example.myapplication.core.theme.VialertPurple
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.domain.model.ReportCategory
import com.example.myapplication.features.homeuser.components.MainLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    viewModel: NewReportViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val submitResult by viewModel.submitResult.collectAsState()
    var isCategoryExpanded by remember { mutableStateOf(false) }

    // Manejo de feedback mediante ResultDialog
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
                text = if (uiState.isEditMode) "Editar Reporte" else "Crear Reporte",
                style = MaterialTheme.typography.headlineSmall,
                color = VialertPurple,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Título
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Título del reporte") },
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
                    label = { Text("Categoría") },
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
                label = { Text("Descripción") },
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
                    Text(
                        text = "Ubicación (Próximamente)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.DarkGray
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                color = Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "Mapa no disponible",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
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
                    Text(
                        text = "Fotos (Próximamente)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.DarkGray
                    )
                    
                    Text(
                        text = "La carga de imágenes será habilitada en futuras versiones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
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
                    Text(if (uiState.isEditMode) "ACTUALIZAR REPORTE" else "PUBLICAR REPORTE")
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
                Text("CANCELAR")
            }
        }
    }
}
