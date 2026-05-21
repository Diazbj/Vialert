package com.example.myapplication.features.signup

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.core.utils.ValidatedField
import com.example.myapplication.domain.model.Gender
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerResult = MutableStateFlow<RequestResult?>(null)
    val registerResult: StateFlow<RequestResult?> = _registerResult.asStateFlow()

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
            value.length < 6 -> "Mínimo 6 caracteres"
            !value.any { it.isUpperCase() } -> "Debe incluir al menos 1 mayúscula"
            !value.any { it.isLowerCase() } -> "Debe incluir al menos 1 minúscula"
            !value.any { it.isDigit() } -> "Debe incluir al menos 1 número"
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

    fun onSubmit() {
        if (isFormValid) {
            viewModelScope.launch {
                _registerResult.value = RequestResult.Loading
                if (userRepository.findByEmail(email.value) != null) {
                    _registerResult.value = RequestResult.Failure("El email ya está registrado")
                    return@launch
                }
                _registerResult.value = try {
                    userRepository.create(
                        User(
                            id = UUID.randomUUID().toString(),
                            firstName = firstName.value,
                            lastName = lastName.value,
                            email = email.value,
                            userName = userName.value,
                            password = password.value,
                            gender = selectedGender.value,
                            birthDate = birthDate.value,
                            score = 0,
                            profilePictureUrl = ""
                        )
                    )
                    RequestResult.Success("Usuario registrado exitosamente")
                } catch (e: Exception) {
                    RequestResult.Failure(e.message ?: "Error registrando al usuario")
                }
            }
        }
    }

    fun clearResult() {
        _registerResult.value = null
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
        gender.reset()
        birthDate.reset()
        hasAcceptedTerms.value = false
        clearResult()
    }

}
