package com.example.myapplication.features.detailreport

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myapplication.core.navigation.ReportDetail
import com.example.myapplication.core.utils.RequestResult
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.domain.model.Comment
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.CommentRepository
import com.example.myapplication.domain.repository.ReportRepository
import com.example.myapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

data class DetailReportUiState(
    val report: Report? = null,
    val comments: List<Comment> = emptyList(),
    val userNames: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val commentText: String = "",
    val error: String? = null
)

@HiltViewModel
class DetailReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reportRoute = savedStateHandle.toRoute<ReportDetail>()
    private val reportId = reportRoute.reportId

    private val _uiState = MutableStateFlow(DetailReportUiState())
    val uiState: StateFlow<DetailReportUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val report = reportRepository.getById(reportId)
                val comments = commentRepository.getByReportId(reportId)
                
                // Cargar nombres de usuarios para los comentarios
                val userIds = comments.map { it.userId }.distinct()
                val namesMap = userIds.associateWith { id ->
                    userRepository.getById(id)?.firstName ?: "Usuario $id"
                }

                _uiState.update { 
                    it.copy(
                        report = report, 
                        comments = comments.sortedByDescending { c -> c.createdAt }, 
                        userNames = namesMap,
                        isLoading = false 
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onCommentTextChanged(text: String) {
        _uiState.update { it.copy(commentText = text) }
    }

    fun addComment() {
        val currentText = _uiState.value.commentText
        if (currentText.isBlank()) return

        viewModelScope.launch {
            try {
                val session = sessionDataStore.sessionFlow.firstOrNull()
                val userId = session?.userId ?: "anonymous"

                val newComment = Comment(
                    id = UUID.randomUUID().toString(),
                    reportId = reportId,
                    userId = userId,
                    content = currentText,
                    createdAt = LocalDateTime.now()
                )

                commentRepository.create(newComment)
                
                // Actualizar comentarios y nombres
                val updatedComments = commentRepository.getByReportId(reportId)
                val userIds = updatedComments.map { it.userId }.distinct()
                val namesMap = userIds.associateWith { id ->
                    userRepository.getById(id)?.firstName ?: "Usuario $id"
                }

                _uiState.update { 
                    it.copy(
                        comments = updatedComments.sortedByDescending { c -> c.createdAt },
                        userNames = namesMap,
                        commentText = "" 
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al agregar comentario") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
