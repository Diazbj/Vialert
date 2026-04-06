package com.example.myapplication.features.resetPassword

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ResetPassword(
    viewModel: ResetPasswordViewModel = viewModel(),
    navController: NavController? = null
){

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = CenterVertically)
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.vialert),
            contentDescription = "Logo"
        )

        Text(
            text = "Actualice su contraseña",
            fontSize = 26.sp,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = viewModel.password1.value,
            onValueChange = { viewModel.password1.onChange(it) },
            label = { Text("Ingrese su nueva contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.password1.error != null,
            supportingText = viewModel.password1.error?.let { error ->
                { Text(error) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.password2.value,
            onValueChange = { viewModel.password2.onChange(it) },
            label = { Text("Verifique su contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.password2.error != null,
            supportingText = viewModel.password2.error?.let { error ->
                { Text(error) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                Toast.makeText(context, "Contraseña Actualizada", Toast.LENGTH_SHORT).show()
            },
            enabled = viewModel.isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar contraseña")
        }
    }

}