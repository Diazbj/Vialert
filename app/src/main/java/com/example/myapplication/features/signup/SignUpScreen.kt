package com.example.myapplication.features.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.core.components.ResultDialog
import com.example.myapplication.core.components.VialertPasswordField
import com.example.myapplication.core.components.VialertTextField
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.domain.model.Gender

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavController? = null,
    onNavigateToBack: () -> Unit
) {
    val registerResult by viewModel.registerResult.collectAsState()
    var isGenderMenuExpanded by remember { mutableStateOf(false) }

    // El diálogo se muestra basándose directamente en el estado del registerResult
    ResultDialog(
        result = registerResult,
        onDismiss = {
            if (registerResult is RequestResult.Success) {
                viewModel.resetForm()
                //onNavigateToBack()
            } else {
                viewModel.clearResult()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF6A1B9A),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Únete a la comunidad de vigilancia ciudadana y haz tu barrio un lugar más seguro",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VialertTextField(
                value = viewModel.firstName.value,
                onValueChange = { viewModel.firstName.onChange(it) },
                label = "Nombre",
                placeholder = "Ej: Juan",
                isError = viewModel.firstName.error != null,
                supportingText = viewModel.firstName.error,
                modifier = Modifier.weight(1f)
            )
            VialertTextField(
                value = viewModel.lastName.value,
                onValueChange = { viewModel.lastName.onChange(it) },
                label = "Apellido",
                placeholder = "Ej: Pérez",
                isError = viewModel.lastName.error != null,
                supportingText = viewModel.lastName.error,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        VialertTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.onChange(it) },
            label = "Correo electrónico",
            placeholder = "correo@ejemplo.com",
            isError = viewModel.email.error != null,
            supportingText = viewModel.email.error
        )

        Spacer(modifier = Modifier.height(15.dp))

        VialertTextField(
            value = viewModel.userName.value,
            onValueChange = { viewModel.userName.onChange(it) },
            label = "Nombre de usuario",
            placeholder = "@usuario123",
            isError = viewModel.userName.error != null,
            supportingText = viewModel.userName.error
        )

        Spacer(modifier = Modifier.height(15.dp))

        VialertPasswordField(
            value = viewModel.password.value,
            onValueChange = { viewModel.onPasswordChanged(it) },
            isError = viewModel.password.error != null,
            supportingText = viewModel.password.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(4) { index ->
                    val color = if (index < viewModel.passwordStrengthLevel.value) Color(0xFF6A1B9A) else Color.LightGray
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(color, RoundedCornerShape(3.dp))
                    )
                }
            }
            Text(
                text = "Seguridad: ${viewModel.passwordStrengthText.value}",
                fontSize = 12.sp,
                color = if (viewModel.passwordStrengthLevel.value == 0) Color.Gray else Color(0xFF6A1B9A),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = isGenderMenuExpanded,
                onExpandedChange = { isGenderMenuExpanded = !isGenderMenuExpanded },
                modifier = Modifier.weight(1f)
            ) {
                VialertTextField(
                    value = viewModel.selectedGender.value?.let { gender ->
                        gender.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " ")
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = "Género",
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderMenuExpanded) },
                    isError = viewModel.gender.error != null,
                    supportingText = viewModel.gender.error,
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isGenderMenuExpanded,
                    onDismissRequest = { isGenderMenuExpanded = false }
                ) {
                    Gender.entries.forEach { gender ->
                        val label = gender.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " ")
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.onGenderSelected(gender)
                                isGenderMenuExpanded = false
                            }
                        )
                    }
                }
            }

            VialertTextField(
                value = viewModel.birthDate.value,
                onValueChange = { viewModel.birthDate.onChange(it) },
                label = "F. Nacimiento",
                placeholder = "DD/MM/AAAA",
                isError = viewModel.birthDate.error != null,
                supportingText = viewModel.birthDate.error,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = viewModel.hasAcceptedTerms.value,
                onCheckedChange = { viewModel.onTermsAcceptanceChange(it) }
            )
            Text(
                text = "Acepto los términos y condiciones y la politica de privacidad de Vialert",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.onSubmit()
            },
            enabled = registerResult !is RequestResult.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A1B9A),
                contentColor = Color.White
            )
        ) {
            if (registerResult is RequestResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Crear Cuenta")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onNavigateToBack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6A1B9A))
        ) {
            Text(text = "Volver")
        }

        Spacer(modifier = Modifier.height(15.dp))
    }

}
