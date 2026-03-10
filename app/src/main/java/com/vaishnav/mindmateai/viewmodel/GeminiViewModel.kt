package com.vaishnav.mindmateai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.vaishnav.mindmateai.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel : ViewModel() {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    private val _responseState = MutableStateFlow<String>("")
    val responseState: StateFlow<String> = _responseState.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun resetState() {
        _responseState.value = ""
    }

    private fun generateWithPrompt(prompt: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _responseState.value = ""
            try {
                val response = generativeModel.generateContent(prompt)
                _responseState.value = response.text ?: "No response received"
            } catch (e: Exception) {
                _responseState.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun summarizeContent(content: String) {
        generateWithPrompt("Summarize this academic content clearly for engineering students:\n$content")
    }

    fun generateMCQ(content: String) {
        val prompt = """
            Generate exactly 5 multiple choice questions from the following text. 
            Output the result STRICTLY as a JSON array. Do NOT include markdown blocks like ```json or any other text.
            The structure must be exactly:
            [
              {
                "question": "Question text here?",
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "correctAnswerIndex": 0
              }
            ]
            
            Text:
            $content
        """.trimIndent()
        generateWithPrompt(prompt)
    }
    
    fun generateQuestions(content: String) {
        generateWithPrompt("Generate 10-15 important exam questions from this content:\n$content")
    }
    
    fun explainKannada(content: String) {
        generateWithPrompt("Explain this concept clearly in Kannada language:\n$content")
    }
    
    fun realWorldAnalogy(content: String) {
        generateWithPrompt("Explain this concept using practical, real world analogies:\n$content")
    }
    
    fun explainCode(content: String) {
        generateWithPrompt("Explain this programming code step-by-step for beginners:\n$content")
    }

    // CHATBOT LOGIC
    private val chat = generativeModel.startChat()
    
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("Hi there! I'm MindMate AI, your personal study buddy. How can I help you today?", false))
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow<Boolean>(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun sendChatMessage(message: String) {
        viewModelScope.launch {
            _chatMessages.value = _chatMessages.value + ChatMessage(text = message, isUser = true)
            _isChatLoading.value = true

            try {
                val response = chat.sendMessage(message)
                val aiReply = response.text ?: "I am having trouble replying right now."
                _chatMessages.value = _chatMessages.value + ChatMessage(text = aiReply, isUser = false)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(text = "Error: ${e.localizedMessage}", isUser = false, isError = true)
            } finally {
                _isChatLoading.value = false
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)