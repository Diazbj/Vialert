package com.example.myapplication.core.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.myapplication.core.utils.RequestResult

@Composable
fun ResultDialog(
    result: RequestResult?,
    onDismiss: () -> Unit
) {
    if (result == null || result is RequestResult.Loading) return

    val title = when (result) {
        is RequestResult.Success -> "Éxito"
        is RequestResult.Failure -> "Error"
        else -> ""
    }

    val message = when (result) {
        is RequestResult.Success -> result.message
        is RequestResult.Failure -> result.errorMessage
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Aceptar")
            }
        }
    )
}
