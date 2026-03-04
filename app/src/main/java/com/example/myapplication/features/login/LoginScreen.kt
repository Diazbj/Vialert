package com.example.myapplication.features.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        )
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.vialert),
            contentDescription = "Logo"
        )

        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.onChange(it) },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            isError = viewModel.email.error != null,
            supportingText = viewModel.email.error?.let { error ->
                { Text(error) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.onChange(it) },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.password.error != null,
            supportingText = viewModel.password.error?.let { error ->
                { Text(error) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                Toast.makeText(context, "Login exitoso", Toast.LENGTH_LONG).show()
                viewModel.resetForm()
            },
            enabled = viewModel.isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}