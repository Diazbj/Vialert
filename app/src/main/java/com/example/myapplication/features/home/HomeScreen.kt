package com.example.myapplication.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.core.navigation.Login
import com.example.myapplication.core.navigation.SignUp
import com.example.myapplication.R

@Composable
fun HomeScreen(
    navController: NavController? = null, onNavigateToRegister: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // fondo claro como mockup
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(id = R.drawable.vialert),
            contentDescription = "Logo"
        )

        Text(
            text = "Reporta problemas viales en tiempo real y construye una ciudad más segura para todos",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(90.dp))

        // Botón Registrarme
        Button(
            onClick = {
                navController?.navigate(SignUp)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A1B9A),
                contentColor = Color.White
            )
        ) {
            Text(text = "Registrarme")
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Botón Iniciar sesión
        OutlinedButton(
            onClick = {
                navController?.navigate(Login)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6A1B9A))
        ) {
            Text(text = "Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "VIGILANCIA CIUDADANA INTELIGENTE.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(15.dp))
    }
}