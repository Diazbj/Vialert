package com.example.myapplication.features.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.myapplication.core.navigation.ForgetPassword
import com.example.myapplication.core.navigation.HomeUser

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    navController: NavController? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

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
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bienvenido de nuevo",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF6A1B9A),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Tu seguridad y la de tu comunidad es nuestra prioridad",
            fontSize = 16.sp,
            color = Color.Gray
        )

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

        Text(
            text = "Olvide mi contraseña",
            color = Color(0xFF6A1B9A),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    navController?.navigate(ForgetPassword)
                }
        )

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

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (viewModel.loginFunction()) {
                    navController?.navigate(HomeUser)
                }
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
            Text(text = "Iniciar Sesión")
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

    if (viewModel.shouldShowInvalidCredentialsDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissInvalidCredentialsDialog,
            title = {
                Text(text = "Datos inválidos")
            },
            text = {
                Text(text = "El correo electrónico o la contraseña no coinciden con los valores esperados.")
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissInvalidCredentialsDialog) {
                    Text(text = "Aceptar")
                }
            }
        )
    }
}