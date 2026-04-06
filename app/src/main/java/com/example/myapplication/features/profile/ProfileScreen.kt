package com.example.myapplication.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.features.homeuser.components.MainLayout

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController? = null
) {
    MainLayout(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi perfil",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { viewModel.toggleEditing() }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                }
            }

            OutlinedTextField(
                value = viewModel.name.value,
                onValueChange = { viewModel.name.onChange(it) },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = viewModel.name.error != null,
                supportingText = viewModel.name.error?.let { error -> { Text(error) } },
                enabled = viewModel.isEditing.value,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = viewModel.email.error != null,
                supportingText = viewModel.email.error?.let { error -> { Text(error) } },
                enabled = viewModel.isEditing.value,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.phone.value,
                onValueChange = { viewModel.phone.onChange(it) },
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                isError = viewModel.phone.error != null,
                supportingText = viewModel.phone.error?.let { error -> { Text(error) } },
                enabled = viewModel.isEditing.value,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (viewModel.isEditing.value) {
                Button(
                    onClick = { viewModel.onSave() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.isFormValid
                ) {
                    Text("Guardar cambios")
                }
            }
        }
    }
}
