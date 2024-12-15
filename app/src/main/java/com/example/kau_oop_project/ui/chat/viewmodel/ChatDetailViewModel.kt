package com.example.kau_oop_project.ui.chat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.ChatMessage
import com.example.kau_oop_project.repository.ChatDetailRepository
import kotlinx.coroutines.launch

class ChatDetailViewModel : ViewModel() {
    private val repository = ChatDetailRepository()

    // 메시지 리스트 상태
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    // 메시지 전송 상태
    private val _messageStatus = MutableLiveData<Boolean>()
    val messageStatus: LiveData<Boolean> get() = _messageStatus

    /**
     * 특정 채팅방의 메시지를 로드
     */
    fun loadMessages(chatRoomId: String) {
        viewModelScope.launch {
            repository.getMessages(chatRoomId).collect { messageList ->
                println("ViewModel Messages Loaded: $messageList") // 로그 추가
                _messages.value = messageList
            }
        }
    }

    /**
     * 메시지 전송
     */
    fun sendMessage(chatRoomId: String, senderId: String, message: String) {
        Log.d("ChatDetailViewModel", "sendMessage called with chatRoomId=$chatRoomId, senderId=$senderId, message=$message")

        if (message.isBlank()) {
            _messageStatus.value = false
            return
        }

        val chatMessage = ChatMessage(
            id = "",
            chatRoomId = chatRoomId,
            senderId = senderId,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val result = repository.sendMessage(chatMessage)
            _messageStatus.value = result.isSuccess
        }
    }
}