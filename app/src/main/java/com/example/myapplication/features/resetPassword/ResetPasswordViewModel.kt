package com.example.myapplication.features.resetPassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.utils.ValidatedField

class ResetPasswordViewModel: ViewModel() {

    val password1 = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatorio"
            value.length < 6 -> "Debe tener mínimo 6 caracteres"
            else -> null
        }
    }

    val password2 = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "Debe tener mínimo 6 caracteres"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

}