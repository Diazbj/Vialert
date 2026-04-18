package com.example.myapplication.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.core.utils.ValidatedField
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
): ViewModel() {
    private val _loginResult = MutableStateFlow<RequestResult?>(null)
    val loginResult: StateFlow<RequestResult?> = _loginResult.asStateFlow()

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

    fun onPasswordChanged(newPassword: String) {
        password.onChange(newPassword)
    }

    fun loginFunction() {
        if (!isFormValid) return

        viewModelScope.launch {
            _loginResult.value = RequestResult.Loading
            try {
                val user = userRepository.findByEmailAndPassword(email.value, password.value)
                if (user != null) {
                    sessionDataStore.saveSession(user.id, user.role)
                    _loginResult.value = RequestResult.Success("Bienvenido, ${user.firstName}")
                } else {
                    _loginResult.value = RequestResult.Failure("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _loginResult.value = RequestResult.Failure("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun clearResult() {
        _loginResult.value = null
    }
}
