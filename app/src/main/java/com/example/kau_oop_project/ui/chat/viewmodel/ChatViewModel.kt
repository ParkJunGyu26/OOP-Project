package com.example.kau_oop_project.ui.chat.viewmodel

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
    private val repository = ChatRepository()

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
            if (userId != null) {
                repository.getChatRooms(userId).collect { rooms ->
                    _chatRooms.value = rooms
                    _operationResult.value = true
                }
            } else {
                _chatRooms.value = emptyList()
                _operationResult.value = false
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
            _operationResult.value = result
        }
    }

    /**
     * 채팅방 생성
     */
    fun createChatRoom(chatRoom: ChatRoom?) {
        if (chatRoom == null) {
            _operationResult.value = false
            return
        }

        viewModelScope.launch {
            val result = repository.createChatRoom(chatRoom)
            _operationResult.value = result != null
        }
    }
} 