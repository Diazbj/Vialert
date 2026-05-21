package com.example.myapplication.features.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.theme.VialertPurple

@Composable
fun RegistrationSuccessScreen(
    onNavigateToLogin: () -> Unit
) {
    val purple = VialertPurple
    val lightPurple = purple.copy(alpha = 0.10f)
    val mediumPurple = purple.copy(alpha = 0.18f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon area
        Box(modifier = Modifier.size(220.dp)) {
            // Outer decorative ring
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = lightPurple
            ) {}
            // Middle ring
            Surface(
                modifier = Modifier.size(178.dp).align(Alignment.Center),
                shape = CircleShape,
                color = mediumPurple
            ) {}
            // Main purple circle
            Surface(
                modifier = Modifier.size(130.dp).align(Alignment.Center),
                shape = CircleShape,
                color = purple
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // White inner circle with checkmark
                    Surface(
                        modifier = Modifier.size(70.dp),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = purple,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }
            }
            // Yellow star badge — top-right of the main circle
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-28).dp, y = 30.dp),
                shape = CircleShape,
                color = Color(0xFFFBBF24)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¡Registro completado!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Bienvenido a la comunidad de vigilancia ciudadana. Ya puedes empezar a reportar y mejorar tu barrio.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Info card 1 — cuenta activa
        InfoCard(
            icon = Icons.Default.Shield,
            title = "Cuenta activa",
            subtitle = "Perfil verificado por Vialert"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Info card 2 — verificar correo
        InfoCard(
            icon = Icons.Default.Email,
            title = "Verifica tu correo",
            subtitle = "Te enviamos un enlace a tu bandeja de entrada"
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Ir a inicio  →",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Vialert",
            fontSize = 13.sp,
            color = purple.copy(alpha = 0.35f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = CircleShape, color = VialertPurple.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = VialertPurple, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
