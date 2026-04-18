package com.example.myapplication.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusCategoryChip(
    text: String,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    // Generamos un fondo suave basado en el color base
    val backgroundColor = baseColor.copy(alpha = 0.2f)
    // El texto usará el color base original para ser más intenso (u oscuro)
    val textColor = baseColor.copy(alpha = 1.0f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
