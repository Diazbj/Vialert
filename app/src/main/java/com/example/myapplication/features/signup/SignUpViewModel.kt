package com.example.myapplication.features.signup

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.utils.ValidatedField
import com.example.myapplication.domain.model.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    val firstName = ValidatedField("") { value ->
        if (value.isEmpty()) "El nombre es obligatorio" else null
    }

    val lastName = ValidatedField("") { value ->
        if (value.isEmpty()) "El apellido es obligatorio" else null
    }

    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un correo válido"
            else -> null
        }
    }

    val userName = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El nombre de usuario es obligatorio"
            value.length < 4 -> "Debe tener al menos 4 caracteres"
            value.contains(" ") -> "No debe contener espacios"
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

    private val _passwordStrengthLevel = mutableStateOf(0)
    val passwordStrengthLevel: State<Int> = _passwordStrengthLevel

    private val _passwordStrengthText = mutableStateOf("Muy Débil")
    val passwordStrengthText: State<String> = _passwordStrengthText

    fun onPasswordChanged(newPassword: String) {
        password.onChange(newPassword)
        calculatePasswordStrength(newPassword)
    }

    private fun calculatePasswordStrength(password: String) {
        var level = 0
        if (password.length >= 10) level++
        if (password.any { it.isDigit() }) level++
        if (password.any { it.isUpperCase() }) level++
        if (password.any { it.isLetterOrDigit().not() }) level++

        _passwordStrengthLevel.value = level
        _passwordStrengthText.value = when (level) {
            1 -> "Débil"
            2 -> "Media"
            3 -> "Fuerte"
            4 -> "Muy Fuerte"
            else -> "Muy Débil"
        }
    }

    private val _selectedGender = mutableStateOf<Gender?>(null)
    val selectedGender: State<Gender?> = _selectedGender

    val gender = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "Selecciona un género"
            else -> null
        }
    }

    fun onGenderSelected(gender: Gender) {
        _selectedGender.value = gender
        this.gender.onChange(gender.name)
    }

    val birthDate = ValidatedField("") { value ->
        val birthDatePattern = Regex("^\\d{2}/\\d{2}/\\d{4}$")
        when {
            value.isEmpty() -> "La fecha de nacimiento es obligatoria"
            !birthDatePattern.matches(value) -> "Usa el formato DD/MM/AAAA"
            else -> null
        }
    }

    val hasAcceptedTerms = mutableStateOf(false)

    val isFormValid: Boolean
        get() = firstName.isValid &&
            lastName.isValid &&
            email.isValid &&
            userName.isValid &&
            password.isValid &&
            gender.isValid &&
            birthDate.isValid &&
            hasAcceptedTerms.value

    fun onSubmit(): String {
        return if (isFormValid) {
            "Registro exitoso"
        } else {
            "Formulario inválido"
        }
    }

    fun onTermsAcceptanceChange(isAccepted: Boolean) {
        hasAcceptedTerms.value = isAccepted
    }

    fun resetForm() {
        firstName.reset()
        lastName.reset()
        email.reset()
        userName.reset()
        password.reset()
        _passwordStrengthLevel.value = 0
        _passwordStrengthText.value = "Muy Débil"
        _selectedGender.value = null
        gender.reset()
        birthDate.reset()
        hasAcceptedTerms.value = false
    }
}
