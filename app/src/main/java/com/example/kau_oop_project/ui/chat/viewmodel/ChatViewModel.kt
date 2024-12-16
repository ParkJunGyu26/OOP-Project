package com.example.kau_oop_project.ui.chat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kau_oop_project.data.model.ChatMessage
import com.example.kau_oop_project.data.model.ChatRoom
import com.example.kau_oop_project.repository.ChatRepository
import kotlinx.coroutines.launch

/**
 * 채팅 관련 기능을 처리하는 ViewModel
 */
class ChatViewModel : ViewModel() {
    private val repository = ChatRepository<Any?>()

    // 채팅방 목록 상태
    private val _chatRooms = MutableLiveData<List<ChatRoom>>()
    val chatRooms: LiveData<List<ChatRoom>> get() = _chatRooms

    // 메시지 목록 상태
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    // 작업 결과 상태
    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    /**
     * 채팅방 목록 로드
     */
    fun loadChatRooms(userId: String?) {
        viewModelScope.launch {
            val result = repository.getChatRooms(userId)
            if (result.isSuccess) {
                _chatRooms.value = result.getOrNull() ?: emptyList()
            } else {
                // 에러 처리 로직 추가
            }
        }
    }

    /**
     * 메시지 목록 로드
     */
    fun loadMessages(chatRoomId: String?) {
        viewModelScope.launch {
            if (chatRoomId != null) {
                repository.getMessages(chatRoomId).collect { messageList ->
                    _messages.value = messageList
                    _operationResult.value = true
                }
            } else {
                _messages.value = emptyList()
                _operationResult.value = false
            }
        }
    }

    /**
     * 메시지 전송
     */
    fun sendMessage(message: ChatMessage?) {
        if (message == null) {
            _operationResult.value = false
            return
        }

        viewModelScope.launch {
            val result = repository.sendMessage(message)
            _operationResult.value = if (result.getOrDefault(false) is Boolean) {
                result.getOrDefault(false) as Boolean
            } else {
                false
            }
        }
    }

    /**
     * 채팅방 생성
     */
    fun createChatRoom(chatRoom: ChatRoom) {
        viewModelScope.launch {
            val result = repository.createChatRoom(chatRoom) // ChatRoom 객체를 전달
            if (result.isSuccess) {
                _operationResult.value = true
            } else {
                Log.e("ChatViewModel", "채팅방 생성 실패: ${result.exceptionOrNull()?.message}")
                _operationResult.value = false
            }
        }
    }
} 