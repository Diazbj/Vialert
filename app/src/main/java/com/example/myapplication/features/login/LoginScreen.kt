package com.example.myapplication.features.login

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import kotlin.math.log

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreen(){

    Log.e("LoginScreen","Recomposicion")
    var localContext =LocalContext.current
    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(id = R.drawable.vialert),
            contentDescription = "Logo"
        )

        OutlinedTextField(
            label = {
                Text(
                    text = "Correo electronico"
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "password"
                )
            },
            value =email,
            onValueChange = {
            }
        )

        OutlinedTextField(
            label = {
                Text(
                    text = "Contraseña"
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "password"
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            value = password,
            onValueChange = {
                password=it
            }
        )

        Button(
            onClick = {

                if(email=="diaz.jordy@hotmail.com" && password == "123") {
                    Toast.makeText(localContext, "Datos Validos", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(localContext, "Datos Invalidos", Toast.LENGTH_LONG).show()
                }
            }
        ) {
            Text(
                text = "Iniciar sesión"
            )

        }
    }

}