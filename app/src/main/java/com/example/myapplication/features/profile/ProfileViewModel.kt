package com.example.myapplication.features.profile

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    // Perfil basico
    val name = mutableStateOf("Carlos Mendoza")
    val title = mutableStateOf("Héroe Comunitario")
    val level = mutableStateOf("Oro")
    val points = mutableStateOf(1250)
    val maxPoints = mutableStateOf(1500)
    val nextLevel = mutableStateOf("Platino")

    // Estadísticas
    val stats = mutableStateOf(
        ProfileStats(
            active = 12,
            completed = 45,
            verified = 38
        )
    )

    // Logros
    val logros = mutableStateListOf(
        Achievement("Primer Reporte", "trophy", false),
        Achievement("10 Verificados", "shield", false),
        Achievement("Racha 7 días", "fire", false),
        Achievement("Colaborador", "handshake", false),
        Achievement("Guardián Urbano", "lock", true),
        Achievement("Influencer Vial", "lock", true)
    )

    val isEditing = mutableStateOf(false)

    fun toggleEditing() {
        isEditing.value = !isEditing.value
    }

    fun onLogout() {
        // Implementar lógica de cierre de sesión
    }
}

data class ProfileStats(
    val active: Int,
    val completed: Int,
    val verified: Int
)

data class Achievement(
    val title: String,
    val iconType: String,
    val isLocked: Boolean
)
