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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            fontSize = 26.sp,
            color = Color.DarkGray
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
