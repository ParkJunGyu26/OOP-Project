package com.example.kau_oop_project.data.model.chat

data class ChatRoom(
    val id: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0
)

data class ChatMessage(
    val id: String = "",
    val chatRoomId: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    val imageUrl: String? = null
)