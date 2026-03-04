package com.example.myapplication.features.forgetPassword

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ForgetPassword(
    viewModel: ForgetPasswordViewModel = viewModel()
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
            text = "Recuperar Contraseña",
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.email.value, // Estado del campo de email desde el ViewModel
            onValueChange = { viewModel.email.onChange(it) }, // Actualiza el estado en el ViewModel
            label = {
                Text(text = "Correo Electrónico")
            },
            isError = viewModel.email.error != null, // Se muestra el borde rojo si hay error
            supportingText = viewModel.email.error?.let { error ->
                { Text(text = error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Button(
            onClick = {
                Toast.makeText(context, "Link enviado", Toast.LENGTH_SHORT).show()
            },
            enabled = viewModel.isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar link")
        }
    }

}
