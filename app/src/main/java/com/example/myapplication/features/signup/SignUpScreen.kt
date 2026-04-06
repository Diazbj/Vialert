package com.example.myapplication.features.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Gender

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(),
    navController: NavController? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isGenderMenuExpanded by remember { mutableStateOf(false) }
    
    val blackFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        focusedPlaceholderColor = Color.Black,
        unfocusedPlaceholderColor = Color.Black,
        focusedBorderColor = Color.Black,
        unfocusedBorderColor = Color.Black
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
            OutlinedTextField(
                value = viewModel.firstName.value,
                onValueChange = { viewModel.firstName.onChange(it) },
                label = { Text("Nombre") },
                placeholder = { Text("Ej: Juan") },
                isError = viewModel.firstName.error != null,
                supportingText = viewModel.firstName.error?.let { error -> { Text(error) } },
                colors = blackFieldColors,
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = viewModel.lastName.value,
                onValueChange = { viewModel.lastName.onChange(it) },
                label = { Text("Apellido") },
                placeholder = { Text("Ej: Pérez") },
                isError = viewModel.lastName.error != null,
                supportingText = viewModel.lastName.error?.let { error -> { Text(error) } },
                colors = blackFieldColors,
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.onChange(it) },
            label = { Text("Correo electrónico") },
            placeholder = { Text("correo@ejemplo.com") },
            isError = viewModel.email.error != null,
            supportingText = viewModel.email.error?.let { error -> { Text(error) } },
            colors = blackFieldColors,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = viewModel.userName.value,
            onValueChange = { viewModel.userName.onChange(it) },
            label = { Text("Nombre de usuario") },
            placeholder = { Text("@usuario123") },
            isError = viewModel.userName.error != null,
            supportingText = viewModel.userName.error?.let { error -> { Text(error) } },
            colors = blackFieldColors,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Contraseña") },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = viewModel.password.error != null,
            supportingText = viewModel.password.error?.let { error -> { Text(error) } },
            colors = blackFieldColors,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
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
                OutlinedTextField(
                    value = viewModel.selectedGender.value?.let { gender ->
                        gender.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " ")
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderMenuExpanded) },
                    isError = viewModel.gender.error != null,
                    supportingText = viewModel.gender.error?.let { error -> { Text(error) } },
                    colors = blackFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
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

            OutlinedTextField(
                value = viewModel.birthDate.value,
                onValueChange = { viewModel.birthDate.onChange(it) },
                label = { Text("F. Nacimiento") },
                placeholder = { Text("DD/MM/AAAA") },
                isError = viewModel.birthDate.error != null,
                supportingText = viewModel.birthDate.error?.let { error -> { Text(error) } },
                keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
                colors = blackFieldColors,
                singleLine = true,
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
                // viewModel.onSubmit()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A1B9A),
                contentColor = Color.White
            )
        ) {
            Text(text = "Crear Cuenta")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController?.popBackStack() },
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
