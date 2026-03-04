package com.example.myapplication.features.forgetPassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.utils.ValidatedField

class ForgetPasswordViewModel: ViewModel() {

    val email = ValidatedField(""){ value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email valido"
            else -> null
        }
    }
    val isFormValid: Boolean
        get() = email.isValid

}