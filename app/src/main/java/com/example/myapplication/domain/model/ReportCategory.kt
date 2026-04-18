package com.example.myapplication.domain.model

import androidx.compose.ui.graphics.Color

enum class ReportCategory(
    val displayName: String,
    val color: Color
) {
    INFRAESTRUCTURA("Infraestructura", Color(0xFFD32F2F)), // Rojo
    SEGURIDAD_VIAL("Seguridad Vial", Color(0xFF1976D2)),  // Azul
    MOVILIDAD("Movilidad", Color(0xFF388E3C)),           // Verde
    ALUMBRADO("Alumbrado", Color(0xFFFBC02D))            // Amarillo/Dorado
}
