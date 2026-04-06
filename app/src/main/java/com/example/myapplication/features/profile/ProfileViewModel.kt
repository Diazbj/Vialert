package com.example.myapplication.features.profile

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.utils.ValidatedField

class ProfileViewModel : ViewModel() {

    val name = ValidatedField("Juan Pérez") { value ->
        when {
            value.isEmpty() -> "El nombre es obligatorio"
            value.length < 2 -> "El nombre debe tener al menos 2 caracteres"
            else -> null
        }
    }

    val email = ValidatedField("juan@example.com") { value ->
        when {
            value.isEmpty() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un correo válido"
            else -> null
        }
    }

    val phone = ValidatedField("") { value ->
        when {
            value.isNotEmpty() && value.length < 8 -> "Número de teléfono inválido"
            else -> null
        }
    }

    val isEditing = mutableStateOf(false)

    val isFormValid: Boolean
        get() = name.isValid && email.isValid && phone.isValid

    fun toggleEditing() {
        isEditing.value = !isEditing.value
    }

    fun onSave(): String {
        return if (isFormValid) {
            isEditing.value = false
            "Perfil actualizado"
        } else {
            "Formulario inválido"
        }
    }
}
