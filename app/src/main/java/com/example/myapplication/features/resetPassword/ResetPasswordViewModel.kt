package com.example.myapplication.features.resetPassword

import androidx.lifecycle.ViewModel
import com.example.myapplication.core.utils.ValidatedField
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : ViewModel() {

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
            !(value == password1.value) -> "Las contraseñas deben de ser iguales"
            else -> null
        }
    }

    val isFormValid: Boolean get() = password1.isValid && password2.isValid

}
