package com.example.kau_oop_project.data.model

data class ChatRoom(
    val id: String = "",
    val participants: List<Participant> = emptyList(),
    var lastMessage: String = "",
    var lastMessageTime: Long = 0L,
)

data class Participant(
    val email: String = "",
    val name: String = ""
)

data class ChatMessage(
    val id: String = "",
    val chatRoomId: String = "",
    val senderId: String = "",
    var message: String = "", // 메세지 수정 가능한 상황 고려
    val timestamp: Long = 0,
    val imageUrl: String? = null
)

fun isValidId(id: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9/.$#\\[\\]]+$") // 유효한 문자 패턴
    return regex.matches(id)
}