package com.example.myapplication.features.newreport

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
                text = "Crear Reporte",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Título (Corresponde a title en Report)
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Título del reporte") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Categoría (Corresponde a type en Report)
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
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false }
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

            // Descripción (Corresponde a description en Report)
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Descripción") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            // SECCIÓN UBICACIÓN (Implementación futura de location)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ubicación (Próximamente)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "Mapa no disponible",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // SECCIÓN FOTOS (Implementación futura de photoUrl)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Fotos (Próximamente)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "La carga de imágenes será habilitada en futuras versiones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Publicar
            Button(
                onClick = viewModel::createReport,
                enabled = uiState.isFormValid && submitResult !is RequestResult.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                if (submitResult is RequestResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("PUBLICAR REPORTE")
                }
            }
        }
    }
}
