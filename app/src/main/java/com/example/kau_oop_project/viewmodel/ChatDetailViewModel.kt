package com.example.kau_oop_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kau_oop_project.repository.ChatDetailRepository

class ChatDetailViewModel : ViewModel() {
    private val repository = ChatDetailRepository()

    // 메시지 상태
    private val _messageStatus = MutableLiveData<Boolean>(false)
    val messageStatus: LiveData<Boolean> get() = _messageStatus

    // 메시지 유효성 검사
    fun messageValid(message: String) {
        updateMessageStatus(message.isEmpty())
    }

    // 메시지 전송 함수 추가
    fun sendMessage(message: String) {
        // 메시지 전송 로직
        // 추후 repository를 통해 실제 전송 구현
        updateMessageStatus(true)
    }

    private fun updateMessageStatus(status: Boolean) {
        _messageStatus.value = status
    }
}