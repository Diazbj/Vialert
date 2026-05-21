package com.example.myapplication.features.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.service.GeminiAiService
import com.google.ai.client.generativeai.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean
)

@HiltViewModel
class VirtualAssistantViewModel @Inject constructor(
    private val geminiAiService: GeminiAiService
) : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(ChatMessage("Hola, soy el asistente de Vialert. ¿En qué puedo ayudarte hoy?", isFromUser = false))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var chat: Chat = geminiAiService.createAssistantChat()

    fun onInputChanged(text: String) {
        _inputText.update { text }
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank() || _isLoading.value) return

        _messages.update { it + ChatMessage(text, isFromUser = true) }
        _inputText.update { "" }
        _isLoading.update { true }

        viewModelScope.launch {
            val response = geminiAiService.sendChatMessage(chat, text)
            _messages.update { it + ChatMessage(response, isFromUser = false) }
            _isLoading.update { false }
        }
    }
}
