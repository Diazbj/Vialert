package com.example.myapplication.features.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.utils.ValidatedField

class LoginViewModel : ViewModel() {

    private val defaultEmail = "usuario@vialert.com"
    private val defaultPassword = "vialert123"

    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    val password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "Debe tener mínimo 6 caracteres"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = email.isValid && password.isValid

    var shouldShowInvalidCredentialsDialog by mutableStateOf(false)
        private set

    fun login(): String {
        return if (isFormValid) {
            "Login exitoso"
        } else {
            "Formulario inválido"
        }
    }

    fun resetForm() {
        email.reset()
        password.reset()
    }

    fun forgetPassword(){

    }

    fun onPasswordChanged(newPassword: String) {
        password.onChange(newPassword)
    }

    fun loginFunction(): Boolean {
        val credentialsMatch =
            email.value == defaultEmail && password.value == defaultPassword

        val isLoginSuccessful = isFormValid && credentialsMatch
        shouldShowInvalidCredentialsDialog = !isLoginSuccessful

        return isLoginSuccessful
    }

    fun dismissInvalidCredentialsDialog() {
        shouldShowInvalidCredentialsDialog = false
    }

}