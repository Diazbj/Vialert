package com.example.myapplication.features.forgetPassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.core.navigation.Login

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ForgetPassword(
    viewModel: ForgetPasswordViewModel = viewModel(),
    navController: NavController? = null
){

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
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.forgetpassword),
            contentDescription = "Logo",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF6A1B9A),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No te preocupes, introduce tu correo electrónico y te enviaremos un enlace para restablecerla",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

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
            Text(text = "Enviar enlace")
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
            Text(text = "Volver al Inicio de Sesión")
        }

    }
}