package com.example.kau_oop_project.data.model.chat

data class ChatRoom(
    val id: String = "",
    var participants: List<String> = listOf(),
    // var participants: Map<String, Boolean> = emptyMap(),
    var lastMessage: String = "",
    var lastMessageTime: Long = 0
)

data class ChatMessage(
    val id: String = "",
    val chatRoomId: String = "",
    val senderId: String = "",
    var message: String = "", // 메세지 수정 가능한 상황 고려
    val timestamp: Long = 0,
    val imageUrl: String? = null
)