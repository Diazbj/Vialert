package com.example.myapplication.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

object AppTextFieldDefaults {
    @Composable
    fun blackFieldColors() = OutlinedTextFieldDefaults.colors(
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
}

@Composable
fun VialertTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        colors = AppTextFieldDefaults.blackFieldColors(),
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        readOnly = readOnly
    )
}

@Composable
fun VialertPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Contraseña",
    isError: Boolean = false,
    supportingText: String? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    VialertTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        isError = isError,
        supportingText = supportingText,
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}
