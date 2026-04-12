package com.example.myapplication.features.reportdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NewReportSubmitState {
	IDLE,
	LOADING,
	SUCCESS,
	ERROR
}

data class NewReportUiState(
	val title: String = "",
	val category: String? = null,
	val description: String = "",
	val location: Location? = null,
	val address: String = "Ubicacion no seleccionada",
	val images: List<String> = emptyList(),
	val categories: List<String> = listOf(
		"Infraestructura",
		"Seguridad Vial",
		"Movilidad",
		"Alumbrado"
	),
	val submitState: NewReportSubmitState = NewReportSubmitState.IDLE,
	val message: String? = null
) {
	val isFormValid: Boolean
		get() = title.isNotBlank() && !category.isNullOrBlank() && description.isNotBlank() && location != null
}

class NewReportViewModel : ViewModel() {

	private val _uiState = MutableStateFlow(NewReportUiState())
	val uiState: StateFlow<NewReportUiState> = _uiState.asStateFlow()

	fun updateTitle(value: String) {
		_uiState.update { it.copy(title = value, message = null) }
	}

	fun updateCategory(category: String) {
		_uiState.update { it.copy(category = category, message = null) }
	}

	fun updateDescription(value: String) {
		_uiState.update { it.copy(description = value, message = null) }
	}

	fun updateLocation(location: Location, address: String) {
		_uiState.update {
			it.copy(
				location = location,
				address = address,
				message = null
			)
		}
	}

	fun addImage(image: String) {
		_uiState.update { state ->
			state.copy(images = state.images + image, message = null)
		}
	}

	fun removeImage(image: String) {
		_uiState.update { state ->
			state.copy(images = state.images.filterNot { it == image }, message = null)
		}
	}

	fun createReport() {
		val current = _uiState.value
		if (!current.isFormValid) {
			_uiState.update {
				it.copy(
					submitState = NewReportSubmitState.ERROR,
					message = "Completa todos los campos obligatorios"
				)
			}
			return
		}

		viewModelScope.launch {
			_uiState.update {
				it.copy(
					submitState = NewReportSubmitState.LOADING,
					message = null
				)
			}

			_uiState.update {
				it.copy(
					submitState = NewReportSubmitState.SUCCESS,
					message = "Reporte publicado exitosamente"
				)
			}
		}
	}

	fun clearMessage() {
		_uiState.update { it.copy(message = null) }
	}
}