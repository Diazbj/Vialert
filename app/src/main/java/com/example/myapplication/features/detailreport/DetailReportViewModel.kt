package com.example.myapplication.features.detailreport

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myapplication.core.navigation.ReportDetail
import com.example.myapplication.data.datastore.SessionDataStore
import com.example.myapplication.domain.model.Comment
import com.example.myapplication.domain.model.Report
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

    private val reportId = savedStateHandle.toRoute<ReportDetail>().reportId

    private val _uiState = MutableStateFlow(DetailReportUiState())
    val uiState: StateFlow<DetailReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
        observeComments()
    }

    private fun loadReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val report = reportRepository.getById(reportId)
            _uiState.update { it.copy(report = report, isLoading = false) }
        }
    }

    private fun observeComments() {
        viewModelScope.launch {
            commentRepository.getByReportId(reportId).collect { comments ->
                val namesMap = comments.map { it.userId }.distinct().associateWith { id ->
                    userRepository.getById(id)?.let { "${it.firstName} ${it.lastName}".trim() } ?: "Usuario"
                }
                _uiState.update { it.copy(comments = comments, userNames = namesMap) }
            }
        }
    }

    fun onCommentTextChanged(text: String) {
        _uiState.update { it.copy(commentText = text) }
    }

    fun addComment() {
        val text = _uiState.value.commentText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val userId = sessionDataStore.sessionFlow.firstOrNull()?.userId ?: "anonymous"
                commentRepository.create(
                    Comment(
                        id = UUID.randomUUID().toString(),
                        reportId = reportId,
                        userId = userId,
                        content = text,
                        createdAt = LocalDateTime.now()
                    )
                )
                _uiState.update { it.copy(commentText = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al agregar comentario") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
