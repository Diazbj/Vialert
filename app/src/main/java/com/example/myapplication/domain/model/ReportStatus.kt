package com.example.myapplication.domain.model

import androidx.compose.ui.graphics.Color

enum class ReportStatus(
    val displayName: String,
    val color: Color
) {
    PENDING("Pendiente", Color(0xFFFFA000)),      // Ámbar/Naranja
    IN_PROGRESS("Verificado", Color(0xFF1976D2)),  // Azul
    RESOLVED("Resuelto", Color(0xFF388E3C)),        // Verde
    DELETED("Eliminado", Color(0xFFD32F2F))         // Rojo
}
