package com.example.myapplication.features.forgetPassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.core.utils.ValidatedField
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _forgetPasswordResult = MutableStateFlow<RequestResult?>(null)
    val forgetPasswordResult: StateFlow<RequestResult?> = _forgetPasswordResult.asStateFlow()

    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = email.isValid

    fun onSubmit() {
        if (isFormValid) {
            viewModelScope.launch {
                _forgetPasswordResult.value = RequestResult.Loading
                
                val user = userRepository.findByEmail(email.value)
                
                if (user != null) {
                    _forgetPasswordResult.value = RequestResult.Success("Enlace de recuperación enviado a ${email.value}")
                } else {
                    _forgetPasswordResult.value = RequestResult.Failure("No existe ninguna cuenta asociada a este correo electrónico")
                }
            }
        }
    }

    fun clearResult() {
        _forgetPasswordResult.value = null
    }

    fun resetForm() {
        email.reset()
        clearResult()
    }
}
